package com.xiaolanhe.domain.strategy.service.rule.chain.impl;

import com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity;
import com.xiaolanhe.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.xiaolanhe.domain.strategy.repository.IStrategyRepository;
import com.xiaolanhe.domain.strategy.service.armory.IStrategyDispatch;
import com.xiaolanhe.domain.strategy.service.rule.factory.DefaultLogicFilterFactory;
import com.xiaolanhe.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author xiaolanhe
 * @date 2024/9/21 20:43
 */

@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain{

    @Resource
    private IStrategyRepository repository;

    @Resource
    protected IStrategyDispatch strategyDispatch;

    // 根据用户ID查询用户抽奖消耗的积分值，暂时写死为固定的值。后续需要从数据库中查询。
    public Long userScore = 0L;

    @Override
    public Integer logic(String userId, Long strategyId) {
        log.info("规则过滤-权重范围 userId: {} strategyId: {} ", userId, strategyId);

        String ruleValue = repository.queryStrategyRuleValue(strategyId, getRuleModel());
        // 1. 根据用户ID查询用户抽奖消耗的积分值
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if(CollectionUtils.isEmpty(analyticalValueGroup)) {
            return null;
        }

        // 2. 转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys, Comparator.reverseOrder());

        Long minValue = analyticalSortedKeys.stream().filter(item -> userScore >= item).findFirst().orElse(null);

        // 3. 权重抽奖
        if(null != minValue) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, analyticalValueGroup.get(minValue).split(Constants.COLON)[0]);
            log.info("抽奖责任链-权重接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, getRuleModel(), awardId);
            return awardId;
        }

        // 4. 过滤其他责任链
        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, getRuleModel());
        return getNext().logic(userId, strategyId);
    }

    @Override
    protected String getRuleModel() {
        return "rule_weight";
    }

    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long, String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroups) {
            // 检查输入是否为空
            if (ruleValueKey == null || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }
            // 分割字符串以获取键和值
            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }
        return ruleValueMap;
    }
}
