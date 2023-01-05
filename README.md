# MutsaSNS

### 미션 요구사항 분석 & 체크리스트

---

### 2주차 미션 요약

#### 필수과제

- [x] 댓글
- [ ] 좋아요
- [x] 마이피드
- [ ] 알림
- [ ] Swagger에 ApiOperation을 써서 Controller 설명 보이게 할 것

#### 도전과제

- [ ] 댓글, 좋아요, 알림 UI구현
- [ ] Admin기능

---

#### [특이사항]

- 시큐리티 antMatchers 패턴 중복 문제.
  - /api/v1/posts/{id} -> /api/v1/posts/**
  - /api/v1/posts/my -> /api/v1/posts/my
  - 두 URI의 패턴이 같아 시큐리티 antMatchers 설정 우선 순위가 중요해짐.
  - 해결책
    1. 우선 순위에 따라 설정.
    2. 패턴을 더 세분화해 설정. **[채택]**
  - antMatchers -> regexMatchers 정규식 활용.

**[아쉬운 점]**

**[궁금한 점]**

### 1주차 미션 요약

#### 필수과제

- [x] AWS EC2에 Docker 배포
- [x] Gitlab CI & Crontab CD
- [x] Swagger
- [x] 회원가입
- [x] 로그인
- [x] 포스트 작성, 수정, 삭제, 리스트

#### 도전과제

- [x] 화면 UI 개발
  - [x] 회원가입
  - [x] 로그인
  - [x] 글쓰기
  - [x] 조회
- [ ] ADMIN 회원으로 등급업하는 기능
  - [x] BE
  - [ ] FE
- [ ] ADMIN 회원이 로그인 시 자신이 쓴 글이 아닌 글과 댓글에 수정, 삭제를 할 수 있는 기능
  - [x] BE
  - [ ] FE

---

#### [접근 방법]

- Swagger
  - 토큰이 필요한 Endpoint에만 Authorize 표시하기
    - @Login 어노테이션을 작성하고 권한이 필요한 Controller 메소드에 붙인다.
    - Swagger SecurityContext 설정에서 @Login 어노테이션이 붙은 메소드에만 authorize 표시가 되도록 한다.

- Controller Test
  - Controller Test에 Security를 포함하여 테스트할지, 제외하고 테스트할지 고민.
    - Security를 제외하는 방법.
      - 완전한 Controller 단위 테스트.
    - 기본 Security 설정 값만 포함 하는 방법.
      - WithMockUser or WithAnonymousUser 사용.
    - 직접 작성한 WebSecurityConfig까지 포함 하는 방법. **[채택]**
      - WebSecurityConfig, CustomAuthenticationEntryPoint, CustomAccessDeniedEntryPoint 포함하는 @WebMvcTestWithSecurity 어노테이션 작성.


#### [특이사항]

**[아쉬운 점]**
- Endpoint 이 외의 잘못된 접근
  - 현재 UNAUTHORIZED 401 에러가 발생.
  - 404 NotFound 로 처리 필요.
- 로깅

**[궁금한 점]**
- ControllerTest
  - Controller 테스트에 시큐리티 테스트를 포함해야할까?
  - 시큐리티를 포함
    - 단위 테스트와 거리가 멀어짐.
  - 시큐리티를 포함하지 않으면
    - 권한에 따른 접근 제한 테스트를 어디서 해야하는가
    - 현재 Controller는 Service레이어의 리턴 값을 그대로 반환해주거나 한 번 Wrapping해서 반환해준다.
    - Controller Test가 의미가 있는가.
      - DTO 팩토리 메소드를 테스트하는 것 뿐인 Controller Test가 되는 것이 아닐까
- 로그인
  - UserSerivce에서 비밀번호가 일치한지 확인하고 jwt를 반환하는 방법.
  - 스프링 시큐리티의 UsernamePasswordAuthenticationFilter의 attemptAuthentication를 Override하는 방법.
  - 무엇이 더 나은 방법일까 
- 좋은 로깅이란 무엇인가
  - 어떤 내용을 포함해야할까
  - 접속 IP, HttpMethod, URL, Body?

## URL
### FE
> ~~http://ec2-43-200-183-93.ap-northeast-2.compute.amazonaws.com~~

### BE
> http://api.hmin.site:8080

### Swagger
> http://api.hmin.site:8080/swagger-ui/index.html

### 데모 로그인

**User**
> ID : user1
>
> PW : user1234

**Admin**
> ID : admin
> 
> PW : qwer1234
<br>

## ERD

![ERD](/ERD.png)
<br>

## Endpoint

### User

#### 회원가입
`POST /api/v1/users/join`

**Request Body**
```json
{
  "userName": "String",
  "password": "String"
}
```

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "userId": 0,
    "userName": "String"
  }
}
```
<br>

#### 로그인
`POST /api/v1/users/login`

**RequestBody**
```json
{
  "userName": "String",
  "password": "String"
}
```

**Response Body**
```json
{
  "jwt": "String"
}
```

#### 권한 수정 : Only Admin
`POST /api/v1/users/{id}/role/chang`

**RequestBody**
```json
{
  "role": "user" | "admin"
}
```

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "userId": 0,
    "userName": "String",
    "role": "ROLE_USER" | "ROLE_ADMIN"
  }
}
```
<br>

### Post
#### 리스트 조회
`GET /api/v1/posts`

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "content": PostDetailResponse[],
    "pageable": Pageable,
    "last": boolean,
    "totalElements": 0,
    "totalPages": 0,
    "size": 0,
    "number": 0,
    "first": boolean,
    "sort": Sort,
    "numberOfElements": 0,
    "empty": boolean
  }
}
```

#### 상세 조회
`GET /api/v1/posts/{id}`

**Response Body**
```json
{
  "id": 0,
  "title": "String",
  "body": "String",
  "userName": "String",
  "createdAt": "yyyy-mm-dd hh:mm:ss",
  "lastModifiedAt": "yyyy-mm-dd hh:mm:ss"
}
```

#### 작성 : Authorize
`POST /api/v1/posts`

**Request Body**
```json
{
  "title": "String",
  "body": "String"
}
```

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 등록 완료",
    "postId": 0
  }
}
```

#### 수정 : Authorize
`PUT /api/v1/posts/{id}`

**Request Body**
```json
{
  "title": "String",
  "body": "String"
}
```

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 수정 완료",
    "postId": 0
  }
}
```

#### 삭제 : Authorize
`DELETE /api/v1/posts/{id}`

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 삭제 완료",
    "postId": 0
  }
}
```

#### 마이피드 : Authorize
`GET /api/v1/posts/my`

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
      "content": PostDetailResponse[],
  "pageable": Pageable,
  "last": boolean,
  "totalElements": 0,
  "totalPages": 0,
  "size": 0,
  "number": 0,
  "first": boolean,
  "sort": Sort,
  "numberOfElements": 0,
  "empty": boolean
  }
}
```
<br>

### Comment
#### 작성 : Authorize
`POST /api/v1/posts/{postId}/comments`

**Request Body**
```json
{
  "comment" : "String"
}
```

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result":{
    "id": 0,
    "comment": "String",
    "userName": "String",
    "postId": 0,
    "createdAt": "yyyy-mm-dd hh:mm:ss"
  }
}
```

#### 특정 게시물 댓글 조회
`GET /posts/{postId}/comments[?page=0]`

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "content": CommentDetailResponse[],
    "pageable": Pageable,
    "last": boolean,
    "totalElements": 0,
    "totalPages": 0,
    "size": 0,
    "number": 0,
    "first": boolean,
    "sort": Sort,
    "numberOfElements": 0,
    "empty": boolean
  }
}
```

#### 댓글 수정 : Authorize
`PUT /posts/{postId}/comments/{id}`

**Request Body**
```json
{
  "comment" : "String"
}
```

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result":{
    "id": 0,
    "comment": "String",
    "userName": "String",
    "postId": 0,
    "createdAt": "yyyy-mm-dd hh:mm:ss",
    "lastModifiedAt": "yyyy-mm-dd hh:mm:ss"
  }
}
```

#### 댓글 삭제 : Authorize
`DELETE /posts/{postId}/comments/{id}`

**Response Body**
```json
{
  "resultCode": "SUCCESS",
  "result":{
    "message": "댓글 삭제 완료",
    "id": 0
  }
}
```