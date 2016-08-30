<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>红警后台登录</title>
    <script type="text/javascript" src="/js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="/js/loginData.js"></script>
    <script type="text/javascript">
		var alertFalg = false;
		function show_tips(tips){
			alertFalg = true;
		    alert(tips);
		};
		function loginGm(){
			 var account = $("#account").val();
			 if (account == ""){
			 	show_tips("请输入你的账号");
			 	return;
			 }
			 var pwd = $("#pwd").val();
			 if (pwd==""){
			 	show_tips("请输入你的密码");
			 	return;
			 }
			 var params = "account=" + account + "&pwd=" + pwd;
			 $.ajax({
			  	type: "get",
			  	url: "./gm/HttpGmLogin",
			  	data: params,
			  	success: function(msg){
			  		var resultData = eval("("+msg+")");
			  		if (resultData.result == "ok"){
			  		   var nowTime = new Date();
			  		   LoginData.set("loginTime",nowTime.getTime());
			  		   window.location.href = './gm/main.jsp';
			  		}else{
			  		   show_tips(resultData.result);
			  		}
		        }
			});
		};
		
		document.onkeyup = function(event){ 
    	    var currentEvent = event || window.event || arguments.callee.caller.arguments[0]; 
    	    if(currentEvent && currentEvent.keyCode == 13){//enter 键
    	        if (alertFalg == true){
    	        	alertFalg = false;
    	        }else{
    	       		loginGm();
    	        }
    	    }
    	};
    	
    	$(document).ready(function() {
    	    LoginData.remove("loginTime");
    		$("#login").click(function(){
			    loginGm();
		    });
    	});
    </script>
  </head>
  <body>
    <div align="middle">
    	<br>
    	<br>
    	 红警后台登录
    	<br>
    	<br>
    	<img src="./image/bg.jpg">
    	<br>
    	<br>
    	<form id="login_form" method="get">
	    	账号 : <input type="text" id="account" placeholder="请输入您的账号"/>
	    	<br>
	    	<br>
	    	密码 : <input type="password" id="pwd" placeholder="请输入您的密码"/>
	    	<br>
	    	<br>
	    	<input id="login" type="button" value="登  录">
  	  	</form>
    </div>
  </body>
</html>
