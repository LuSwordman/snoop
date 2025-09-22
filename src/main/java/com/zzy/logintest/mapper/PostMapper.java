package com.zzy.logintest.mapper;



import com.zzy.logintest.domain.dto.PostDto;
import com.zzy.logintest.domain.pojo.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {
    @Insert("INSERT INTO posts (user_id, title, content) VALUES (#{userId}, #{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id") //返回主键
    int insert(Post post);

    @Select("SELECT * FROM posts WHERE id = #{id} AND status = 1")
    Post findById(Long id);

    @Select("SELECT * FROM posts WHERE status = 1 ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    List<Post> findAll(@Param("offset") int offset, @Param("size") int size);

    @Update("UPDATE posts SET title = #{title}, content = #{content} WHERE id = #{id} AND status = 1")
    int update(Post post);

    @Update("UPDATE posts SET status = 0 WHERE id = #{id}")
    int delete(Long id);

    @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    @Update("UPDATE posts SET view_count = view_count + #{newCount} WHERE id = #{id}")
    int updateViewCount(Long id, int newCount);

    @Update("UPDATE posts SET like_count =  #{newCount}  WHERE id = #{id}")
    int updateLikeCount(Long id,int newCount);

    @Update("UPDATE posts SET comment_count = comment_count + #{addCount} WHERE id = #{postId}")
    int updateCommentCount(Long postId, Integer addCount);

    /**
     * 模糊搜索帖子
     * 根据关键词在标题和内容中进行模糊搜索
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param size 每页大小
     * @return 帖子列表
     */
    @Select("SELECT * FROM posts WHERE status = 1 AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    List<Post> searchPosts(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    /**
     * 获取搜索结果总数
     * @param keyword 搜索关键词
     * @return 总数
     */
    @Select("SELECT COUNT(*) FROM posts WHERE status = 1 AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%'))")
    int countSearchResults(@Param("keyword") String keyword);
}