<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>地图管理</title>
    <script type="text/javascript" src="../js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="../js/loginData.js"></script>
    <script type="text/javascript">	
    	$(document).ready(function() {
			if (!LoginData.check("loginTime")){
		   		alert("you must login");
		   		window.location.href = '../index.jsp';
			}
    		 $("#drawMap").click(function(){
				if (!LoginData.check("loginTime")){
			   		alert("you must login");
			   		window.location.href = '../index.jsp';
				}
    			var str_data = $("#checkBoxs input").map(function(){
			  		return ($(this).attr("id") + '=' +$(this).attr("checked"));
			  	}).get().join("&");
			  	//alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpDrawMap",
				  	data: str_data,
				  	success: function(msg){
				  	    alert("绘制成功");
				  		window.location.href = './mapShow.jsp';
			        }
			   });
			});
			$("#history").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			window.location.href = './mapShow.jsp';
			});
			$("#selectAll").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var showStr = $(this).attr("value");
    			var nextFlag = false;
    			if (showStr == "全选"){
    				$(this).attr("value","反选");
    				nextFlag = true;
    			}else{
    				nextFlag = false;
    				$(this).attr("value","全选");
    			}
    			$("#res_check").attr("checked",nextFlag);
    			$("#monster_check").attr("checked",nextFlag);
    			$("#role_city_check").attr("checked",nextFlag);
    			$("#fortress_check").attr("checked",nextFlag);
    			$("#barracks_check").attr("checked",nextFlag);
    			$("#union_city_check").attr("checked",nextFlag);
    			$("#ectype_check").attr("checked",nextFlag);
    			$("#role_move_check").attr("checked",nextFlag);
    			$("#role_garrison_check").attr("checked",nextFlag);
    			$("#union_build_check").attr("checked",nextFlag);
    			$("#data_check").attr("checked",nextFlag);
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
  		<form id="checkBoxs" method="get">
		  	<br>
		  	<br>
		  	资源<input type="checkbox" id="res_check" />
		  	怪物<input type="checkbox" id="monster_check" />
		  	主城<input type="checkbox" id="role_city_check" />
		  	要塞<input type="checkbox" id="fortress_check" />
		  	军营<input type="checkbox" id="barracks_check" />
		  	城市<input type="checkbox" id="union_city_check" />
		  	副本<input type="checkbox" id="ectype_check" />
		  	迁城点<input type="checkbox" id="role_move_check" />
		  	玩家据点<input type="checkbox" id="role_garrison_check" />
		  	联盟建筑<input type="checkbox" id="union_build_check" />
		  	数据层<input type="checkbox" id="data_check" />
		  	<input id="selectAll" type="button" value="全选">
		  	<input id="history" type="button" value="历史">
		  	<input id="drawMap" type="button" value="绘制">
	  	</form>
    </div>
  </body>
</html>
