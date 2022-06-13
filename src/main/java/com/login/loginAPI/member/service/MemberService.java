package com.login.loginAPI.member.service;


import com.login.loginAPI.global.exception.exceptions.CustomException;
import com.login.loginAPI.global.exception.exceptions.error.ErrorCode;
import com.login.loginAPI.member.dto.JoinRequest;
import com.login.loginAPI.member.dto.LoginRequest;
import com.login.loginAPI.member.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberService {
    private final MemberMapper memberMapper;


    public MemberService(MemberMapper memberMapper){
        this.memberMapper = memberMapper;
    }

    public void login(LoginRequest loginRequest){

        log.info("[MemberMapper] :::: ========= loginRequest  = " + loginRequest);

        if(!memberMapper.login(loginRequest)){
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        memberMapper.insertLoginTime(loginRequest.getId());
    }

    public void join(JoinRequest joinRequest){

        try{
            memberMapper.join(joinRequest);
        }catch (Exception e){
            throw new CustomException(ErrorCode.JOIN_FAIL);
        }

    }

    public boolean idCheck(String id){
        return memberMapper.checkId(id);
    }
}
