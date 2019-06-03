# musical-octo-chainsaw


##HTTP webserver

###v1.0.1
GET, POST 수신 시에 헤더 확인 가능.
response로 cache-control, content-type, e-tag 등 제공

###v1.0.2
POST에서 이미지를 받을 경우

> upload한 이미지 파일을 base64로 인코딩

> 인코딩된 값을 form 태그 내 input 태그에 써줌

> upload한 이미지 파일 초기화됨

> base64로 인코딩된 이미지만 서버로 전송됨.

###v1.0.3 계획
request의 Body 부분을 전부 byte로 받음.
> image, text 가리지 않고 한 가지 방법으로 처리하기 위해서

> 5/17 아직 수정중..

###v1.1
> 5/20 bit로 받기 성공. String 변환 좀더 생각

> 5/21 ReadBody.java, MakeFile.java 추가. 이미지 받는부분 조금만 더 다듬으면 됨.

> 5/22 RequestParser.java ResponseBuilder.java, ByteProcessing.java 추가, NewServerThread.java 수정.     객체지향화 중.  그리고 이미지 이제 수신 잘 됨. 60KB 이하는 다 됨.

###v1.2
> 5/24 용량이 큰파일도 수신 성공. 좀만 다듬으면 됨.



##webserver2

>아직 구현 X. 백차장님 말씀하신 것 구현중

>6/3. dir, notepad, screenshot 구현 완료. OOP, thread-pool, file io처리 필요

##test

>test파일 모음
