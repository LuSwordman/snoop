//package com.zzy.posts.mapper;
//
//import com.zzy.posts.domain.entity.PostVideo;
//import org.apache.ibatis.annotations.*;
//
//import java.util.List;
//
//@Mapper
//public interface PostVideoMapper {
//    @Insert("INSERT INTO post_videos (post_id, video_url, thumbnail_url, duration, width, height, sort_order) " +
//            "VALUES (#{postId}, #{videoUrl}, #{thumbnailUrl}, #{duration}, #{width}, #{height}, #{sortOrder})")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
//    int insert(PostVideo video);
//
//    @Select("SELECT * FROM post_videos WHERE post_id = #{postId} ORDER BY sort_order")
//    List<PostVideo> findByPostId(Long postId);
//
//    @Delete("DELETE FROM post_videos WHERE post_id = #{postId}")
//    int deleteByPostId(Long postId);
//}