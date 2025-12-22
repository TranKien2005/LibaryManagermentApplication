package com.library.backend.services;

import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThrottlingService {

    @Value("${throttling.limit}")
    int limit;

    @Value("${throttling.duration}")
    int duration;

    @Value("${throttling.base-delay}")
    int baseDelay;

    final RedissonClient redissonClient;

    public void throttle(String key) throws InterruptedException {
        String redisKey = "throttle:" + key;
        RList<Long> timestamps = redissonClient.getList(redisKey);

        long now = System.currentTimeMillis();
        for (int i = timestamps.size() - 1; i >= 0; i--) {
            Long t = timestamps.get(i);
            if (t == null || t < now - duration * 1000L) {
                timestamps.remove(i);
            }
        }
        timestamps.add(now);
        timestamps.expire(duration, TimeUnit.SECONDS);

        int count = timestamps.size();

        if (count > limit) {
            int overload = count - limit;
            long delay = (long) baseDelay * overload;
            Thread.sleep(Math.min(delay, 2000));
            now = System.currentTimeMillis();
            for (int i = timestamps.size() - 1; i >= 0; i--) {
                Long t = timestamps.get(i);
                if (t < now - duration * 1000L) {
                    timestamps.remove(i);
                }
            }
            if (timestamps.size() > limit * 1.5) {
                throw new GeneralException(ResponseCode.TOO_MANY_REQUEST);
            }
        }
    }

}
