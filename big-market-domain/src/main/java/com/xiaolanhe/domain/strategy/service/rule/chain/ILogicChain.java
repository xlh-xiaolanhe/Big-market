package com.xiaolanhe.domain.strategy.service.rule.chain;

/** 抽奖策略规则责任链接口
 * @author xiaolanhe
 * @version V1.0
 * @Package com.xiaolanhe.domain.strategy.service.rule.chain
 * @date 2024/9/21 20:05
 */
public interface ILogicChain extends ILogicChainArmory{

    /**
     *  规则校验
     * @author xiaolanhe
     * @date 20:06 2024/9/21
     * @param userId
     * @param strategyId
     * @return java.lang.Integer
     **/
    Integer logic(String userId, Long strategyId);
}
