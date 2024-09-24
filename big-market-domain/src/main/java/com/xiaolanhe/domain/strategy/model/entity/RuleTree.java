package com.xiaolanhe.domain.strategy.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * Description: 规则树
 *
 * @author xiaolanhe
 * @date 2024/9/24 22:47
 */
@Data
public class RuleTree {

    /** 自增ID */
    private Long id;
    /** 规则树ID */
    private String treeId;
    /** 规则树名称 */
    private String treeName;
    /** 规则树描述 */
    private String treeDesc;
    /** 规则根节点 */
    private String treeRootRuleKey;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
