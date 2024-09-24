package com.xiaolanhe.domain.strategy.service.rule.chain.impl;

import com.xiaolanhe.domain.strategy.service.armory.IStrategyDispatch;
import com.xiaolanhe.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Description: 默认责任链
 *
 * @author xiaolanhe
 * @date 2024/9/21 20:32
 */

@Slf4j
@Component("default")
public class DefaultLoginChain extends AbstractLogicChain{

    @Resource
    protected IStrategyDispatch strategyDispatch;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, getRuleModel(), awardId);
        return DefaultChainFactory.StrategyAwardVO.builder()
                .logicModel(getRuleModel())
                .awardId(awardId)
                .build();
    }

    @Override
    protected String getRuleModel() {
        return "default";
    }
}
