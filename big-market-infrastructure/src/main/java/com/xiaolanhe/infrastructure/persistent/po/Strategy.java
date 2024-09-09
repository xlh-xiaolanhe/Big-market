package com.xiaolanhe.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/** 抽奖策略表
 *@author: xiaolanhe
 *@createDate: 2024/9/9 22:19
 */
@Data
public class Strategy {

    /** 自增ID */
    private Long id;
    /** 抽奖策略ID */
    private Long strategyId;
    /** 抽奖策略描述 */
    private String strategyDesc;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
