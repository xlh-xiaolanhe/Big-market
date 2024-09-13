package com.xiaolanhe.domain.strategy.service.armory;

/** 策略抽奖调度
 * @author xiaolanhe
 * @version V1.0
 * @date 2024/9/13 18:57
 */
public interface IStrategyDispatch {

    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param strategyId 策略ID
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);

    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);
}
