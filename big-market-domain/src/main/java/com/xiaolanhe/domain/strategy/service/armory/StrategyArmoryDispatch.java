package com.xiaolanhe.domain.strategy.service.armory;

import com.xiaolanhe.domain.strategy.model.entity.StrategyAwardEntity;
import com.xiaolanhe.domain.strategy.model.entity.StrategyEntity;
import com.xiaolanhe.domain.strategy.model.entity.StrategyRuleEntity;
import com.xiaolanhe.domain.strategy.repository.IStrategyRepository;
import com.xiaolanhe.types.enums.ResponseCode;
import com.xiaolanhe.types.exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *@author: xiaolanhe
 *@createDate: 2024/9/11 22:41
 */

@Service
@Slf4j
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch{

    @Resource
    private IStrategyRepository repository;

    // 以空间换时间的思想，先将此策略id关联的奖品配置，生成一个概率查找表，再存入redis中
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 1. 查询策略配置
        List<StrategyAwardEntity> strategyAwards = repository.queryStrategyAwardListByStrategyId(strategyId);
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwards);

        // 2. 权重策略配置 - 适用于 rule_weight 权重规则配置
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight){
            return true;
        }
        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRule(strategyId, ruleWeight);
        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        Map<String, List<Integer>> ruleWightValues = strategyRuleEntity.getRuleWightValues();
        for(String key : ruleWightValues.keySet()){
            List<Integer> values = ruleWightValues.get(key);
            List<StrategyAwardEntity> strategyAwardsClone = new ArrayList<>(strategyAwards);
            strategyAwardsClone.removeIf(item -> !values.contains(item.getAwardId()));
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key), strategyAwardsClone);
        }

        return true;
    }

    private void assembleLotteryStrategy(String strategyId, List<StrategyAwardEntity> strategyAwards) {
        //  获取最小概率值
        BigDecimal minRate = strategyAwards.stream().map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        //  获取概率值总和
        BigDecimal totalRate = strategyAwards.stream().map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rateRange = totalRate.divide(minRate, 0, BigDecimal.ROUND_CEILING);

        //  生成策略奖品概率查找表「这里指需要在list集合中，存放上对应的奖品占位即可，占位越多等于概率越高」
        List<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for(StrategyAwardEntity award : strategyAwards){
            Integer awardId = award.getAwardId();
            BigDecimal awardRate = award.getAwardRate();
            // 计算出每个概率值需要存放到查找表的数量，循环填充
            for(int i = 0; i < rateRange.multiply(awardRate).setScale(0, BigDecimal.ROUND_CEILING).intValue(); i++){
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        Collections.shuffle(strategyAwardSearchRateTables);

        Map<Integer, Integer> shuffleStrategyAwardSearchRateTable = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTables.get(i));
        }

        // 存入redis
        repository.storeStrategyAwardSearchTable(strategyId, shuffleStrategyAwardSearchRateTable.size(), shuffleStrategyAwardSearchRateTable);
    }

    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param strategyId 策略ID
     * @return 抽奖结果
     */
    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = repository.getRateRange(strategyId);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(String.valueOf(strategyId), new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        int rateRange = repository.getRateRange(key);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(rateRange));
    }
}
