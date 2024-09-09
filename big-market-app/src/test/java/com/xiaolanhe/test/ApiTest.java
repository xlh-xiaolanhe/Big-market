package com.xiaolanhe.test;

import com.alibaba.fastjson.JSON;
import com.xiaolanhe.infrastructure.persistent.dao.IAwardDao;
import com.xiaolanhe.infrastructure.persistent.po.Award;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 *@author: xiaolanhe
 *@createDate: 2024/7/28 16:15
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private IAwardDao awardDao;

    @Test
    public void test() {
        log.info("测试完成");
    }

    @Test
    public void test_queryAwardList() {
        List<Award> awards = awardDao.queryAwardList();
        log.info("测试结果： {}", JSON.toJSONString(awards));
    }
}
