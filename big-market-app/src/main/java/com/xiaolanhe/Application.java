package com.xiaolanhe;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *@author: xiaolanhe
 *@createDate: 2024/7/28 16:10
 */

@SpringBootApplication
@Configurable
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }
}
