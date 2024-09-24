package com.xiaolanhe.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Description: 规则树-连线
 *
 * @author xiaolanhe
 * @date 2024/9/22 19:50
 */
@Data
@Builder
@AllArgsConstructor
public class RuleTreeNodeLineVO {
    /** 规则树id **/
    private Integer treeId;

    /** 规则key节点  from **/
    private String ruleNodeFrom;

    /** 规则key节点  to **/
    private String ruleNodeTo;

    /** 规则限定类型 **/
    private RuleLimitTypeVO ruleLimitType;

    /** 限定值 **/
    private RuleLogicCheckTypeVO ruleLimitValue;
}
