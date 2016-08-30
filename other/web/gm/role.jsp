<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>玩家管理</title>
    <script type="text/javascript" src="../js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="../js/loginData.js"></script>
    <script type="text/javascript" src="../js/role.js"></script>
    <script type="text/javascript">	
		function res_select(){
			var r_n = $("#res_name").val();
			if (r_n == "food" || r_n == "metal" || r_n == "oil" || r_n == "alloy"){
				$("#res_city_text").show();
				$("#res_cityId").show();
			}else{
				$("#res_city_text").hide();
				$("#res_cityId").hide();
			}
		}
		
		function link_Level(){
		    var index = $(troops_id).find("option:selected").attr("grade");
        	$(troops_level).empty();
			var linkLevels = levels[index];
 			for (var i = 0 ; i < linkLevels.length ; i++){
 				$(troops_level).append(linkLevels[i]);
 			}
		};
		
    	$(document).ready(function() {
			if (!LoginData.check("loginTime")){
		   		alert("you must login");
		   		window.location.href = '../index.jsp';
			}
    		for(var i = 0 ; i < items.length ; i++) {
	 			$("#item_name").append(items[i]);
			}
			for(var i = 0 ; i < equips.length ; i++) {
	 			$("#equip_name").append(equips[i]);
			}
			for(var i = 0 ; i < stones.length ; i++) {
	 			$("#stone_name").append(stones[i]);
			}
			for(var i = 0 ; i < reses.length ; i++) {
	 			$("#res_name").append(reses[i]);
			}
			for(var i = 0 ; i < armys.length ; i++) {
	 			$("#troops_id").append(armys[i]);
			}
			for(var i = 0 ; i < builds.length ; i++) {
	 			$("#build_operation").append(builds[i]);
			}
			$("#addGold").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#gold_uid").attr("name") + "=" + $("#gold_uid").val() + "&" +
			    	$("#gold_num").attr("name") + "=" + $("#gold_num").val() + "&action=addGold"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#clearGold").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#gold_uid").attr("name") + "=" + $("#gold_uid").val() + "&action=clearGold"; 
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#addItem").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#item_uid").attr("name") + "=" + $("#item_uid").val() + "&" +
			        $("#item_name").attr("name") + "=" + $("#item_name").val() + "&" +
			    	$("#item_num").attr("name") + "=" + $("#item_num").val() + "&action=addItem"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#addEquip").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#equip_uid").attr("name") + "=" + $("#equip_uid").val() + "&" +
			        $("#equip_name").attr("name") + "=" + $("#equip_name").val() + "&" +
			    	$("#equip_num").attr("name") + "=" + $("#equip_num").val() + "&action=addEquip"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});	
			$("#addStone").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#stone_uid").attr("name") + "=" + $("#stone_uid").val() + "&" +
			        $("#stone_name").attr("name") + "=" + $("#stone_name").val() + "&" +
			    	$("#stone_num").attr("name") + "=" + $("#stone_num").val() + "&action=addStone"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#addRes").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#res_uid").attr("name") + "=" + $("#res_uid").val() + "&" +
			        $("#res_name").attr("name") + "=" + $("#res_name").val() + "&" +
			        $("#res_operation").attr("name") + "=" + $("#res_operation").val() + "&" +
			        $("#res_cityId").attr("name") + "=" + $("#res_cityId").val() + "&" +
			    	$("#res_num").attr("name") + "=" + $("#res_num").val() + "&action=addRes"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#kickRole").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#kick_uid").attr("name") + "=" + $("#kick_uid").val() + "&action=kickRole"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#buildUp").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = "build_uid=" + $("#build_uid").val() + "&" +
			        "build_cityId=" + $("#build_cityId").val() + "&" +
			    	"build_num=" + $("#build_num").val() + "&" +
			    	"build_operation=" + $("#build_operation").val() + "&" +
			    	"action=buildUp";
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#addSkill").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#skill_uid").attr("name") + "=" + $("#skill_uid").val() + "&" +
			    	$("#skill_num").attr("name") + "=" + $("#skill_num").val() + "&action=addSkill"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#changeVip").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#vip_uid").attr("name") + "=" + $("#vip_uid").val() + "&" +
			    	$("#vip_num").attr("name") + "=" + $("#vip_num").val() + "&action=changeVip"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#clcGuides").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = $("#guide_uid").attr("name") + "=" + $("#guide_uid").val() + "&action=clcGuides"; 
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#troopsChange").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			if($("#troops_num").val() > 10000 && $("#troops_num").val() != 8000001){
    				alert("军队不允许这么多,你养得活吗? (数量不能大于10000)");
    				return;
    			}
			    var str_data = "troops_uid=" + $("#troops_uid").val() + "&" +
			    			   "troops_cityId=" + $("#troops_cityId").val() + "&" +
			    	           "troops_id=" + $("#troops_id").val() + "&" +
			    	           "troops_level=" + $("#troops_level").val() + "&" +
			    	           "troops_num=" + $("#troops_num").val() + "&" +
			    	           "troops_operation=" + $("#troops_operation").val() + "&" +
			    	           "action=troopsChange";
			    //alert(str_data);
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
			$("#bag_clear").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
			    var str_data = "bag_clear_uid=" + $("#bag_clear_uid").val() + "&" +
			    			   "bag_clear_type=" + $("#bag_clear_type").val() + "&" +
			    	           "action=bag_clear";
			    $.ajax({
				  	type: "get",
				  	url: "./HttpGmRoleMgr",
				  	data: str_data,
				  	success: function(msg){
				  		var resultData = eval("("+msg+")");
				  		alert(resultData.result);
			        }
			   });
			});
    	});
    	function setOtherTextUid(x){
    		var y = document.getElementById(x).value;
			gold_uid.value=y;
			item_uid.value=y;
			equip_uid.value=y;
			stone_uid.value=y;
			res_uid.value=y;
			build_uid.value=y;
			skill_uid.value=y;
			kick_uid.value=y;
			troops_uid.value=y;
			bag_clear_uid.value=y;
			guide_uid.value=y;
			vip_uid.value=y;
    	}
    </script>
  </head>
  <body>
    <img align="AbsMiddle" src="../image/main.png" width="32" height="32" >
  	&nbsp;
  	<a href="main.jsp" title="首页" >
      <span>首页</span>
  	</a>
  	<div align="left">
  		<form id="role_form">
		  	<br>
		  	<br>
		  	<br>
		  	用户编号：<input name="gold_uid" id="gold_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		        数  量：<input name="gold_num" id="gold_num" value="10000000" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
		    <input id="addGold" type="button" value="添加金币">
		    <input id="clearGold" type="button" value="金币清零">
		  	<br>
		  	<br>
		  	用户编号：<input name="item_uid" id="item_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		        道  具：<select name="item_name" id="item_name"></select>
		        数  量：<input name="item_num" id="item_num" value="13" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		    <input id="addItem" type="button" value="添  加">
		    <br>
		    <br>
		        用户编号：<input name="equip_uid" id="equip_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		        装  备：<select name="equip_name" id="equip_name"></select>
		        数  量：<input name="equip_num" id="equip_num" value="1" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		    <input id="addEquip" type="button" value="添  加">
		    <br>
		    <br>
		        用户编号：<input name="stone_uid" id="stone_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		        材  料：<select name="stone_name" id="stone_name"></select>
		        数  量：<input name="stone_num" id="stone_num"  value="13" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		    <input id="addStone" type="button" value="添  加">
		    <br>
		    <br>
		        用户编号：<input name="res_uid" id="res_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		        资  源：<select name="res_name" id="res_name" onchange='res_select()'></select>
		    <span id="res_city_text"> 城市编号:</span>
		    <input name="res_cityId" id="res_cityId"  value="0" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		        数  量：<input name="res_num" id="res_num"  value="100000000" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		        操作类型:
		    <select name="res_operation" id="res_operation">
		    	<option value="add">增加</option>
		    	<option value="red">减少</option>
		    	<option value="clear">清除</option>
		    </select>
		    <input id="addRes" type="button" value="修  改">
		    <br>
		    <br>
		        用户编号：<input name="build_uid" id="build_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		        城市编号：<input name="build_cityId" id="build_cityId"  value="0" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		        目标类型:
		    <select name="build_operation" id="build_operation">
		    </select>    
		        等       级：<input name="build_num" id="build_num"  value="22" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		    <input id="buildUp" type="button" value="升级">
		    <br>
		    <br>
		        用户编号：<input name="skill_uid" id="skill_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		        数  量：<input name="skill_num" id="skill_num" value="88" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		    <input id="addSkill" type="button" value="修改技能点">
		    <br>
		    <br>
		        用户编号：<input name="vip_uid" id="vip_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		    Vip剩余时间(s/秒)：<input name="vip_num" id="vip_num" value="10" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		    <input id="changeVip" type="button" value="修改VIP的剩余时间">
		    <br>
		    <br>  
		       用户编号：<input name="kick_uid" id="kick_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		    <input id="kickRole" type="button" value="踢出玩家">
		    <br>
		    <br> 
		        用户编号：<input name="troops_uid" id="troops_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		        城市编号：
		    <input name="troops_cityId" id="troops_cityId"  value="0" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>  
		       部队类型：<select name="troops_id" id="troops_id" onchange='link_Level()'></select>
	               部队等 级：<select name="troops_level" id="troops_level"></select>       
	               部队数 量：<input name="troops_num" id="troops_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
		        操作类型:
		    <select name="troops_operation" id="troops_operation">
		    	<option value="add">增加</option>
		    	<option value="red">减少</option>
		    </select>
		    <input id="troopsChange" type="button" value="修改">
		    <br>
		    <br>
		        用户编号：<input name="guide_uid" id="guide_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		    <input id="clcGuides" type="button" value="完成所有指引(重登后生效)">
		    <br>
		    <br>
		      用户编号：<input name="bag_clear_uid" id="bag_clear_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' onchange='setOtherTextUid(this.id)'/>
		      目标:
		    <select name="bag_clear_type" id="bag_clear_type">
		    	<option value="item">道具栏</option>
		    	<option value="equip">装备栏</option>
		    	<option value="material">材料栏</option>
		    </select>
		   <input id="bag_clear" type="button" value="清空">
	    </form>
    </div>
  </body>
</html>