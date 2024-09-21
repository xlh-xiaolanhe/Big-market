package com.xiaolanhe.domain.strategy.service.rule.factory;

import com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity;
import com.xiaolanhe.domain.strategy.service.annotation.LogicStrategy;
import com.xiaolanhe.domain.strategy.service.rule.ILogicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 规则工厂
 *@author: xiaolanhe
 *@createDate: 2024/9/16 10:26
 */

@Service
public class DefaultLogicFilterFactory {

    // ILogicFilter<?> 表示 ILogicFilter 接口的一个实现类
    private Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    public DefaultLogicFilterFactory(List<ILogicFilter<?>> logicFilterList) {
        if(!CollectionUtils.isEmpty(logicFilterList)){
            logicFilterList.forEach(item -> {
                LogicStrategy logicStrategy = AnnotationUtils.findAnnotation(item.getClass(), LogicStrategy.class);
                if(null != logicStrategy) {
                    logicFilterMap.put(logicStrategy.logicMode().getCode(), item);
                }
            });
        }
    }

    public <T extends RuleActionEntity.RaffleEntity> Map<String, ILogicFilter<T>> openLogicFilter() {
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>) logicFilterMap;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回"),

        ;

        private final String code;
        private final String info;
    }
}
