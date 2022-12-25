# MutsaSNS

## URL
### FE
> http://ec2-43-200-183-93.ap-northeast-2.compute.amazonaws.com

### BE
> http://ec2-43-200-183-93.ap-northeast-2.compute.amazonaws.com:8080

### 데모 로그인

> ID : user1
>
> PW : user1234
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
"password": "String",
}
```

**Response Body**
```json
{
    "resultCode": "SUCCESS",
    "result": {
        "userId": Number,
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
    "userId": Number,
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
    "totalElements": Number,
    "totalPages": Number,
    "size": Number,
    "number": Number,
    "first": boolean,
    "sort": Sort,
    "numberOfElements": Number,
    "empty": boolean
  }
}
```

#### 상세 조회
`GET /api/v1/posts/{id}`

**Response Body**
```json
{
  "id": Number,
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
        "postId": Number
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
        "postId": Number
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
        "postId": Number
    }
}
```
<br>