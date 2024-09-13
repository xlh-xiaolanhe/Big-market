package com.xiaolanhe.domain.strategy.model.entity;/**
 * @Package com.xiaolanhe.domain.strategy.model.entity
 * @author xiaolanhe
 * @date 2024/9/13 18:34
 * @version V1.0
 */


import com.xiaolanhe.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** 策略规则实体
 *@author: xiaolanhe
 *@createDate: 2024/9/13 18:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyRuleEntity {
    /** 抽奖策略ID */
    private Long strategyId;
    /** 抽奖奖品ID【规则类型为策略，则不需要奖品ID】 */
    private Integer awardId;
    /** 抽象规则类型；1-策略规则、2-奖品规则 */
    private Integer ruleType;
    /** 抽奖规则类型【rule_random - 随机值计算、rule_lock - 抽奖几次后解锁、rule_luck_award - 幸运奖(兜底奖品)】 */
    private String ruleModel;
    /** 抽奖规则比值 */
    private String ruleValue;
    /** 抽奖规则描述 */
    private String ruleDesc;

    /**
     * 获取权重值
     * 数据案例；10:102,103 70:106,107 1000:104,105
     */
    public Map<String, List<Integer>> getRuleWightValues() {
        if(StringUtils.isEmpty(ruleModel) || StringUtils.isEmpty(ruleModel = ruleModel.trim()) || !Constants.RULE_WEIGHT.equals(ruleModel)
                || StringUtils.isEmpty(ruleValue) || StringUtils.isEmpty(ruleValue = ruleValue.trim())){
            return Collections.EMPTY_MAP;
        }

        String[] ruleValues = this.ruleValue.split(Constants.SPACE);
        Map<String, List<Integer>> resultMap = new HashMap<>(ruleValues.length);
        for(String curVal : ruleValues){
            if(StringUtils.isEmpty(curVal)){
                continue;
            }
            String[] split = curVal.split(Constants.COLON);
            if(split == null || split.length != 2){
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format: " + curVal);
            }
            String[] valueStrings = split[1].split(Constants.SPLIT);
            List<Integer> values = new ArrayList<>(valueStrings.length);
            for(String curValue : valueStrings){
                values.add(Integer.valueOf(curValue));
            }
            resultMap.put(split[0], values);
        }
        return resultMap;
    }
}
