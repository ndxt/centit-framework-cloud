package com.centit.framework.oauth.service.impl;

import com.centit.framework.oauth.constant.RedisConstant;
import com.centit.framework.oauth.service.ResourceService;
import cn.hutool.core.collection.CollUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 资源与角色匹配关系管理业务类
 *
 * @author
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    private final RedisTemplate<String, Object> redisTemplate;

    public ResourceServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initData() {
        Map<String, List<String>> resourceRolesMap = new TreeMap<>();
        resourceRolesMap.put("/resource/hello", CollUtil.toList("ADMIN"));
        //resourceRolesMap.put("/framework/test/get", CollUtil.toList("ADMIN"));
        resourceRolesMap.put("/resource/user/currentUser", CollUtil.toList("ADMIN", "USER"));
        redisTemplate.opsForHash().putAll(RedisConstant.RESOURCE_ROLES_MAP, resourceRolesMap);
    }
}
