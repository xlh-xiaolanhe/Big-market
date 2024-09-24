package com.xiaolanhe.domain.strategy.service.rule.filter.factory;

import com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity;
import com.xiaolanhe.domain.strategy.service.annotation.LogicStrategy;
import com.xiaolanhe.domain.strategy.service.rule.filter.ILogicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
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


        RULE_LOCK("rule_lock", "【抽奖中规则】抽奖n次后，对应奖品可解锁抽奖", "center"),
        RULE_LUCK_AWARD("rule_luck_award", "【抽奖后规则】抽奖n次后，对应奖品可解锁抽奖", "after"),
        ;

        private final String code;
        private final String info;
        private final String type;

        public static boolean isCenter(String code){
            return "center".equals(Arrays.stream(LogicModel.values()).filter(n -> n.getCode().equals(code)).findFirst().orElse(null));
        }

        public static boolean isAfter(String code){
            return "after".equals(Arrays.stream(LogicModel.values()).filter(n -> n.getCode().equals(code)).findFirst().orElse(null));
        }
    }
}
