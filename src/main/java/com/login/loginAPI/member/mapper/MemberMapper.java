package com.login.loginAPI.member.mapper;

import com.login.loginAPI.member.dto.JoinRequest;
import com.login.loginAPI.member.dto.LoginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface MemberMapper {

    boolean login(LoginRequest loginRequest);

    int join(JoinRequest joinRequest);

    boolean checkId(String id);

    int insertLoginTime(String id);

    int insertLogoutTime(String id);
}
