package com.xiaolanhe.domain.strategy.service.rule.chain.impl;

import com.xiaolanhe.domain.strategy.service.rule.chain.ILogicChain;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:  抽奖策略责任链，判断走那种抽奖策略。如；默认抽象、权重抽奖、黑名单抽奖
 *
 * @author xiaolanhe
 * @date 2024/9/21 20:29
 */

@Slf4j
public abstract class AbstractLogicChain implements ILogicChain {

    private ILogicChain next;

    @Override
    public ILogicChain getNext() {
        return this.next;
    }

    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }

    protected abstract  String getRuleModel();
}
