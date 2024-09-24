package com.xiaolanhe.domain.strategy.service.rule.chain.factory;

import com.xiaolanhe.domain.strategy.model.entity.StrategyEntity;
import com.xiaolanhe.domain.strategy.repository.IStrategyRepository;
import com.xiaolanhe.domain.strategy.service.rule.chain.ILogicChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author xiaolanhe
 * @date 2024/9/21 20:09
 */

@Service
public class DefaultChainFactory implements ApplicationContextAware {
    private final Map<Long, ILogicChain> logicChainMap;

    protected IStrategyRepository repository;

    // 原型模式获取对象
    private ApplicationContext applicationContext;

    public DefaultChainFactory(IStrategyRepository repository) {
        this.repository = repository;
        this.logicChainMap = new ConcurrentHashMap<>();
    }

    // 在装配责任链的时候，可以根据 ruleModels 的顺序，进行存储责任链条
    public ILogicChain fillLogicChain(Long strategyId) {
        ILogicChain cacheLogicChain = logicChainMap.get(strategyId);
        if (null != cacheLogicChain) return cacheLogicChain;

        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategyEntity.getRuleModels();

        if(null == ruleModels || ruleModels.length == 0){
            ILogicChain ruleDefaultLogicChain = applicationContext.getBean("default", ILogicChain.class);
            logicChainMap.put(strategyId, ruleDefaultLogicChain);
            return ruleDefaultLogicChain;
        }

        // 按照配置顺序装填用户配置的责任链；rule_blacklist、rule_weight 「注意此数据从Redis缓存中获取，如果更新库表，记得在测试阶段手动处理缓存」
        int index = 0;
        if(index < ruleModels.length && !applicationContext.containsBean(ruleModels[index])){
            index++;
        }

        if(index == ruleModels.length){
            ILogicChain ruleDefaultLogicChain = applicationContext.getBean("default", ILogicChain.class);
            logicChainMap.put(strategyId, ruleDefaultLogicChain);
            return ruleDefaultLogicChain;
        }

        ILogicChain headNode = applicationContext.getBean(ruleModels[index], ILogicChain.class);
        ILogicChain curNode = headNode;
        for(int i = index+1; i < ruleModels.length; i++) {
            if(applicationContext.containsBean(ruleModels[i]) == false){
                continue;
            }
            ILogicChain nextChain = applicationContext.getBean(ruleModels[i], ILogicChain.class);
            curNode = curNode.appendNext(nextChain);
        }

        // 默认责任链打底
        curNode.appendNext(applicationContext.getBean("default", ILogicChain.class));

        logicChainMap.put(strategyId, headNode);

        return headNode;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /** 抽奖奖品ID - 内部流转使用 */
        private Integer awardId;
        /**  */
        private String logicModel;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重规则"),
        ;

        private final String code;
        private final String info;

    }
}
