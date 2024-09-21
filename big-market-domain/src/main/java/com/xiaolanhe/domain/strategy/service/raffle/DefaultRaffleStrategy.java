package com.xiaolanhe.domain.strategy.service.raffle;

import com.xiaolanhe.domain.strategy.model.entity.RaffleFactorEntity;
import com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity;
import com.xiaolanhe.domain.strategy.model.entity.RuleMatterEntity;
import com.xiaolanhe.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.xiaolanhe.domain.strategy.repository.IStrategyRepository;
import com.xiaolanhe.domain.strategy.service.armory.IStrategyDispatch;
import com.xiaolanhe.domain.strategy.service.rule.ILogicFilter;
import com.xiaolanhe.domain.strategy.service.rule.factory.DefaultLogicFilterFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 默认的抽奖策略是实现
 * @author xiaolanhe
 * @date 2024/9/19 23:25
 */

@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy{

    @Resource
    private DefaultLogicFilterFactory logicFactory;

    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch) {
        super(repository, strategyDispatch);
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> logicFilterMap = logicFactory.openLogicFilter();

        // 黑名单过滤
        String blacklist = Arrays.stream(logics).filter(item -> item.contains(DefaultLogicFilterFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);

        if(StringUtils.isNotEmpty(blacklist)) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterMap.get(DefaultLogicFilterFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();

            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setRuleModel(DefaultLogicFilterFactory.LogicModel.RULE_BLACKLIST.getCode());

            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> actionEntity = logicFilter.filter(ruleMatterEntity);
            if(!RuleLogicCheckTypeVO.ALLOW.getCode().equals(actionEntity.getCode())) {
                return actionEntity;
            }
        }

        // 顺序过滤剩余规则
        Set<String> ruleSet = Arrays.stream(logics).filter(item -> !DefaultLogicFilterFactory.LogicModel.RULE_BLACKLIST.getCode().equals(item))
                .collect(Collectors.toSet());

        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ans = null;
        for(String rule : ruleSet) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterMap.get(rule);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setRuleModel(rule);
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            // 这行代码有必要吗
            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
            ans = logicFilter.filter(ruleMatterEntity);
            log.info("抽奖前规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), rule, ans.getCode(), ans.getInfo());

            if(!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ans.getCode())) {
                return ans;
            }
        }
        return ans;
    }
}
