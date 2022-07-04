# LoginAPI
![image](https://user-images.githubusercontent.com/78134917/175506879-a53a6606-35d8-4138-9bb5-f347f5f184c7.png)


## USE Case 
- 사용자는 회원가입을 할 수 있다.  
- 사용자는 회원가입 시 아이디 중복 체크를 할 수 있다.   
- 사용자는 회원탈퇴를 할 수 있다.    
- 사용자는 로그인이 가능하다.  

## #1. 요구사항 
1. 회원가입  
2. 로그인

## #2. 기술 Stack
1. Spring-boot  
2. Java 11(open-jdk 11)   
3. Mysql  
4. Redis  

## #3. Table 구성   
**Member Table.**  

<img width="1254" alt="스크린샷 2022-06-08 오후 5 20 01" src="https://user-images.githubusercontent.com/78134917/172568210-9bd4cc59-18ff-4540-8c38-5b1d87d24201.png">
 
**Login History Table.**  

<img width="1056" alt="스크린샷 2022-06-08 오후 5 21 40" src="https://user-images.githubusercontent.com/78134917/172568463-d27d79d6-cf31-4ed9-98d6-387123c7ec4f.png">
 
 
 ## #4. 설명 
 2022/04 ~ 2022/05
**#Spring #Java #Mybatis #Mysql #Redis**  
  
  
**#1. 확장성을 위한 JWT token 사용**  

클라이언트와의 연결은 HTTP 통신을 기반으로 하기 때문에 기본적으로 connectless,stateless하여 로그인 정보를 효과적으로 유지할 수 없었습니다. 이 문제를 해결하기 위해 JWT와 Session을 고민하였습니다. 다중 서버는 확장성이 중요한데 Session을 사용할 경우 확장성이 떨어진다고 생각했습니다. Disk를 Session 저장소로 사용한 다는 것 자체가 서버를 무상태로 유지하지 않는다는 의미이기 때문입니다. 또한 Session 저장소를 사용할 경우 별도의 자원이 추가적으로 필요하기 때문에 부하가 더 발생할 수 있다고 생각햇습니다.  
   
이에 반해 JWT의 경우 클라이언트가 서버로 부터 발급받은 토큰을 로그인 인증이 필요한 요청과 함께 토큰을 보내게되면 서버에서는 토큰의 유효성에 대해 검증하고 결과에 따라 처리하면됩니다. 다른 자원에 접근할 일이 없어 추가적인 부하가 발생하지 않습니다. 또한 토큰을 만드는데 필요한 필드가 많지 않다면 주고받는 토큰의 크기는 가벼워지기 때문에 부담없이 사용이 가능합니다. 이러한 것들 때문에 서버가 확장된다해도 여러 서버에서 동일한 로그인 처리가 가능했습니다.  
   
하지만 JWT에서 Access Token만 사용할 경우 탈취로 인해 보안을 보장할 수 없다고 생각했습니다. 그래서 Refresh Token을 추가적으로 사용하기로 하였습니다. 이 토큰은 요청자의 로그인 유지 기간을 의미합니다. 로그인 인증에 사용하는 Access Token을 짧게 두고 만료되면 Refresh Token의 유효성 검사를 하고 유효하다면 추가 로그인 없이 새로운 Access Token을 요청한 데이터와 함께 요청자에게 반환하도록 구성했습니다. 
  
**#2. Redis를 이용한 Refresh Token 관리**  
  
Refresh Token을 클라이언트에서 관리하기보다는 클라이언트에게는 토큰과 관계없는 키값을 주고 키값을 통해 Refresh Token을 서버 사이드에서 관리하면 클라이언트 사이드에서 관리하는 것 보다 더 안전하다고 생각 했습니다. 그래서 Refresh Token를 서버에서 관리하기로 결정했습니다.
  
우선 Refresh Token은 영속성을 유지할 필요가 없는 데이터이기 때문에 Disk DB를 고려하지 않았습니다. 또한 In-MemoryDB는 RAM을 메모리로 사용하기 때문에 Disk를 사용하는 DiskDB에 비해 IO부하도 적고 처리 속도도 배로 빨랐습니다. 다양한 In-MemoryDB 솔루션들이 존재했지만 대표적으로 사용되는 Memcached와 Redis가 있었습니다. Memcached는 Redis에 비해 지원하는 기능이 적었기 때문에 Redis 를 사용하였습니다.   
  
**#3. Spring Security의 BcryptPassowrdEncoder를 사용한 암호화**    
  

일반적으로 해쉬 함수를 사용해서 단방향 해싱을 하는 것 보다 여러가지 공격에 대비하도록 만들어진 Bcrypt를 사용하는 것이 더 안전하다고 판단했습니다. 내용은 [여기](https://github.com/jhd7130/AtomLab/blob/main/mentoring/%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8%20%EC%A0%80%EC%9E%A5%20Bcrypt.md)에 자세히 적혀 있습니다.  
  

   
   
## #5. 기능 테스트  
 ### 로그인 기능.  
case 1. 로그인 성공(액세스토큰, 리프레시토큰 반환) 
 <img width="1294" alt="스크린샷 2022-06-08 오후 5 32 28" src="https://user-images.githubusercontent.com/78134917/172570665-401a484c-c7de-4cf3-bf61-8525a38caafa.png">.  
case 2. 엑세스 토큰 입력 후 로그인 성공 
<img width="1295" alt="스크린샷 2022-06-08 오후 5 34 20" src="https://user-images.githubusercontent.com/78134917/172571191-d88df01d-cf0f-4fff-937d-88347832554e.png">.  

 ### 로그아웃 기능.  
 <img width="1295" alt="스크린샷 2022-06-08 오후 5 37 16" src="https://user-images.githubusercontent.com/78134917/172571771-52a49e83-021a-4dad-b978-d04116022b7d.png">.  
  
 
 ### 회원가입 기능.   
 <img width="1291" alt="스크린샷 2022-06-08 오후 5 30 22" src="https://user-images.githubusercontent.com/78134917/172570259-8054ba3f-4fce-42ff-9e3d-16c131e9fcf3.png">.  
<img width="460" alt="스크린샷 2022-06-08 오후 5 30 43" src="https://user-images.githubusercontent.com/78134917/172570308-1b9eba03-a02d-4306-876e-0c1b4919a9ef.png">.  

 
