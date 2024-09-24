package com.xiaolanhe.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Description: 规则树-树根
 *
 * @author xiaolanhe
 * @date 2024/9/22 19:41
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeVO {
    /** 规则树id **/
    private String treeId;

    /** 规则树名称 **/
    private String treeName;

    /** 规则树描述 **/
    private String treeDesc;

    /** 规则树根节点 **/
    private String treeRootRuleNode;

    /** 规则节点 **/
    private Map<String, RuleTreeNodeVO> treeNodeMap;

}
