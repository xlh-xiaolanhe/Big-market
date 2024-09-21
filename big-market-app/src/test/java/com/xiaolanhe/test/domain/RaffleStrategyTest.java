package com.xiaolanhe.test.domain;

import com.alibaba.fastjson.JSON;
import com.xiaolanhe.domain.strategy.model.entity.RaffleAwardEntity;
import com.xiaolanhe.domain.strategy.model.entity.RaffleFactorEntity;
import com.xiaolanhe.domain.strategy.service.raffle.IRaffleStrategy;
import com.xiaolanhe.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import com.xiaolanhe.types.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author xiaolanhe
 * @date 2024/9/21 15:39
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private RuleWeightLogicFilter ruleWeightLogicFilter;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(ruleWeightLogicFilter, "userScore", 40500L);
    }

    @Test
    public void test_performRaffle() {
        RaffleFactorEntity factorEntity = RaffleFactorEntity.builder()
                .userId("xiaolanhe")
                .strategyId(100001L)
                .build();
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.executeRaffle(factorEntity);
        log.info("request: {}", GsonUtils.toJson(factorEntity));
        log.info("response: {}", GsonUtils.toJson(raffleAwardEntity));
    }

    @Test
    public void test_performRaffle_blacklist() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user003")  // 黑名单用户 user001,user002,user003
                .strategyId(100001L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.executeRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void test_raffle_center_rule_lock(){
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("xiaolanhe")
                .strategyId(100003L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.executeRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }
}
