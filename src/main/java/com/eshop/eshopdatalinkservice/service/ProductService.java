package com.eshop.eshopdatalinkservice.service;

/**
 * @author: Xukai
 * @description:
 * @createDate: 2018/8/30 13:36
 * @modified By:
 */
public interface ProductService {

    /**
     * 将商品信息保存到本地的ehcache缓存中
     *
     * @param productJsonStr
     */
    String saveProduct2LocalCache(String productJsonStr);

    /**
     * 从本地ehcache缓存中获取商品信息
     *
     * @param productId
     * @return
     */
    String getProductFromLocalCache(Long productId);

    /**
     * 将商品信息保存到redis主集群中
     *
     * @param productJsonStr
     */
    void saveProduct2RedisCache(String productJsonStr);

    /**
     * 从redis主集群中获取商品信息
     *
     * @param productId
     */
    String getProductFromRedisCache(Long productId);

    /**
     * 通过feign向商品数据服务请求
     *
     * @param productId
     */
    String getProductFromRemoteService(Long productId);

}
