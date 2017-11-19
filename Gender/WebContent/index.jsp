<%@ page language="java" contentType="text/html; charset=UTF-8"
 pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script
 src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

</head>
<body>
 <h1>Upload An Image For Detection</h1>
 <br>
 <p id="result"></p>
 <form method="post" action="/Gender/uploadImage"
  enctype="multipart/form-data">
  Choose a image: <input type="file" name="uploadImage"> <br>
  <br> <input type="submit" name="upload">
 </form>
</body>
</html>