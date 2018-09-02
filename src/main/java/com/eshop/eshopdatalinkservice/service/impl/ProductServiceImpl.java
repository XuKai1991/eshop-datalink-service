package com.eshop.eshopdatalinkservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.eshop.eshopdatalinkservice.service.ProductFeignClient;
import com.eshop.eshopdatalinkservice.service.ProductService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author: Xukai
 * @description:
 * @createDate: 2018/8/30 13:41
 * @modified By:
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    public static final String CACHE_NAME = "local";

    @Override
    @CachePut(value = CACHE_NAME, keyGenerator = "keyGenerator")
    public String saveProduct2LocalCache(String productJsonStr) {
        return productJsonStr;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'dim_product:'+#productId")
    public String getProductFromLocalCache(Long productId) {
        return null;
    }

    @Override
    public void saveProduct2RedisCache(String productJsonStr) {
        JSONObject productJsonObject = JSONObject.parseObject(productJsonStr);
        Long productId = productJsonObject.getLong("id");
        redisTemplate.opsForValue().set("dim_product:" + productId, productJsonObject.toJSONString());
    }

    @Override
    public String getProductFromRedisCache(Long productId) {
        String productJsonStr = redisTemplate.opsForValue().get("dim_product:" + productId);
        if (StringUtils.isNotEmpty(productJsonStr)) {
            return productJsonStr;
        } else {
            return null;
        }
    }

    @HystrixCommand(fallbackMethod = "getProductFromRemoteServiceFallback")
    @Override
    public String getProductFromRemoteService(Long productId) {
        String productJsonStr = productFeignClient.findProductById(productId);
        if (StringUtils.isNotEmpty(productJsonStr)) {
            JSONObject productJsonObject = JSONObject.parseObject(productJsonStr);
            // 根据productId从远端服务获取productProperty，如果非空就拼装到product中
            String productPropertyJsonStr = productFeignClient.findProductPropertyByProductId(productId);
            if (StringUtils.isNotEmpty(productPropertyJsonStr)) {
                productJsonObject.put("product_property", JSONObject.parseObject(productPropertyJsonStr));
            }
            // 根据productId从远端服务获取productSpecification，如果非空就拼装到product中
            String productSpecificationJsonStr = productFeignClient.findProductSpecificationByProductId(productId);
            if (StringUtils.isNotEmpty(productSpecificationJsonStr)) {
                productJsonObject.put("product_specification", JSONObject.parseObject(productSpecificationJsonStr));
            }
            return productJsonObject.toJSONString();
        }
        return null;
    }

    public String getProductFromRemoteServiceFallback(Long productId) {
        return null;
    }
}
