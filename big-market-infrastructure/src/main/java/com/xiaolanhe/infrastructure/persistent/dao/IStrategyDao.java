package com.xiaolanhe.infrastructure.persistent.dao;

import com.xiaolanhe.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author xiaolanhe
 * @version V1.0
 * @Package com.xiaolanhe.infrastructure.persistent.dao
 * @date 2024/9/9 22:25
 */

@Mapper
public interface IStrategyDao {
    List<Strategy> queryStrategyList();
}
