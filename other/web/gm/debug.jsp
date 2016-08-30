<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>系统管理</title>
    <script type="text/javascript" src="../js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="../js/loginData.js"></script>
    <script type="text/javascript">
        function request(params){
	    	$.ajax({
				  	type: "get",
				  	url: "./HttpGmDebug",
				  	data: params,
				  	dataType : "json",
				  	success: function(msg){
				  		alert(msg.result);
			        }
			 });
        };
    	$(document).ready(function() {
    	    if (!LoginData.check("loginTime")){
    	     	alert("you must login");
    		   	window.location.href = '../index.jsp';
    	    }
			$("#battleLog").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "battle_log_open=" + $("#battle_log_open").val() + "&" + 
    						 "action=battleLog"; 
			    request(params);
			});
			$("#refreshMarket").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "market_uid=" + $("#market_uid").val() + "&action=refreshMarket";
    			request(params);
			});
			$("#attack_city_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "attack_city_op=" + $("#attack_city_op").val() + "&" + 
    					     "action=attack_city_change"; 
			    request(params);
			});
			$("#money_shop_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "money_shop_op=" + $("#money_shop_op").val() + "&" + 
    					     "action=money_shop_change"; 
			    request(params);
			});
			$("#online_reward_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "online_reward_uid=" + $("#online_reward_uid").val() + "&" +
    						 "online_reward_num=" + $("#online_reward_num").val() + "&" + 
    					     "action=online_reward_change"; 
			    request(params);
			});
			$("#build_time_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "build_time_uid=" + $("#build_time_uid").val() + "&" +
    						 "build_time_city=" + $("#build_time_city").val() + "&" + 
    						 "build_time_num=" + $("#build_time_num").val() + "&" + 
    					     "action=build_time_change"; 
			    request(params);
			});
			$("#union_max_num_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "union_max_uid=" + $("#union_max_uid").val() + "&" +
    						 "union_max_num=" + $("#union_max_num").val() + "&" + 
    					     "action=union_max_num_change"; 
			    request(params);
			});
			$("#union_teach_time_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "union_teach_time_uid=" + $("#union_teach_time_uid").val() + "&" +
    						 "union_teach_time_num=" + $("#union_teach_time_num").val() + "&" + 
    					     "action=union_teach_time_change"; 
			    request(params);
			});
			$("#union_share_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "union_share_uid=" + $("#union_share_uid").val() + "&" +
    						 "union_share_num=" + $("#union_share_num").val() + "&" + 
    					     "action=union_share_change"; 
			    request(params);
			});
			$("#union_build_create_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "union_build_create_uid=" + $("#union_build_create_uid").val() + "&" +
    						 "union_build_create_num=" + $("#union_build_create_num").val() + "&" + 
    					     "action=union_build_create_change"; 
			    request(params);
			});
			$("#union_build_up_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "union_build_up_uid=" + $("#union_build_up_uid").val() + "&" +
    						 "union_build_up_num=" + $("#union_build_up_num").val() + "&" + 
    					     "action=union_build_up_change"; 
			    request(params);
			});
			$("#union_build_drop_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "union_build_drop_uid=" + $("#union_build_drop_uid").val() + "&" +
    						 "union_build_drop_num=" + $("#union_build_drop_num").val() + "&" + 
    					     "action=union_build_drop_change"; 
			    request(params);
			});
			$("#admin_op_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "admin_src_uid=" + $("#admin_src_uid").val() + "&" +
    						 "admin_tar_uid=" + $("#admin_tar_uid").val() + "&" + 
    					     "admin_op_flag=" + $("#admin_op_flag").val() + "&" + 
    					     "action=admin_op_change"; 
			    request(params);
			});
			$("#fortress_build_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "fortress_build_uid=" + $("#fortress_build_uid").val() + "&" +
    						 "fortress_build_num=" + $("#fortress_build_num").val() + "&" + 
    					     "action=fortress_build_change"; 
			    request(params);
			});
			$("#fortress_level_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "fortress_level_uid=" + $("#fortress_level_uid").val() + "&" +
    						 "fortress_level_num=" + $("#fortress_level_num").val() + "&" + 
    					     "action=fortress_level_change"; 
			    request(params);
			});
			$("#fortress_drop_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "fortress_drop_uid=" + $("#fortress_drop_uid").val() + "&" +
    						 "fortress_drop_num=" + $("#fortress_drop_num").val() + "&" + 
    					     "action=fortress_drop_change"; 
			    request(params);
			});
			$("#city_move_change").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
    			var params = "city_move_uid=" + $("#city_move_uid").val() + "&" +
    						 "city_move_num=" + $("#city_move_num").val() + "&" + 
    						 "city_move_city=" + $("#city_move_city").val() + "&" + 
    					     "action=city_move_change"; 
			    request(params);
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
	  用户编号：<input id="market_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
	  <input id="refreshMarket" type="button" value="刷新黑市每日最低折扣"/>
	  <br>
	  <br>
	   战斗日志：<select id="battle_log_open">
	     <option value='true'>开启</option>
	     <option value='false'>关闭</option>
	  </select>
	  <input id="battleLog" type="button" value="设置"/>
	   攻城必胜：<select id="attack_city_op">
	     <option value='true'>开启</option>
	     <option value='false'>关闭</option>
	  </select>
	  <input id="attack_city_change" type="button" value="设置"/>
	  <br>
	  <br> 
	   金币商城：<select id="money_shop_op">
	     <option value='true'>开启</option>
	     <option value='false'>关闭</option>
	  </select>
	  <input id="money_shop_change" type="button" value="设置"/>
	  (重新登录生效)
	  <br>
	  <br> 
	    用户编号：<input id="online_reward_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
	    在线奖励：<input id="online_reward_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
	  <input id="online_reward_change" type="button" value="修改"/>
	  (数字设置为0或者下线后恢复正常流程)
	  <br>
	  <br> 
	    用户编号：<input id="build_time_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
	    城市编号：<input id="build_time_city" value="0" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
	    工程队时间：<input id="build_time_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")'/>
	  <input id="build_time_change" type="button" value="设置" />
	  <br>
	  <br> 
	   用户编号:<input id="union_max_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   联盟人数上限:<input id="union_max_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="union_max_num_change" type="button" value="设置" />	 
	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="union_teach_time_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   联盟科技升级时间:<input id="union_teach_time_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="union_teach_time_change" type="button" value="设置" />	  
	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="union_share_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   联盟捐献经验:<input id="union_share_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="union_share_change" type="button" value="设置" />	  
	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="union_build_create_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   联盟建筑建造时间:<input id="union_build_create_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="union_build_create_change" type="button" value="设置" />	 	 
	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="union_build_up_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   联盟建筑升级时间:<input id="union_build_up_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="union_build_up_change" type="button" value="设置" />	 	 
	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="union_build_drop_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   联盟联盟建筑拆除时间:<input id="union_build_drop_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="union_build_drop_change" type="button" value="设置" />	 	 
 	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="fortress_build_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   要塞建造时间:<input id="fortress_build_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="fortress_build_change" type="button" value="设置" />	 	 
 	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="fortress_level_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   要塞和军营升级时间:<input id="fortress_level_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="fortress_level_change" type="button" value="设置" />	 	 
 	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="fortress_drop_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   要塞和军营放弃时间:<input id="fortress_drop_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="fortress_drop_change" type="button" value="设置" />	 	 
 	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   用户编号:<input id="city_move_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   城市编号:<input id="city_move_city" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   迁城点建造时间:<input id="city_move_num" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	  <input id="city_move_change" type="button" value="设置" />	 	 
 	  (数字设置为0恢复正常流程)
	  <br>
	  <br>
	   正常编号:<input id="admin_src_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   异常编号:<input id="admin_tar_uid" onkeyup='this.value=this.value.replace(/\D/gi,"")' onafterpaste='this.value=this.value.replace(/\D/g,"")' />
	   操作：
	  <select id="admin_op_flag">
	     <option value='true'>开启</option>
	     <option value='false'>关闭</option>
	  </select>
	  <input id="admin_op_change" type="button" value="设置" />
	  (非开发人员慎用) 	 
    </div>
  </body>
</html>
