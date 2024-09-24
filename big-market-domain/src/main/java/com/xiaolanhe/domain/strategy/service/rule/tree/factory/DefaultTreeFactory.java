package com.xiaolanhe.domain.strategy.service.rule.tree.factory;

import com.xiaolanhe.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.xiaolanhe.domain.strategy.model.valobj.RuleTreeVO;
import com.xiaolanhe.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.xiaolanhe.domain.strategy.service.rule.tree.engine.IDecisionTreeEngine;
import com.xiaolanhe.domain.strategy.service.rule.tree.engine.impl.DecisionTreeEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Description: 规则树工厂
 *
 * @author xiaolanhe
 * @date 2024/9/22 20:17
 */

@Service
public class DefaultTreeFactory {
    private final Map<String, ILogicTreeNode> logicTreeNodeMap;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeNodeMap) {
        this.logicTreeNodeMap = logicTreeNodeMap;
    }

    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO) {
        return new DecisionTreeEngine(logicTreeNodeMap, ruleTreeVO);
    }

    /**
     *  决策树决策结果
     * @author xiaolanhe
     * @date 20:20 2024/9/22
     **/
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity {
        private RuleLogicCheckTypeVO ruleLogicCheckType;
        private StrategyAwardData strategyAwardData;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardData {
        /** 抽奖奖品ID - 内部流转使用 */
        private Integer awardId;

        /** 抽奖奖品规则 */
        private String awardRuleValue;
    }
}
