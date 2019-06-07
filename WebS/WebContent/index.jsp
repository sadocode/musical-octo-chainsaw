<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import= "java.util.Properties" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.IOException" %>
<%! public void setEnvironment() 
{
	try(FileInputStream fis = new FileInputStream("info.properties")) {
		this.info = new Properties();
		this.info.load(fis);
		this.webRoot = info.getProperty("WEBROOT");
	} catch(IOException ioe) {
		ioe.printStackTrace(System.out);
	}
} %>
<!DOCTYPE html>
<html>
  <head>
    <title>webserver</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="./css/styles.css" type="text/css">
    <script src="https://code.jquery.com/jquery-1.11.1.js"></script>
  </head>
  <body>
  
    <h1>WebServer</h1>
    <div class="card">
      <h3>Ozil</h3>
      <img src="./images/ozil.jpg" alt="ozil" class="playerPic">
      <p>link : <a href="https://www.google.com/search?q=ozil&oq=ozil&aqs=chrome.0.69i59l2j0l4.2461j0j7&sourceid=chrome&ie=UTF-8">Ozil link</a></p>
    </div>
    <div class="card">
      <h3>Ramsey</h3>
      <img src="./images/ramsey.jpg" alt="ramsey" class="playerPic">
      <p>link : <a href="./temp/temp.html">Ramsey link</a></p>
    </div>
    <p>
    <div>
      아무 단어나 입력해보세요!<br>
      <form action="/WebS/get/GetWords" method="get">
        <input type="text" name="words">
        <button id="CT" type="submit">보기</button>
      </form>
    </div>
    <form action="/WebS/post/PostImages" method="post" enctype="multipart/form-data">
      <input name="file1" id="file1"type="file" accept="image/*">
      <input name="file2" id="file2" type="file" accept="image/*">
      <button type="submit">전송</button>
    </form>
</body>
</html>
