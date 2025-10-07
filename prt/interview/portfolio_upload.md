도메인 : 인터뷰
설명 : 포트폴리오를 입력하는 API 입니다.
HttpMethod : POST
URL : /interview/portfolio
권한 : 사용자, 운영자

# Request

### Body

```json
{
	"file": File
}
```

# Response (Success)

### Body

```json
{
	"status": 200,
	"message": "기본 포트폴리오를 설정했습니다.",
}
```

# Response (Fail)

### Body

```json
{
	"status": 400,
	"message": "해당 파일의 형식이 올바르지 않습니다."
}
```

```json
{
	"status": 401,
	"message": "당신은 이 프로그램의 사용자가 아닙니다."
}
```