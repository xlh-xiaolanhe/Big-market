package com.xiaolanhe.infrastructure.persistent.dao;

import com.xiaolanhe.domain.strategy.model.entity.RuleTree;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiaolanhe
 * @version V1.0
 * @Package com.xiaolanhe.infrastructure.persistent.dao
 * @date 2024/9/24 22:50
 */

@Mapper
public interface IRuleTreeDao {
    RuleTree queryRuleTreeByTreeId(String treeId);
}
