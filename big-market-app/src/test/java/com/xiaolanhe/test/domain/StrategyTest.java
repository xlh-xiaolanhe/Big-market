package com.xiaolanhe.test.domain;

import com.google.gson.Gson;
import com.xiaolanhe.domain.strategy.service.armory.IStrategyArmory;
import com.xiaolanhe.infrastructure.persistent.redis.IRedisService;
import com.xiaolanhe.types.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 *@author: xiaolanhe
 *@createDate: 2024/9/13 16:59
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyTest {

    @Resource
    public IStrategyArmory strategyArmory;

    @Resource
    public IRedisService redisService;

    @Test
    public void test_assembleLotteryStrategy() {
        boolean res = strategyArmory.assembleLotteryStrategy(100001L);
        log.info("测试结果：{}", res);
    }

    @Test
    public void test_getAwardRateMap() {
        RMap<Object, Object> map = redisService.getMap("big_market_strategy_rate_table_key_100001");
        log.info("test_getAwardRateMap: {}", GsonUtils.toJson(map));
    }
}
