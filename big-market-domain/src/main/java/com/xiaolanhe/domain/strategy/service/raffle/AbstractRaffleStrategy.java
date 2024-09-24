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
import com.xiaolanhe.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
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
    protected final DefaultChainFactory defaultChainFactory;

    // 抽奖的决策树 -> 负责抽奖中到抽奖后的规则过滤，如抽奖到A奖品ID，之后要做次数的判断和库存的扣减等。
    protected final DefaultTreeFactory defaultTreeFactory;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
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

        // 2、责任链抽奖计算【这步拿到的是初步的抽奖ID，之后需要根据ID处理抽奖】注意；黑名单、权重等非默认抽奖的直接返回抽奖结果
        DefaultChainFactory.StrategyAwardVO chainstrategyAwardVO = raffleLogicChain(raffleFactorEntity.getUserId(), raffleFactorEntity.getStrategyId());
        log.info("抽奖策略计算-责任链 {} {} {} {}", userId, strategyId, chainstrategyAwardVO.getAwardId(), chainstrategyAwardVO.getLogicModel());
        if(!DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(chainstrategyAwardVO.getLogicModel())){
            return RaffleAwardEntity.builder()
                    .awardId(chainstrategyAwardVO.getAwardId())
                    .build();
        }

        // 3、规则树抽奖过滤 【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】
        DefaultTreeFactory.StrategyAwardData treeLoginAwardData = raffleLogicTree(userId, strategyId, chainstrategyAwardVO.getAwardId());
        log.info("抽奖策略计算-规则树 {} {} {} {}", userId, strategyId, treeLoginAwardData.getAwardId(), treeLoginAwardData.getAwardRuleValue());

        // 4、 返回抽奖结果
        return RaffleAwardEntity.builder()
                .awardId(treeLoginAwardData.getAwardId())
                .awardConfig(treeLoginAwardData.getAwardRuleValue())
                .build();
    }

    /**
     *  抽奖结果计算，责任链抽象方法
     * @author xiaolanhe
     * @date 22:10 2024/9/24
     * @param userId
     * @param strategyId
     * @return com.xiaolanhe.domain.strategy.service.rule.chain.factory.DefaultChainFactory.StrategyAwardVO
     **/
    public abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId);

    /**
     *  抽奖结果过滤，决策树决策抽象方法
     * @author xiaolanhe
     * @date 22:26 2024/9/24
     * @param userId
     * @param strategyId
     * @param awardId
     * @return com.xiaolanhe.domain.strategy.service.rule.tree.factory.DefaultTreeFactory.StrategyAwardData
     **/
    public abstract DefaultTreeFactory.StrategyAwardData raffleLogicTree(String userId, Long strategyId, Integer awardId);


}
