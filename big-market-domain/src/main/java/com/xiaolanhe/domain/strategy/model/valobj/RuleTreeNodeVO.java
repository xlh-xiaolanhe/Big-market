package com.xiaolanhe.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Description: 规则树-节点
 *
 * @author xiaolanhe
 * @date 2024/9/22 19:48
 */
@Data
@Builder
@AllArgsConstructor
public class RuleTreeNodeVO {
    /** 规则树id **/
    private Integer treeId;

    /** 规则 key **/
    private String ruleKey;

    /** 规则描述 **/
    private String ruleDesc;

    /** 规则比值 **/
    private String ruleValue;

    /** 规则连线 **/
    private List<RuleTreeNodeLineVO> treeNodeLineVOList;
}
