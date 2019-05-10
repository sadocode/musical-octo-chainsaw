# musical-octo-chainsaw
HTTP webserver

GET, POST 수신 시에 헤더 확인 가능.
response로 cache-control, content-type, e-tag 등 제공

POST에서 이미지를 받을 경우, 계속 이상한 문자가 하나씩 추가됨
> 이미지가 깨짐.
> Base64 인코딩, 디코딩 문제인지 아니면 CRLF 처리 제대로 못 한 건지 아직 모르겠음.
> 처리해야함.

