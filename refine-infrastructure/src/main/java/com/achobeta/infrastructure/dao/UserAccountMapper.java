package com.achobeta.infrastructure.dao;

import com.achobeta.domain.user.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAccountMapper {

    UserEntity selectById(String userId);

    UserEntity selectByAccount(String userAccount);

    void insert(UserEntity user);

    void update(UserEntity user, String account);

}