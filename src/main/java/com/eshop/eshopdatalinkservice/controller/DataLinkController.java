package com.eshop.eshopdatalinkservice.controller;

import com.eshop.eshopdatalinkservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Xukai
 * @description:
 * @createDate: 2018/8/30 13:11
 * @modified By:
 */
@RestController
@Slf4j
public class DataLinkController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/getProduct")
    public String getProduct(Long productId) {
        String productJsonStr;
        // 先从本地缓存中取
        productJsonStr = productService.getProductFromLocalCache(productId);
        // 本地缓存无数据就从redis主集群取
        if (StringUtils.isEmpty(productJsonStr)) {
            productJsonStr = productService.getProductFromRedisCache(productId);
            // redis主集群无数据就通过feign向数据服务请求商品、商品属性、商品规格，再将三个数据拼接
            if (StringUtils.isEmpty(productJsonStr)) {
                productJsonStr = productService.getProductFromRemoteService(productId);
                // 将数据写入redis主集群，主集群会自动同步到从集群
                if (StringUtils.isNotEmpty(productJsonStr)) {
                    productService.saveProduct2RedisCache(productJsonStr);
                }
            }
            // 将数据写入本地缓存
            if (StringUtils.isNotEmpty(productJsonStr)) {
                productService.saveProduct2LocalCache(productJsonStr);
            }
        }
        return productJsonStr;
    }
}
