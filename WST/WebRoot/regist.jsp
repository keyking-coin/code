<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>用户注册</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" src="<%=path%>/js/jquery-2.1.1.min.js"></script>
  	<script type="text/javascript">
  	    $(document).ready(function(){
  	    	$("#couldLogin").hide();
  	    });
		function regist(){
			if (registForm.account.value == ""){
				alert("请输入用户名");
				return;
			}
			if (registForm.pwd.value == ""){
				alert("请输入密码");
				return;
			}
			if (registForm.re_pwd.value == ""){
				alert("请输入确认密码");
				return;
			}
			if (registForm.pwd.value != registForm.re_pwd.value){
				alert("两次输入不匹配");
				return;
			}
			var params = "account=" + registForm.account.value + "&pwd=" + registForm.pwd.value;
		    $.ajax({
			  	type: "post",
			  	url: "./user/regist?" + params,
			  	//contentType: "application/json;charset=UTF-8",
			    dataType : "json",
			  	success: function(data){
			  		if (data.result == 1){
			  			alert("注册成功");
			  			$("#couldLogin").show();
			  		}else{
			  			alert("注册失败");
			  		}
		        },
		        error: function (msg) {
		            alert(msg);
		        }
		   });
		}
		
		function backLogin(){ 
			location.href="index.jsp"; 
			return true; 
		}
	</script>
  </head>
  <body>
  	<center>
  		<form name="registForm">
  			<p>账号：<input type="text" size="10" name="account"/> 
  			<p>密码: <input type="password" size="10" name="pwd"/> 
  			<p>确认密码: <input type="password" size="10" name="re_pwd"/> 
  			<p>
  			<input type="button" value="提交" onclick="regist()" />
  			<input type="button" value="返回" onclick="backLogin()" />
  		</form>
  		<div id="couldLogin">
  			<a href="index.jsp">去登录</a>
  		</div>
  	</center>
  </body>
</html>
