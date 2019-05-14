# musical-octo-chainsaw
HTTP webserver

GET, POST 수신 시에 헤더 확인 가능.
response로 cache-control, content-type, e-tag 등 제공

POST에서 이미지를 받을 경우
> upload한 이미지 파일을 base64로 인코딩
> 인코딩된 값을 form 태그 내 input 태그에 써줌
> upload한 이미지 파일 초기화됨
> base64로 인코딩된 이미지만 서버로 전송됨.
