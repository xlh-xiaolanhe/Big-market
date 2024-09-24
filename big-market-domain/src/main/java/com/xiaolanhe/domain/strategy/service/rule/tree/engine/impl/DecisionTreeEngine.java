package com.xiaolanhe.domain.strategy.service.rule.tree.engine.impl;

import com.xiaolanhe.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.xiaolanhe.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import com.xiaolanhe.domain.strategy.model.valobj.RuleTreeNodeVO;
import com.xiaolanhe.domain.strategy.model.valobj.RuleTreeVO;
import com.xiaolanhe.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.xiaolanhe.domain.strategy.service.rule.tree.engine.IDecisionTreeEngine;
import com.xiaolanhe.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: 决策树引擎
 *
 * @author xiaolanhe
 * @date 2024/9/22 20:34
 */

@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardData process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardData strategyAwardData = null;
        String treeRootRuleKey = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(treeRootRuleKey);
        while(null != treeRootRuleKey) {
            // 获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());

            DefaultTreeFactory.TreeActionEntity actionEntity = logicTreeNode.logic(userId, strategyId, awardId);
            RuleLogicCheckTypeVO ruleLogicCheckType = actionEntity.getRuleLogicCheckType();
            strategyAwardData = actionEntity.getStrategyAwardData();

            log.info("决策树引擎【{}】treeId: {} node: {} code: {}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), treeRootRuleKey, ruleLogicCheckType.getCode());

            treeRootRuleKey = getNextNodeKey(ruleLogicCheckType.getCode(), ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode = treeNodeMap.get(treeRootRuleKey);
        }
        return strategyAwardData;
    }

    /**
     *  获取下一个决策节点
     * @author xiaolanhe
     * @date 20:46 2024/9/22
     * @param matterValue
     * @param treeNodeLineVOList
     * @return java.lang.String
     **/
    private String getNextNodeKey(String matterValue, List<RuleTreeNodeLineVO> treeNodeLineVOList) {
        if(CollectionUtils.isEmpty(treeNodeLineVOList)) {
            return null;
        }
        for(RuleTreeNodeLineVO lineVO : treeNodeLineVOList) {
            if(decisionLogic(matterValue, lineVO)){
                return lineVO.getRuleNodeTo();
            }
        }
        throw new RuntimeException("决策树引擎，nextNode 计算失败，未找到可执行节点！");
    }

    private boolean decisionLogic(String matterValue, RuleTreeNodeLineVO lineVO) {
        switch (lineVO.getRuleLimitType()){
            case EQUAL:
                return matterValue.equals(lineVO.getRuleLimitValue().getCode());
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }
}
