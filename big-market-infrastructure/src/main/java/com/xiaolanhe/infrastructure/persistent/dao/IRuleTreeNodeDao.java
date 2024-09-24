package com.xiaolanhe.infrastructure.persistent.dao;


import com.xiaolanhe.domain.strategy.model.entity.RuleTreeNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface IRuleTreeNodeDao {

    List<RuleTreeNode> queryRuleTreeNodeListByTreeId(String treeId);

}
