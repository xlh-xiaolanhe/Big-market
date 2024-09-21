package com.xiaolanhe.domain.strategy.service.rule.chain;

/** 责任链装配
 * @author xiaolanhe
 * @version V1.0
 * @Package com.xiaolanhe.domain.strategy.service.rule.chain
 * @date 2024/9/21 20:05
 */
public interface ILogicChainArmory {

    ILogicChain getNext();

    ILogicChain appendNext(ILogicChain next);
}
