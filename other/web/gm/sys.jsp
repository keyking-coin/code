<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>系统管理</title>
    <script type="text/javascript" src="../js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="../js/loginData.js"></script>
    <script type="text/javascript">	
    	$(document).ready(function() {
    	     if (!LoginData.check("loginTime")){
    	     	 alert("you must login");
    		   	 window.location.href = '../index.jsp';
    	     }
    		 $("#closeService").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			if(!window.confirm('确定关闭服务器？')){
                 	return;
              	}
			    $.ajax({
				  	type: "get",
				  	url: "./HttpSysMgr",
				  	data: "action=close",
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#loadFile").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var str_data = $("#loadType").attr("name") + "=" + $("#loadType").val() + "&action=loadFile"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpSysMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#sendEmail").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var str_data = "content=" + $("#email_content").val() + "&" +
    			"action=sendEmail"; 
			    $.ajax({
				  	type: "get",
				  	url: "./HttpSysMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#sendNoticeMsg").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var str_data = "content=" + $("#notice_content").val() + "&" + 
    			"priorityLevel=" + $("#priorityLevel").val() + "&" +
    			"action=sendNoticeMsg"; 
			    $.ajax({
				  	type: "get",
				  	url: "./HttpSysMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
    	});    	
    </script>
  </head>
  <body>
    <img align="AbsMiddle" src="../image/main.png" width="32" height="32" >
  	&nbsp;
  	<a href="main.jsp" title="首页" >
      <span>首页</span>
  	</a>  
  	<div align="left">
      <br>
	  <br>
	  <br>
	  <input id="closeService" type="button" value="关闭服务器">
	  <br>
	  <br>
	    重新加载文件：<select name="loadType" id="loadType">
	     <option value='sys'>系统文件</option>
	     <option value='json'>Json文件</option>
	     <option value='disallow'>禁言文件</option>
	     <option value='activity'>活动文件</option>
	  </select>
	  <input id="loadFile" type="button" value="加载">
	  <br>
	  <br>
	   邮件内容:
	  <br>
	  <textarea id="email_content" rows="5" cols="50"></textarea>
	  <br>
	  <br>
	  <input id="sendEmail" type="button" value="发送">
	  <br>
	  <br>公告的内容:
	  <br>
	  <textarea id="notice_content" rows="5" cols="50"></textarea>
	  <br>
	   <br>
	   公告权限(值越高优先级越高):<select name="priorityLevel" id="priorityLevel">
	     <option value=0>0</option>
	     <option value=1>1</option>
	     <option value=2>2</option>
	     <option value=3>3</option>
	     <option value=4>4</option>
	     <option value=5>5</option>
	     <option value=6>6</option>
	     <option value=7>7</option>
	     <option value=8>8</option>
	     <option value=9>9</option>
	     <option value=10>10</option>
	     <option value=11>11</option>
	  </select>
	  <br>
	  <input id="sendNoticeMsg" type="button" value="发  送">
    </div>
  </body>
</html>
