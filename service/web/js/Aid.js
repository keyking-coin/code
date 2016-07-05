function View_HTML (data,title_id,body_id) {
$("#"+title_id).html(data.nikeName+"&nbsp;&nbsp;&nbsp;&nbsp;头衔:<span style='color:#FF0000'>"+data.title+'</span>');
var html="";
html="<span style='font-weight:bold;'>姓名:</span>"+data.name+"&nbsp;&nbsp;&nbsp;&nbsp;<span style='font-weight:bold;'>联系电话:</span>"+data.tel+"&nbsp;&nbsp;&nbsp;&nbsp;<span style='font-weight:bold;'>注册时间:</span>"+format_Time(data.registTime)+"<br><span style='font-weight:bold;'>签名:</span>"+data.signature
html=html+'<div style="width:100%; height:180px; overflow:auto;position: relative;">';

if (data.addresses.length>0) {
     var addresses=''
    for (var i = 0; i < data.addresses.length; i++) {
        addresses=addresses+'<tr><td>'+data.addresses[i]+'</td></tr>'
    };
}else{
    var addresses='<tr><td>仅对交易方可见</td></tr>'
};
html=html+'<table class=\"table table-striped\" style="margin-bottom:0px"><thead style="font-size: 10px">       <tr>          <th>文交所账号</th></tr></thead><tbody style="font-size: 10px">'+addresses+'</tbody> </table>';

if (data.banks.length>0) {
     var banks=''
    for (var i = 0; i < data.banks.length; i++) {
        banks=banks+'<tr><td>'+data.banks[i].name+'</td><td>'+data.banks[i].account+'</td><td>'+data.banks[i].openName+'</td><td>'+data.banks[i].openAddress+'</td></tr>'
    };
}else{
    var banks='<tr><td>仅对交易方可见</td></tr>'
};


html=html+'<table class="table table-striped" style="margin-bottom:0px"><thead style="font-size: 10px">       <tr>          <th width="10%">银行</th>       <th width="20%">卡号</th>         <th width="40%">开户人</th>        <th width="20%">开户行</th>       </tr>    </thead>    <tbody style="font-size: 10px"> '+banks+' </tbody> </table>';
html=html+'</div> '+"<br><center> <span style='font-weight:bold;'>成交额:</span>"+data.credit.totalDealValue+"元&nbsp;&nbsp;&nbsp;&nbsp;<span style='font-weight:bold;'>成交笔数:</span>"+parseInt(parseInt(data.credit.cp)+parseInt(data.credit.hp)+parseInt(data.credit.cp))+"&nbsp;&nbsp;&nbsp;&nbsp;<span style='font-weight:bold;'>好评:</span>"+data.credit.hp+"&nbsp;&nbsp;&nbsp;&nbsp;<span style='font-weight:bold;'>中评:</span>"+data.credit.zp+"&nbsp;&nbsp;&nbsp;&nbsp;<span style='font-weight:bold;'>差评:</span>"+data.credit.cp+"</center>";
/*return html;*/

$("#"+body_id).html(html);
};

function Getlogin(){
if (iid!=null && pwd!=null) {
    return true;
};
return false;
}


function format_Time(Time_s){
var Time =Time_s.replace(/-/g,"/");
var date = new Date(Time);
return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
}


function setCookie(name,value)
//写cookies
{
var Days = 30;
var exp = new Date();
exp.setTime(exp.getTime() + Days*24*60*60*1000);
document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}
function getCookie(name)
//读取cookies
{
var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
if(arr=document.cookie.match(reg))
return unescape(arr[2]);
else
return null;
}

function delCookie(name)
//删除cookies
{
var exp = new Date();
exp.setTime(exp.getTime() - 1);
var cval=getCookie(name);
if(cval!=null)
document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}


function clearCookie(){ 
	//清空 cookie
var keys=document.cookie.match(/[^ =;]+(?=\=)/g); 
if (keys) { 
for (var i = keys.length; i--;)
document.cookie=keys[i]+'=0;expires=' + new Date(0).toUTCString() 
} 
} 


function Time_revoke(_Time){
var date = new Date(_Time.replace(/-/g,"/")),date2 = new Date();
if (date2.getTime()>=date.getTime()) {
  return true;
}
  return false;
}

function select_Get(bool,str1,str2){
  if (bool) {
 return(str1)
  }else{
return(str2)
  };
}


function Get_Posts(type,did,oid){
if (type==0) {
window.open("../?nav=Deal&did="+did);  
}else{
window.open("../?nav=Order&did="+did+"&oid="+oid);  
};
}

function bombUrl(url){
window.open(url); 
}

function _scroll(){
window.parent.iFrameHeight();
}

function iFrameHeight() {

var ifm= document.getElementById("iframe1");   
ifm.height = 330;
var subWeb = document.frames ? document.frames["iframe1"].document : ifm.contentDocument;   
if(ifm != null && subWeb != null) {
   ifm.height = subWeb.body.scrollHeight+50;
   // ifm.width = subWeb.body.scrollWidth;
} 
ifm.width=700; 
}


