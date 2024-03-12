package com.heisenberg.user.config.redis;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;


@Component
public class RedisCache {

    public RedisTemplate redisTemplate;

    public <T> void setCacheObject(final String key, final T value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public boolean expire(final String key, final long timeout) {
        return this.expire(key, timeout, TimeUnit.SECONDS);
    }

    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return this.redisTemplate.expire(key, timeout, unit);
    }

    public long getExpire(final String key) {
        return this.redisTemplate.getExpire(key);
    }

    public Boolean hasKey(String key) {
        return this.redisTemplate.hasKey(key);
    }

    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = this.redisTemplate.opsForValue();
        return operation.get(key);
    }

    public boolean deleteObject(final String key) {
        return this.redisTemplate.delete(key);
    }

    public long deleteObject(final Collection collection) {
        return this.redisTemplate.delete(collection);
    }

    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = this.redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0L : count;
    }

    public <T> long setCacheList(final String key, final List<T> dataList, final Long timeout, final TimeUnit timeUnit) {
        Long count = this.redisTemplate.opsForList().rightPushAll(key, new Object[]{dataList, timeout, timeUnit});
        return count == null ? 0L : count;
    }

    public <T> List<T> getCacheList(final String key) {
        return this.redisTemplate.opsForList().range(key, 0L, -1L);
    }

    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = this.redisTemplate.boundSetOps(key);
        Iterator it = dataSet.iterator();

        while(it.hasNext()) {
            setOperation.add((T) new Object[]{new Object[]{it.next()}});
        }

        return setOperation;
    }

    public <T> Set<T> getCacheSet(final String key) {
        return this.redisTemplate.opsForSet().members(key);
    }

    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            this.redisTemplate.opsForHash().putAll(key, dataMap);
        }

    }

    public <T> Map<String, T> getCacheMap(final String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        this.redisTemplate.opsForHash().put(key, hKey, value);
    }

    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = this.redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return this.redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    public Collection<String> keys(final String pattern) {
        return this.redisTemplate.keys(pattern);
    }
}
