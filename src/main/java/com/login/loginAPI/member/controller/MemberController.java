package com.login.loginAPI.member.controller;


import com.login.loginAPI.global.exception.exceptions.CustomException;
import com.login.loginAPI.global.exception.exceptions.error.ErrorCode;
import com.login.loginAPI.global.security.jsonwebtoken.JwtProvider;
import com.login.loginAPI.member.dto.JoinRequest;
import com.login.loginAPI.member.dto.LoginRequest;
import com.login.loginAPI.member.mapper.MemberMapper;
import com.login.loginAPI.member.service.MemberService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final MemberMapper memberMapper;

    public MemberController(MemberService memberService, JwtProvider jwtProvider, MemberMapper memberMapper){
        this.memberService = memberService;
        this.jwtProvider = jwtProvider;
        this.memberMapper = memberMapper;
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest, HttpServletResponse response, HttpSession session){

        // 로그인이 안되면 서비스에서 Exeption발생
        memberService.login(loginRequest);

       String accessToken = jwtProvider.createAccessToken(loginRequest.getId(),loginRequest.getPassword());
       String refreshToken = jwtProvider.createRefreshToken(loginRequest.getId(),loginRequest.getPassword());

        /**
         * Redis session에 id를 key값으로, value 값으로 regfreshToken을 저장
         * refresh_token이 필요할 때는 session에서 값 반환
         */

       response.setHeader("ACCESS_TOKEN",accessToken);
       response.setHeader("REFRESH_TOKEN_KEY",loginRequest.getId());
       loginRequest.setRefreshToken(refreshToken);

       log.info(loginRequest.toString());
       session.setAttribute(loginRequest.getId(),loginRequest);

       return new ResponseEntity(HttpStatus.OK);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response){

        Claims claims = jwtProvider.getInformation(request.getHeader("ACCESS_TOKEN"));
        response.setHeader("ACCESS_TOKEN","");

        memberMapper.insertLogoutTime((String)claims.get("id"));

        return new ResponseEntity("로그아웃 되었습니다.",HttpStatus.OK);
    }

    //회원가입
    @PostMapping("/join")
    public ResponseEntity join(@RequestBody JoinRequest joinRequest){

        if(memberService.idCheck(joinRequest.getId())){
            throw new CustomException(ErrorCode.DUPLICATE_ID);
        }

        memberService.join(joinRequest);

        return new ResponseEntity("성공적으로 가입을 완료했습니다.",HttpStatus.OK);
    }


    /**
     * Google Login Section
     */
}

