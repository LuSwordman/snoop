package com.zzy.logintest.service;

import com.zzy.logintest.domain.vo.FriendRelationVO;
import com.zzy.logintest.domain.vo.UserVo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FriendRelationService {

     ResponseEntity<List<FriendRelationVO>> getFriendRelation(String userEmail, int page, int size);
}
