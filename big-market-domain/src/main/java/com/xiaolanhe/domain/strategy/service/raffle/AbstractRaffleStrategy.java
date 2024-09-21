package com.xiaolanhe.domain.strategy.service.raffle;

import com.xiaolanhe.domain.strategy.model.entity.RaffleAwardEntity;
import com.xiaolanhe.domain.strategy.model.entity.RaffleFactorEntity;
import com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity;
import com.xiaolanhe.domain.strategy.model.entity.StrategyEntity;
import com.xiaolanhe.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.xiaolanhe.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.xiaolanhe.domain.strategy.repository.IStrategyRepository;
import com.xiaolanhe.domain.strategy.service.armory.IStrategyDispatch;
import com.xiaolanhe.domain.strategy.service.rule.chain.ILogicChain;
import com.xiaolanhe.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.xiaolanhe.domain.strategy.service.rule.factory.DefaultLogicFilterFactory;
import com.xiaolanhe.types.enums.ResponseCode;
import com.xiaolanhe.types.exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**  定义标准的抽奖流程
 *@author: xiaolanhe
 *@createDate: 2024/9/19 22:16
 */

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy{

    // 仓储策略服务
    @Resource
    protected IStrategyRepository repository;

    // 策略调度服务
    @Resource
    protected IStrategyDispatch strategyDispatch;

    // 抽奖的责任链 -> 从抽奖的规则中，解耦出前置规则为责任链处理
    private final DefaultChainFactory defaultChainFactory;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
    }

    @Override
    public RaffleAwardEntity executeRaffle(RaffleFactorEntity raffleFactorEntity) {
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if(StringUtils.isEmpty(userId) || null == strategyId){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 1、查询策略
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);

        // 2. 获取抽奖责任链 - 前置规则的责任链处理
        ILogicChain logicChain = defaultChainFactory.fillLogicChain(strategyId);

        // 3. 通过责任链获得，奖品ID
        Integer awardId = logicChain.logic(userId, strategyId);

        // 4、查询奖品规则，用于： 抽奖中（拿到奖品id时，规则过滤校验）; 抽奖后：扣减完奖品库存后过滤，抽奖中拦截和无库存走兜底逻辑
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModelVO(strategyId, awardId);

        // 5、抽奖中-规则过滤
        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionCenterEntity = this.doCheckRaffleCenterLogic(RaffleFactorEntity.builder()
                .strategyId(strategyId)
                .userId(userId)
                .awardId(awardId)
                .build(), strategyAwardRuleModelVO.raffleCenterRuleModelList());

        if(RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionCenterEntity.getCode())) {
            log.info("【临时日志】命中抽奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。");
            return RaffleAwardEntity.builder()
                    .awardDesc("中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }



    /** 抽奖中规则校验
     * @author xiaolanhe
     * @date 17:00 2024/9/21
     * @param raffleFactorEntity
     * @param logics
     * @return com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity<com.xiaolanhe.domain.strategy.model.entity.RuleActionEntity.RaffleCenterEntity>
     **/
    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
}
