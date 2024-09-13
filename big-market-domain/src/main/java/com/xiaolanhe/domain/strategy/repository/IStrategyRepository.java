package com.xiaolanhe.domain.strategy.repository;

import com.xiaolanhe.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;
import java.util.Map;

/**
 * @author xiaolanhe
 * @version V1.0
 * 策略服务仓储接口
 * @date 2024/9/11 22:39
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardListByStrategyId(Long strategyId);

    void storeStrategyAwardSearchTable(Long strategyId, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, Integer rateKey);
}
