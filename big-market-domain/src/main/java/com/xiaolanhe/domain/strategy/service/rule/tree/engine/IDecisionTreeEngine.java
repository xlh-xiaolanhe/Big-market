package com.xiaolanhe.domain.strategy.service.rule.tree.engine;

import com.xiaolanhe.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/** 规则树组合接口
 * @author xiaolanhe
 * @version V1.0
 * @Package com.xiaolanhe.domain.strategy.service.rule.tree.engine
 * @date 2024/9/22 20:33
 */
public interface IDecisionTreeEngine {
    DefaultTreeFactory.StrategyAwardData process(String userId, Long strategyId, Integer awardId);
}
