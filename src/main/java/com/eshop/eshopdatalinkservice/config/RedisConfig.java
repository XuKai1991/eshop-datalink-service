package com.eshop.eshopdatalinkservice.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Xukai
 * @description:
 * @createDate: 2018/8/30 16:56
 * @modified By:
 */
@CacheConfig
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 自定义key生成策略，非必须配置项，如有需要才配置使用
     * 使用方法：@Cacheable(value = "XXX", keyGenerator = "keyGenerator")
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        //为给定的方法及其参数生成一个键
        //格式为：com.example.service.BookService-getById-8
        return (target, method, params) -> {
            StringBuffer sb = new StringBuffer();
            sb.append("dim_product:");
            for (Object param : params) {
                String productJsonStr = param.toString();
                Long productId = JSONObject.parseObject(productJsonStr).getLong("id");
                sb.append(productId);
            }
            return sb.toString();
        };
    }

}

