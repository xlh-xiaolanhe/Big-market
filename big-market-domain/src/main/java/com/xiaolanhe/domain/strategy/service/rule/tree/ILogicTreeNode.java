package com.xiaolanhe.domain.strategy.service.rule.tree;

import com.xiaolanhe.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/** 规则树接口
 * @author xiaolanhe
 * @version V1.0
 * @Package com.xiaolanhe.domain.strategy.service.rule.tree
 * @date 2024/9/22 20:16
 */
public interface ILogicTreeNode {

    /**
     *  决策方法
     * @author xiaolanhe
     * @date 20:22 2024/9/22
     * @param userId
     * @param strategyId
     * @param awardId
     * @return com.xiaolanhe.domain.strategy.service.rule.tree.factory.DefaultTreeFactory.TreeActionEntity
     **/
    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId);
}
