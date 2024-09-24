package com.xiaolanhe.infrastructure.persistent.dao;


import com.xiaolanhe.domain.strategy.model.entity.RuleTreeNodeLine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface IRuleTreeNodeLineDao {

    List<RuleTreeNodeLine> queryRuleTreeNodeLineListByTreeId(String treeId);

}
