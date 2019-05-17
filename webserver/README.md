main에서 request를 read()로 byte단위로 읽은 값을 byte[] reqeust에 저장

rq - parameter로 byte[] req 받음
	받은 req를 byte[] request에 arraycopy

	int indexStart	request를 읽을 때, lineByte에 copy해주기 위해 알아야하는 시작 index	

	int index 		temp변수.
	
	int lineNumber 	String readLine이 몇 번째인지 확인하기 위해 설정. lineNumber는 1부터 시작함.
	
	byte[] lineByte	newLine이 true 면 request의 indexStart~indexEnd 만큼을 lineByte에 arraycopy
	
	String readLine	request를 
	
	StringTokenizer st 	request의 first라인에서 method, filePath, protocol 구분하기위해 생성.
	
	boolean newLine	request에 \r\n이 나오면 newLine값을 true로 변경. 나머지는 for문이 1번 돌때마다 false로 변경.

	
	for (; ; ) {
		byte[] request를 0번부터 끝까지 읽음.


	}
