<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>登录界面</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" src="<%=path%>/js/jquery-2.1.1.min.js"></script>
  	<script type="text/javascript">
		function on_submit(){ 
			if(loginForm.username.value == ""){ 
				alert("用户名不能为空，请重新输入！"); 
				return false; 
			} else if(loginForm.userpassword.value == ""){ 
				alert("密码不能为空，请重新输入！"); 
				return false; 
			}
			return true;
		}
		
		function login(){
			if (!on_submit()){
				return;
			}
			var params = "account=" + loginForm.username.value + "&pwd=" + loginForm.userpassword.value;
		    $.ajax({
			  	type: "post",
			  	url: "./user/login?" + params,
			  	//contentType: "application/json;charset=UTF-8",
			    dataType : "json",
			  	success: function(data){
			  		if (data.result == 1){
			  			location.href="main.jsp"; 
			  		}else{
			  			alert("账户或密码不正确");
			  		}
		        },
		        error: function (msg) {
		            alert(msg);
		        }
		   });
		}
		
		function goRegist(){ 
			location.href="regist.jsp"; 
			return true; 
		}
	</script> 
  </head> 

  <body> 
    <center>
	  <form name="loginForm"> 
		<p>账&nbsp;&nbsp;号&nbsp;:&nbsp;<input type="text" size="10" name="username"/> 
		<p>密&nbsp;&nbsp;码&nbsp;:&nbsp;<input type="password" size="10" name="userpassword"/> 
		<p>
		<!-- input type="submit" value="提交" name="submitbutton"/-->
		<!--input type="reset" value="重新输入" name="resetbutton"/-->
		<input type="button" value="提交" onclick="login()"/>
		<input type="button" value="注册" onclick="goRegist()"/>
	  </form>
	</center> 
  </body>
</html>
