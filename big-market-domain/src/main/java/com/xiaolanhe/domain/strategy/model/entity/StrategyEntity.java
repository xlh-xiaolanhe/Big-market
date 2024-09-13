package com.xiaolanhe.domain.strategy.model.entity;

import com.xiaolanhe.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Stream;

/** 策略实体
 *@author: xiaolanhe
 *@createDate: 2024/9/13 18:27
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyEntity {
    /** 抽奖策略ID */
    private Long strategyId;
    /** 抽奖策略描述 */
    private String strategyDesc;
    /** 抽奖规则模型 如配置： rule_weight,rule_blacklist */
    private String ruleModels;

    public String[] getRuleModels() {
        if(StringUtils.isEmpty(ruleModels)){
            return null;
        }
        return ruleModels.split(Constants.SPLIT);
    }

    public String getRuleWeight() {
        String[] ruleModels = getRuleModels();
        return Arrays.stream(ruleModels).filter(n -> Constants.RULE_WEIGHT.equals(n))
                .findFirst()
                .orElse(null);
    }
}
