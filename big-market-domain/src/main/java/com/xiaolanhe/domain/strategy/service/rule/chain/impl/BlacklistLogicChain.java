package com.xiaolanhe.domain.strategy.service.rule.chain.impl;

import com.xiaolanhe.domain.strategy.repository.IStrategyRepository;
import com.xiaolanhe.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.xiaolanhe.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 黑名单责任链
 *
 * @author xiaolanhe
 * @date 2024/9/21 20:33
 */

@Slf4j
@Component("rule_blacklist")
public class BlacklistLogicChain extends AbstractLogicChain{

    @Resource
    private IStrategyRepository repository;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-黑名单开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, getRuleModel());
        // 查询规则值配置
        String ruleValue = repository.queryStrategyRuleValue(strategyId, getRuleModel());
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);

        // 黑名单抽奖判断
        Set<String> userBlackIds = Arrays.stream(splitRuleValue[1].split(Constants.SPLIT)).collect(Collectors.toSet());
        if(!CollectionUtils.isEmpty(userBlackIds) && userBlackIds.contains(userId)){
            log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, getRuleModel(), awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(getRuleModel())
                    .build();
        }

        // 过滤其他责任链
        log.info("抽奖责任链-黑名单放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, getRuleModel());
        return getNext().logic(userId, strategyId);
    }

    @Override
    protected String getRuleModel() {
        return "rule_blacklist";
    }
}
