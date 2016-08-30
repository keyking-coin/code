<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>战斗模拟器</title>
    <script type="text/javascript" src="../js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="../js/loginData.js"></script>
    <script type="text/javascript" src="../js/battle.js"></script>
    <script type="text/javascript">
    	var hookStr    = "<div id='del_h_@'> <select name='h_army@' id='h_army@' onchange='link_Level(\"#h_army@\",\"#h_level@\")'></select> 等 级:<select name='h_level@' id='h_level@'></select> 数 量:<input name='h_num@' id='h_num@' onkeyup='this.value=this.value.replace(/\\D/gi,\"\")'/> <input type='button' onclick='del_Hook(@)' value='删除'/> </div>";
		var hookCount  = 0;
		
		function del_Hook(count){	
		    var delName = "#del_h_" + count;
			$(delName).remove();
		};
		
		function link_Level(a,b){
		    var objS = $(a);
		    var index = objS.find("option:selected").attr("grade");
        	$(b).empty();
			var linkLevels = levels[index];
 			for (var i = 0 ; i < linkLevels.length ; i++){
 				$(b).append(linkLevels[i]);
 			}
		};
		
		function build_flag_change(obj,name){
			var flag = $(obj).is(':checked');
	    	if (flag){
	    		$(name).show();
	    	}else{
	    		$(name).hide();
	    	}
		}
		
		function just_init(){
			for (var i = 0 ; i < grounds.length ; i++) {
			    $("#a_row1").append(grounds[i]);
				$("#a_row2").append(grounds[i]);
				$("#a_row3").append(grounds[i]);
				$("#d_row1").append(grounds[i]);
				$("#d_row2").append(grounds[i]);
				$("#d_row3").append(grounds[i]);
			}
			for (var i = 0 ; i < planes.length ; i++) {
				$("#a_row4").append(planes[i]);
				$("#d_row4").append(planes[i]);
			}
			link_Level("#a_row1","#a_level1");
			link_Level("#a_row2","#a_level2");
			link_Level("#a_row3","#a_level3");
			link_Level("#a_row4","#a_level4");
			link_Level("#d_row1","#d_level1");
			link_Level("#d_row2","#d_level2");
			link_Level("#d_row3","#d_level3");
			link_Level("#d_row4","#d_level4");
			for (var i = 0 ; i < builds.length ; i++) {
				$("#b_army1").append(builds[i]);
				$("#b_army2").append(builds[i]);
				$("#b_army3").append(builds[i]);
			}
			for (var i = 0 ; i < levels[4].length ; i++) {
				$("#b_level1").append(levels[4][i]);
				$("#b_level2").append(levels[4][i]);
				$("#b_level3").append(levels[4][i]);
			}
			$("#build1").attr("checked",false);
			$("#build2").attr("checked",false);
			$("#build3").attr("checked",false);
			$("#build_sun1").hide();
			$("#build_sun2").hide();
			$("#build_sun3").hide();
			$("#hooks").attr("checked",false);
			hookCount = 0;
		    $("#hook_suns").empty();
		    $("#hooks_div").hide();
		}
		
		$(document).ready(function() {
			if (!LoginData.check("loginTime")){
		   		alert("you must login");
		   		window.location.href = '../index.jsp';
			}
			just_init();
			$("#reset").click(function(){
				$("#a_row1").val("none");
				$("#a_row2").val("none"); 
				$("#a_row3").val("none"); 
				$("#a_row4").val("none"); 
				$("#d_row1").val("none"); 
				$("#d_row2").val("none"); 
				$("#d_row3").val("none"); 
				$("#d_row4").val("none");
				$("#a_num1").val("");
				$("#a_num2").val("");
				$("#a_num3").val("");
				$("#a_num4").val("");
				$("#d_num1").val("");
				$("#d_num2").val("");
				$("#d_num3").val("");
				$("#d_num4").val("");
				$("#result").empty();
				$("#build1").attr("checked",false);
				$("#build2").attr("checked",false);
				$("#build3").attr("checked",false);
				$("#build_sun1").hide();
				$("#build_sun2").hide();
				$("#build_sun3").hide();
				$("#hooks").attr("checked",false);
				hookCount = 0;
			    $("#hook_suns").empty();
			    $("#hooks_div").hide();
			});
			
			$("#fight").click(function(){
				if (!LoginData.check("loginTime")){
    		   		alert("you must login");
    		   		window.location.href = '../index.jsp';
    			}
				$("#result").empty();
				var str_data = $("#armyInfo input,select").map(function(){
			  		return ($(this).attr("name") + '=' +$(this).val());
			  	}).get().join("&");
			   var moreStr = "&build1=" + $("#build1").is(":checked") + 
			   				 "&build2=" + $("#build2").is(":checked") + 
			   				 "&build3=" + $("#build3").is(":checked") + 
			   				 "&hooks=" + $("#hooks").is(":checked") + 
			   				 "&hookCount=" + hookCount 
			                  ;
			   str_data = str_data + moreStr;
			   //alert(str_data);
			   $.ajax({
				  	type: 'get',
				  	url: './HttpStartFight',
				  	data: str_data,
				  	success: function(msg){
				  	  var dataObj = eval("("+msg+")");
				      if (dataObj.result == "ok"){
				      	$("#result").append(dataObj.infos);
				      }else{
				      	 alert(dataObj.result);
				      }
			        }
			   });
		    });
			$("#hooks").click(function(){
				var flag = $(this).is(':checked');
		    	if (flag){
		    		$("#hooks_div").show();
		    	}else{
		    		hookCount = 0;
		    		$("#hook_suns").empty();
		    		$("#hooks_div").hide();
		    	}
		    });
			$("#addHook").click(function(){
				 hookCount++;
			     var newStr = hookStr.replace(/@/gi,hookCount);
			     $("#hook_suns").append(newStr);
			     var hookIdName = "#h_army" + hookCount;
			     for(var i = 0 ; i < hooks.length ; i++) {
		 			$(hookIdName).append(hooks[i]);
				 }
				 link_Level("#h_army" + hookCount,"#h_level" + hookCount);
			});
		});
	</script>
  </head>
  <img align="AbsMiddle" src="../image/main.png" width="32" height="32" >
  &nbsp;
  <a href="../gm/main.jsp" title="首页" >
      <span>首页</span>
  </a>
  <div align="center">
	     <div align="middle" >
	     	<form id="armyInfo" method="get">
	     	        攻击方：<br>
	    	 	第一排：<select name="a_row1" id="a_row1" onchange="link_Level('#a_row1','#a_level1')"></select>
	    	 	等 级：<select name="a_level1" id="a_level1"></select> 
	    	 	数 量：<input name="a_num1" id="a_num1" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
	    	 	<br>
	    	 	第二排：<select name="a_row2" id="a_row2" onchange="link_Level('#a_row2','#a_level2')"></select> 
	    	 	等 级：<select name="a_level2" id="a_level2"></select>
	    	 	数 量：<input name="a_num2" id="a_num2" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
	    	 	<br>
	    	 	第三排：<select name="a_row3" id="a_row3" onchange="link_Level('#a_row3','#a_level3')"></select> 
	    	 	等 级：<select name="a_level3" id="a_level3"></select>
	    	 	数 量：<input name="a_num3" id="a_num3" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
	    	 	<br>
	    	 	第四排：<select name="a_row4" id="a_row4" onchange="link_Level('#a_row4','#a_level4')"></select> 
	    	 	等 级：<select name="a_level4" id="a_level4"></select>
	    	 	数 量：<input name="a_num4" id="a_num4" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
	    	 	<br> 
	    	 	 防御方：<br>
	    	 	第一排：<select name="d_row1" id="d_row1" onchange="link_Level('#d_row1','#d_level1')"></select> 
	    	 	等 级：<select name="d_level1" id="d_level1"></select>
	    	 	数 量：<input name="d_num1" id="d_num1" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
	    	 	<br>
	    	 	第二排：<select name="d_row2" id="d_row2" onchange="link_Level('#d_row2','#d_level2')"></select>
	    	 	等 级：<select name="d_level2" id="d_level2"></select> 
	    	 	数 量：<input name="d_num2" id="d_num2" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
	    	 	<br>
	    	 	第三排：<select name="d_row3" id="d_row3" onchange="link_Level('#d_row3','#d_level3')"></select> 
	    	 	等 级：<select name="d_level3" id="d_level3"></select>
	    	 	数 量：<input name="d_num3" id="d_num3" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
	    	 	<br>
	    	 	第四排：<select name="d_row4" id="d_row4" onchange="link_Level('#d_row4','#d_level4')"></select>
	    	 	等 级：<select name="d_level4" id="d_level4"></select> 
	    	 	数 量：<input name="d_num4" id="d_num4" onkeyup='this.value=this.value.replace(/\D/gi,"")'/>
	    	 	<br>
	    	 	<br> 
	    	 	<div id = "builds_div">
	    	 	    <div name="城防单位1">
		    	 		<input type="checkbox" id="build1" onchange="build_flag_change(this,'#build_sun1')">城防坑一
		    	 		<div id="build_sun1" style="display:none">
		    	 			 类型:
			    	 	    <select name="b_army1" id="b_army1" onchange="link_Level('#b_army1','#b_level1')" >
			    	 	    </select> 
			    	 	       等 级:
			    	 	    <select name="b_level1" id="b_level1">
			    	 	    </select> 
			    	 	        血 量:
			    	 	    <input name="b_hp1" id="b_hp1" onkeyup="this.value=this.value.replace(/\\D/gi,'')" />
		    	 		</div>
	    	 		</div>
	    	 		<div name="城防单位2">
		    	 		<input type="checkbox" id="build2" onchange="build_flag_change(this,'#build_sun2')">城防坑二
		    	 		<div id="build_sun2" style="display:none">
			    	 	       类型:
			    	 	    <select name="b_army2" id="b_army2" onchange="link_Level('#b_army2','#b_level2')" >
			    	 	    </select> 
			    	 	       等 级:
			    	 	    <select name="b_level2" id="b_level2">
			    	 	    </select> 
			    	 	        血 量:
			    	 	    <input name="b_hp2" id="b_hp2" onkeyup="this.value=this.value.replace(/\\D/gi,'')" />
		    	 		</div>
	    	 		</div>
	    	 		<div name="城防单位3">
		    	 		<input type="checkbox" id="build3" onchange="build_flag_change(this,'#build_sun3')">城防坑三
		    	 		<div id="build_sun3" style="display:none">
		    	 			 类型:
			    	 	    <select name="b_army3" id="b_army3" onchange="link_Level('#b_army3','#b_level3')" >
			    	 	    </select> 
			    	 	       等 级:
			    	 	    <select name="b_level3" id="b_level3">
			    	 	    </select> 
			    	 	        血 量:
			    	 	    <input name="b_hp3" id="b_hp3" onkeyup="this.value=this.value.replace(/\\D/gi,'')" />  
		    	 		</div>
	    	 		</div>
	    	 	</div>
	    	 	<input type="checkbox" id="hooks" value="false">开启陷阱
	    	 	<div id = "hooks_div" style="display:none">
	    	 		<input id="addHook" type="button" value="添加陷阱">	
	    	 	    <div id = "hook_suns">
	    	 	    </div>
	    	 	</div>
	    	 	<br>
	    	 	<input id="reset" type="button" value="重        置">	
	    		<input id="fight" type="button" value="开始战斗">
	    		战斗次数：<input name="fightNum" id="fightNum" value="1" onkeyup="this.value=this.value.replace(/\D/gi,"")" />
	      </form>    	 
	     </div>
	     <div id = "result">
	     </div>
     </div>
  </body>
</html>
