package com.zzy.logintest.Task;

import com.zzy.logintest.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 帖子浏览量同步任务
 * 定期将Redis中的浏览量数据同步到数据库
 */
@Component
public class PostViewTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PostMapper postMapper;

    /**
     * 同步帖子浏览量数据
     * 每30秒执行一次
     */
    @Scheduled(cron = "0/15 * * * * *") // 每30秒执行一次
    @Transactional
    public void syncPostViewCounts() {
        System.out.println("开始同步帖子浏览量数据...");

        // 获取所有浏览量相关的Redis键
        Set<String> keys = stringRedisTemplate.keys("post:view:*");
        if (keys == null || keys.isEmpty()) {
            System.out.println("没有需要同步的浏览量数据");
            return;
        }

        int syncCount = 0;
        for (String key : keys) {
            try {
                // 从键中提取帖子ID
                Long postId = Long.valueOf(key.substring("post:view:".length()));

                // 获取Redis中的浏览量
                String viewCountStr = stringRedisTemplate.opsForValue().get(key);
                if (viewCountStr == null || viewCountStr.isEmpty()) {
                    continue;
                }

                int redisViewCount = Integer.parseInt(viewCountStr);
                if (redisViewCount <= 0) {
                    continue;
                }

                // 获取当前帖子信息以获取数据库中的浏览量
                var post = postMapper.findById(postId);
                if (post == null) {
                    System.err.println("帖子不存在，ID: " + postId);
                    // 删除无效的Redis键
                    stringRedisTemplate.delete(key);
                    continue;
                }

                // 更新数据库中的浏览量
                postMapper.updateViewCount(postId, redisViewCount);

                // 同步成功后删除Redis中的数据，避免重复处理
                stringRedisTemplate.delete(key);
                syncCount++;


            } catch (NumberFormatException e) {
                System.err.println("解析帖子ID或浏览量失败，键: " + key + "，错误: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("同步帖子浏览量失败，键: " + key + "，错误: " + e.getMessage());
            }
        }

        System.out.println("浏览量同步完成，共同步 " + syncCount + " 个帖子");
    }

    /**
     * 获取指定帖子在Redis中的浏览量增量
     * @param postId 帖子ID
     * @return Redis中的浏览量增量，如果不存在则返回0
     */
    public int getRedisViewCount(Long postId) {
        String key = "post:view:" + postId;
        String viewCountStr = stringRedisTemplate.opsForValue().get(key);
        if (viewCountStr == null || viewCountStr.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(viewCountStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 手动触发同步指定帖子的浏览量
     * @param postId 帖子ID
     * @return 是否同步成功
     */
    public boolean syncSinglePostViewCount(Long postId) {
        String key = "post:view:" + postId;
        try {
            String viewCountStr = stringRedisTemplate.opsForValue().get(key);
            if (viewCountStr == null || viewCountStr.isEmpty()) {
                return false;
            }

            int redisViewCount = Integer.parseInt(viewCountStr);
            if (redisViewCount <= 0) {
                return false;
            }

            var post = postMapper.findById(postId);
            if (post == null) {
                stringRedisTemplate.delete(key);
                return false;
            }

            int currentViewCount = post.getViewCount() != null ? post.getViewCount() : 0;
            int newViewCount = currentViewCount + redisViewCount;

            postMapper.updateViewCount(postId, newViewCount);
            stringRedisTemplate.delete(key);

            System.out.println("手动同步帖子ID: " + postId + " 浏览量增加: " + redisViewCount + " 新总量: " + newViewCount);
            return true;
        } catch (Exception e) {
            System.err.println("手动同步帖子浏览量失败，帖子ID: " + postId + "，错误: " + e.getMessage());
            return false;
        }
    }
}
