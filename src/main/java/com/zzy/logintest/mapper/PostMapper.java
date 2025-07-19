package com.zzy.logintest.mapper;



import com.zzy.logintest.domain.dto.PostDto;
import com.zzy.logintest.domain.pojo.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {
    @Insert("INSERT INTO posts (user_id, title, content) VALUES (#{userId}, #{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PostDto postDto);

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

    @Update("UPDATE posts SET like_count = like_count + #{addCount} WHERE id = #{id}")
    int updateLikeCount(Long id,int addCount);

    @Update("UPDATE posts SET comment_count = comment_count + #{addCount} WHERE id = #{postId}")
    int updateCommentCount(Long postId, Integer addCount);
}