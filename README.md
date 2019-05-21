# musical-octo-chainsaw
HTTP webserver


master----v1.0.0
develop      \\---v1.0.1---v1.0.2

<v1.0.1>
GET, POST 수신 시에 헤더 확인 가능.
response로 cache-control, content-type, e-tag 등 제공

<v1.0.2>
POST에서 이미지를 받을 경우
> upload한 이미지 파일을 base64로 인코딩
> 인코딩된 값을 form 태그 내 input 태그에 써줌
> upload한 이미지 파일 초기화됨
> base64로 인코딩된 이미지만 서버로 전송됨.

<v1.0.3 계획>
request의 Body 부분을 전부 byte로 받음.
> image, text 가리지 않고 한 가지 방법으로 처리하기 위해서
> 5/17 아직 수정중..
> 5/20 bit로 받기 성공. String 변환 좀더 생각
> 5/21 ReadBody.java, MakeFile.java 추가. 이미지 받는부분 조금만 더 다듬으면 됨.
