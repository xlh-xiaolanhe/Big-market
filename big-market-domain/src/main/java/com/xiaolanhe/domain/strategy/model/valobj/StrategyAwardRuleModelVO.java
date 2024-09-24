package com.xiaolanhe.domain.strategy.model.valobj;

import com.xiaolanhe.domain.strategy.service.rule.filter.factory.DefaultLogicFilterFactory;
import com.xiaolanhe.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 抽奖策略规则规则值对象
 * @author xiaolanhe
 * @date 2024/9/21 17:20
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardRuleModelVO {
    private String ruleModels;

    /** 提取抽奖中规则
     * @author xiaolanhe
     * @date 17:23 2024/9/21
     * @return java.lang.String[]
     **/
    public String[] raffleCenterRuleModelList() {
        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT);
        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFilterFactory.LogicModel.isCenter(ruleModelValue)) {
                ruleModelList.add(ruleModelValue);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }

    /**
     *  提取抽奖后规则
     * @author xiaolanhe
     * @date 17:23 2024/9/21
     * @return java.lang.String[]
     **/
    public String[] raffleAfterRuleModelList() {
        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT);
        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFilterFactory.LogicModel.isAfter(ruleModelValue)) {
                ruleModelList.add(ruleModelValue);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }
}
