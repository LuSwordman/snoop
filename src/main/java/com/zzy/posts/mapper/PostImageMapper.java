//package com.zzy.posts.mapper;
//
//import com.zzy.posts.domain.entity.PostImage;
//import org.apache.ibatis.annotations.*;
//
//import java.util.List;
//
//@Mapper
//public interface PostImageMapper {
//    @Insert("INSERT INTO post_images (post_id, image_url, width, height, sort_order) " +
//            "VALUES (#{postId}, #{imageUrl}, #{width}, #{height}, #{sortOrder})")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
//    int insert(PostImage image);
//
//    @Select("SELECT * FROM post_images WHERE post_id = #{postId} ORDER BY sort_order")
//    List<PostImage> findByPostId(Long postId);
//
//    @Delete("DELETE FROM post_images WHERE post_id = #{postId}")
//    int deleteByPostId(Long postId);
//}