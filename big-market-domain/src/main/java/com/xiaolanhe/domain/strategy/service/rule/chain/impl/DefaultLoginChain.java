package com.xiaolanhe.domain.strategy.service.rule.chain.impl;

import com.xiaolanhe.domain.strategy.service.armory.IStrategyDispatch;
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
    public Integer logic(String userId, Long strategyId) {
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, getRuleModel(), awardId);
        return awardId;
    }

    @Override
    protected String getRuleModel() {
        return "default";
    }
}
