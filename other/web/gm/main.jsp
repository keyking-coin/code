<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  	<style type="text/css">
		.mask{margin:0;padding:0;border:none;width:100%;height:100%;background:#333;opacity:0.6;filter:alpha(opacity=60);z-index:9999;position:fixed;top:0;left:0;display:none;}
		#LoginBox{position:absolute;left:460px;top:150px;background:white;width:426px;height:282px;border:3px solid #444;border-radius:7px;z-index:10000;display:none;}
		.row1{background:#f7f7f7;padding:0px 20px;line-height:50px;height:50px;font-weight:bold;color:#666;font-size:20px;}
		.row{height:77px;line-height:77px;padding:0px 30px;font-family:华文楷体;font-size:x-large;}
		.close_btn{font-family:arial;font-size:30px;font-weight:700;color:#999;text-decoration:none;float:right;padding-right:4px;}
		.inputBox{border:1px solid #c3c3c3;padding:1px 3px 6px 3px;border-radius:5px;margin-left:5px;}
		#tokenPwd{height:27px;width:230px;border:none;}
		#tokenLogic{color:White;background:#4490f7;text-decoration:none;padding:10px 95px;margin-left:87px;margin-top:40px;border-radius:5px;opacity:0.8;filter:alpha(opacity=80);}
		.warning{float:right;color:Red;text-decoration:none;font-size:20px;font-weight:bold;margin-left:20px;display:none;}
	</style>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>红警后台</title>
    <script type="text/javascript" src="../js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="../js/loginData.js"></script>
    <script type="text/javascript">
		$(document).ready(function() {
		    $("#LoginBox").fadeOut("fast");
			if (!LoginData.check("loginTime")){
		   		alert("you must login");
		   		window.location.href = '../index.jsp';
			}
    	});
    </script>
    <script type="text/javascript">
		$(function ($) {
			//弹出验证
			$("#sysToken").hover(function () {
				$(this).stop().animate({
					opacity: '1'
				}, 600);
			}, function () {
				$(this).stop().animate({
					opacity: '0.6'
				}, 1000);
			}).on('click', function () {
				$("body").append("<div id='mask'></div>");
				$("#mask").addClass("mask").fadeIn("slow");
				$("#LoginBox").fadeIn("slow");
			});
			//按钮的透明度
			$("#loginbtn").hover(function () {
				$(this).stop().animate({
					opacity: '1'
				}, 600);
			}, function () {
				$(this).stop().animate({
					opacity: '0.8'
				}, 1000);
			});
			//文本框不允许为空---按钮触发
			$("#tokenLogic").click(function (){
				var tokenCode = $("#tokenPwd").val();
				if (tokenCode == "" || tokenCode == undefined || tokenCode == null) {
					alert("口令不能为空");
				}else{
					var str_data = "tokenCode=" + tokenCode + "&action=token"; 
				    //alert(str_data);
				    $.ajax({
					  	type: "get",
					  	url: "./HttpSysMgr",
					  	data: str_data,
					  	success: function(msg){
					  		var resultData = eval("("+msg+")");
					  		if	(resultData.result == "ok"){
					  			 window.location.href = './sys.jsp';
					  		}else{
					  			alert(resultData.result);
					  		}
				        }
				   });
				}
			});
			//关闭
			$(".close_btn").hover(function () { 
					$(this).css({ color: 'black' }) 
				}, function () {
					$(this).css({ color: '#999'}) 
				}).on('click', function () {
				$("#LoginBox").fadeOut("fast");
				$("#mask").css({display: 'none'});
			});
		});
	</script>
  </head>
  <body>
    <img align="AbsMiddle" src="../image/login.png" width="32" height="32" >
  	&nbsp;
  	<a href="../index.jsp" title="返回登录" >
      <span>返回登录</span>
  	</a>
  	<div align="left" >
  	  <br/>
  	  <br/>
  	  <img align="AbsMiddle" src="../image/role.png" width="32" height="32">
      &nbsp;
      <a href="role.jsp" title="玩家管理" >
	      <span>玩家管理</span>
      </a>
      <br/>
      <br/>
      <img align="AbsMiddle" src="../image/sys.png" width="32" height="32" >
      &nbsp;
      <a href="#" id="sysToken" title="系统管理" >
	      <span>系统管理</span>
      </a>
      <br/>
      <br/>
      <img align="AbsMiddle" src="../image/map.png" width="32" height="32" >
      &nbsp;
      <a href="map.jsp" title="地图系统" >
	      <span>地图系统</span>
      </a>
      <br/>
      <br/>
      <img align="AbsMiddle" src="../image/fight.png" width="32" height="32" >
      &nbsp;
      <a href="../battle/battle.jsp" title="战斗系统" >
	      <span>战斗系统</span>
      </a>
      <br/>
      <br/>
      <img align="AbsMiddle" src="../image/debug.jpg" width="32" height="32" >
      &nbsp;
      <a href="debug.jsp" title="调试系统" >
	      <span>调试系统</span>
      </a>
    </div>
    <div id="LoginBox">
        <div class="row1">
                  系统管理验证<a href="javascript:void(0)" title="关闭窗口" class="close_btn" id="closeBtn">×</a>
        </div>
        <div class="row">
                      系统口令: 
            <span class="inputBox">
                <input type="password" id="tokenPwd"/>
            </span>
        </div>
        <div class="row">
            <a href="#" id="tokenLogic">验&nbsp;&nbsp;&nbsp;&nbsp;证</a>
        </div>
    </div>
  </body>
</html>
