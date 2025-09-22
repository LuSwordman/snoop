package com.zzy.logintest.Task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzy.logintest.domain.pojo.PostLike;
import com.zzy.logintest.mapper.PostLikeMapper;
import com.zzy.logintest.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostLikeSyncTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PostLikeMapper postLikeMapper;
    @Autowired
    private PostMapper postMapper;

    @Scheduled(cron = "0/10 * * * * *") // 每天10S执行一次
    @Transactional
    public void syncPostLikes() {
        System.out.println("开始同步帖子点赞数据...");
        Set<String> keys = stringRedisTemplate.keys("post:like:*");
        if (keys == null || keys.isEmpty()) {
            System.out.println("没有需要同步的点赞数据");
            return;
        }

        for (String key : keys) {
            Long postId = Long.valueOf(key.substring("post:like:".length()));

            Set<String> redisUserIds = stringRedisTemplate.opsForSet().members(key);
            if (redisUserIds == null || redisUserIds.isEmpty()) continue;

            // 拆分 Redis 里的点赞和取消赞用户ID集合
            Set<Long> likedUserIds = new HashSet<>();
            Set<Long> dislikedUserIds = new HashSet<>();

            for (String uidStr : redisUserIds) {
                try {
                    if (uidStr.startsWith("-")) {

                        dislikedUserIds.add(Long.parseLong(uidStr.substring(1)));
                    } else {
                        likedUserIds.add(Long.parseLong(uidStr));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // 或记录日志
                }
            }

            // 查询数据库已点赞用户ID集合
            List<PostLike> postLikes = postLikeMapper.selectList(new QueryWrapper<PostLike>().eq("post_id", postId));
            Set<Long> postLikeUserIds = postLikes.stream().map(PostLike::getUserId).collect(Collectors.toSet());


            // 新增点赞 = Redis点赞 - 数据库点赞
            Set<Long> toAdd = new HashSet<>(likedUserIds);
            toAdd.removeAll(postLikeUserIds);

            // 删除点赞 数据库一定存在


            //批量插入
            if( !toAdd.isEmpty()){
                  postLikeMapper.batchInsert(toAdd.stream().map(userId -> new PostLike(postId, userId)).collect(Collectors.toList()));
            }
            // 帖子点赞数增加



            //批量删除
            System.out.println("批量删除用户ID:"+dislikedUserIds);
            if(!dislikedUserIds.isEmpty()){
                // 根据帖子id + 用户id
                QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("post_id", postId).in("user_id", dislikedUserIds);

                postLikeMapper.delete(queryWrapper);
            }
            // 同步完成后清空Redis对应的点赞数据，避免重复处理

            //更新mysql帖子点赞量postId
            int newLikeCount = postLikeUserIds.size() + toAdd.size() - dislikedUserIds.size();
            postMapper.updateLikeCount(postId, newLikeCount);
            stringRedisTemplate.delete(key);
        }

    }
}
