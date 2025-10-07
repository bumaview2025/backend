도메인 : 인터뷰
설명 : 자신이 면접 질문에 답변한 리스트를 확인할 수 있습니다.
HttpMethod : GET
URL : /interview/answerlist
권한 : 사용자, 운영자

# Request

### Body

```json

```

# Response (Success)

### Body

```json
{
	"status": 200,
	"message": "답변한 리스트를 불러왔습니다.",
	"contents": [
		{
			"question": {
				"id": 1,
				"question": "회사에서 어떻게 하실건가요?",
				"category": "back"
			},
			"answer": "열심히 하겠습니다!!"
		},
	]
}
```

# Response (Fail)

### Body

```json
{
	"status": 400,
	"message": "포트폴리오를 먼저 등록해주세요."
}
```

```json
{
	"status": 401,
	"message": "당신은 이 프로그램의 사용자가 아닙니다."
}
```