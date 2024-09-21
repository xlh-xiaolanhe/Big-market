package com.xiaolanhe.domain.strategy.service.annotation;

import com.xiaolanhe.domain.strategy.service.rule.factory.DefaultLogicFilterFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xiaolanhe
 * @version V1.0
 * @Package com.xiaolanhe.domain.strategy.service.annotation
 * @date 2024/9/16 10:34
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicStrategy {
    DefaultLogicFilterFactory.LogicModel logicMode();
}
