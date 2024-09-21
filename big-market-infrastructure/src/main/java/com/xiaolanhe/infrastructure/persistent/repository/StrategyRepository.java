package com.xiaolanhe.infrastructure.persistent.repository;

import com.xiaolanhe.domain.strategy.model.entity.StrategyAwardEntity;
import com.xiaolanhe.domain.strategy.model.entity.StrategyEntity;
import com.xiaolanhe.domain.strategy.model.entity.StrategyRuleEntity;
import com.xiaolanhe.domain.strategy.repository.IStrategyRepository;
import com.xiaolanhe.infrastructure.persistent.dao.IStrategyAwardDao;
import com.xiaolanhe.infrastructure.persistent.dao.IStrategyDao;
import com.xiaolanhe.infrastructure.persistent.dao.IStrategyRuleDao;
import com.xiaolanhe.infrastructure.persistent.po.Strategy;
import com.xiaolanhe.infrastructure.persistent.po.StrategyAward;
import com.xiaolanhe.infrastructure.persistent.po.StrategyRule;
import com.xiaolanhe.infrastructure.persistent.redis.IRedisService;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xiaolanhe.types.common.Constants.RedisKey.STRATEGY_AWARD_KEY;
import static com.xiaolanhe.types.common.Constants.RedisKey.STRATEGY_KEY;
import static com.xiaolanhe.types.common.Constants.RedisKey.STRATEGY_RATE_RANGE_KEY;
import static com.xiaolanhe.types.common.Constants.RedisKey.STRATEGY_RATE_TABLE_KEY;

/** 策略服务仓储实现
 *@author: xiaolanhe
 *@createDate: 2024/9/11 22:55
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyDao strategyDao;
    @Resource
    private IStrategyRuleDao strategyRuleDao;

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardListByStrategyId(Long strategyId) {
        String cacheKey = STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        if(!CollectionUtils.isEmpty(strategyAwardEntities)){
            return strategyAwardEntities;
        }
        // 从库中获取数据
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey, strategyAwardEntities);
        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchTable(String strategyId, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        // 1. 存储抽奖策略范围值
        redisService.setValue(STRATEGY_RATE_RANGE_KEY + strategyId, rateRange);
        // 2. 存储抽奖策略搜索表
        RMap<Integer, Integer> map = redisService.getMap(STRATEGY_RATE_TABLE_KEY + strategyId);
        map.putAll(shuffleStrategyAwardSearchRateTable);
        // RMap 是 Redission 提供的一个代理对象,当你对 RMap 进行任何操作（如 put, putAll, remove 等），这些操作会自动同步到 Redis 数据库中
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public int getRateRange(String key) {
        return redisService.getValue(STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public Integer getStrategyAwardAssemble(String strategyId, Integer rateKey) {
        return redisService.getFromMap(STRATEGY_RATE_TABLE_KEY + strategyId, rateKey);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        String cacheKey = STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        if(null != strategyEntity){
            return strategyEntity;
        }
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
        redisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleWeight) {
        StrategyRule strategyRuleReq = new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleWeight);
        StrategyRule strategyRuleRes = strategyRuleDao.queryStrategyRule(strategyRuleReq);
        return StrategyRuleEntity.builder()
                .strategyId(strategyRuleRes.getStrategyId())
                .awardId(strategyRuleRes.getAwardId())
                .ruleType(strategyRuleRes.getRuleType())
                .ruleModel(strategyRuleRes.getRuleModel())
                .ruleValue(strategyRuleRes.getRuleValue())
                .ruleDesc(strategyRuleRes.getRuleDesc())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        strategyRule.setRuleModel(ruleModel);
        return strategyRuleDao.queryStrategyRuleValue(strategyRule);
    }
}
