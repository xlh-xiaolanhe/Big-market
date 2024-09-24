package com.xiaolanhe.domain.strategy.model.valobj;

import lombok.Data;

import java.util.Map;

/**
 * Description: 规则树-树根
 *
 * @author xiaolanhe
 * @date 2024/9/22 19:41
 */

@Data
public class RuleTreeVO {
    /** 规则树id **/
    private Integer treeId;

    /** 规则树名称 **/
    private String treeName;

    /** 规则树描述 **/
    private String treeDesc;

    /** 规则树根节点 **/
    private String treeRootRuleNode;

    /** 规则节点 **/
    private Map<String, RuleTreeNodeVO> treeNodeMap;

}
