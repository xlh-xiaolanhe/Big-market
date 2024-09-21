package com.xiaolanhe.domain.strategy.service.raffle;

import com.xiaolanhe.domain.strategy.model.entity.RaffleAwardEntity;
import com.xiaolanhe.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @author xiaolanhe
 * @version V1.0 策略抽奖接口
 * @Package com.xiaolanhe.domain.strategy.service.raffle
 * @date 2024/9/19 22:13
 */
public interface IRaffleStrategy {

    /**
        执行抽奖动作，根据抽奖因子，执行抽奖计算，返回对应奖品信息
    */
    RaffleAwardEntity executeRaffle(RaffleFactorEntity raffleFactorEntity);

}
