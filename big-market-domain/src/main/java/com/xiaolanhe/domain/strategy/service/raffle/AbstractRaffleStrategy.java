package com.xiaolanhe.domain.strategy.service.raffle;

import com.xiaolanhe.domain.strategy.model.entity.RaffleAwardEntity;
import com.xiaolanhe.domain.strategy.model.entity.RaffleFactorEntity;
import com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity;
import com.xiaolanhe.domain.strategy.model.entity.StrategyEntity;
import com.xiaolanhe.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.xiaolanhe.domain.strategy.repository.IStrategyRepository;
import com.xiaolanhe.domain.strategy.service.armory.IStrategyDispatch;
import com.xiaolanhe.domain.strategy.service.rule.factory.DefaultLogicFilterFactory;
import com.xiaolanhe.types.enums.ResponseCode;
import com.xiaolanhe.types.exceptions.AppException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**  定义标准的抽奖流程
 *@author: xiaolanhe
 *@createDate: 2024/9/19 22:16
 */
public abstract class AbstractRaffleStrategy implements IRaffleStrategy{

    // 仓储策略服务
    @Resource
    protected IStrategyRepository repository;

    // 策略调度服务
    @Resource
    protected IStrategyDispatch strategyDispatch;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
    }

    @Override
    public RaffleAwardEntity executeRaffle(RaffleFactorEntity raffleFactorEntity) {
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if(StringUtils.isEmpty(userId) || null == strategyId){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 查询策略
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);

        // 抽奖前规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> beforeEntityRuleActionEntity = this.doCheckRaffleBeforeLogic(RaffleFactorEntity.builder().userId(userId).strategyId(strategyId).build()
                        , strategyEntity.getRuleModels());
        if(RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(beforeEntityRuleActionEntity.getCode())) {
            if(DefaultLogicFilterFactory.LogicModel.RULE_BLACKLIST.getCode().equals(beforeEntityRuleActionEntity.getRuleModel())) {
                return RaffleAwardEntity.builder()
                        .awardId(beforeEntityRuleActionEntity.getData().getAwardId())
                        .build();
            }else if(DefaultLogicFilterFactory.LogicModel.RULE_WIGHT.getCode().equals(beforeEntityRuleActionEntity.getRuleModel())) {
                // 根据权重返回的信息进行抽奖
                RuleActionEntity.RaffleBeforeEntity data = beforeEntityRuleActionEntity.getData();
                String ruleWeightValueKey = data.getRuleWeightValueKey();
                Integer randomAwardId = strategyDispatch.getRandomAwardId(strategyId, ruleWeightValueKey.split(":")[0]);
                return RaffleAwardEntity.builder()
                        .awardId(randomAwardId)
                        .build();
            }
        }

        // 执行默认抽奖流程
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }




    /**
     *  抽奖前置动作
     * @author xiaolanhe
     * @date 22:45 2024/9/19
     * @param raffleFactorEntity
     * @return com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity<com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity.RaffleBeforeEntity>
     **/
    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
}
