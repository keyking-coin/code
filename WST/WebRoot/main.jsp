<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String contextPath = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+contextPath+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>ChatRoom demo</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css">
        input#chat {
            width: 410px
        }
        #console-container {
            width: 400px;
        }
        #console {
            border: 1px solid #CCCCCC;
            border-right-color: #999999;
            border-bottom-color: #999999;
            height: 170px;
            overflow-y: scroll;
            padding: 5px;
            width: 100%;
        }
        #console p {
            padding: 0;
            margin: 0;
        }
    </style>
    <script type="application/javascript">
        var Chat = {};
        Chat.socket = null;
        Chat.connect = (function(host) {
            if ('WebSocket' in window) {
                Chat.socket = new WebSocket(host);
            } else if ('MozWebSocket' in window) {
                Chat.socket = new MozWebSocket(host);
            } else {
                Console.log('Error: WebSocket is not supported by this browser.');
                document.getElementById('noscript').show();
                return;
            }
            Chat.socket.onopen = function () {
                //Console.log('Info: WebSocket connection opened.');
                document.getElementById('chat').onkeydown = function(event) {
                    if (event.keyCode == 13) {
                        Chat.sendMessage();
                    }
                };
            };
            Chat.socket.onclose = function () {
                document.getElementById('chat').onkeydown = null;
                //Console.log('Info: WebSocket closed.');
                alert("与服务器链接失败,刷新重试");
            };
            Chat.socket.onmessage = function (message) {
                Console.log(message.data);
            };
        });

        Chat.initialize = function() {
            var path = '<%=contextPath%>';
            if (window.location.protocol == 'http:') {
                Chat.connect('ws://' + window.location.host + path + '/websocket/chat');
            } else {
                Chat.connect('wss://' + window.location.host + path + '/websocket/chat');
            }
        };

        Chat.sendMessage = (function() {
            var message = document.getElementById('chat').value;
            if (message != '') {
                Chat.socket.send(message);
                document.getElementById('chat').value = '';
            }
        });

        var Console = {};

        Console.log = (function(message) {
            var console = document.getElementById('console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.innerHTML = message;
            console.appendChild(p);
            while (console.childNodes.length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        });
        Chat.initialize();
    </script>
  </head>
  
  <body>
  	<div id="noscript" style="display:none">
  		<h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websockets rely on Javascript being enabled. Please enable Javascript and reload this page!</h2>
  	</div>
	<div>
		<div>
      		<input type="text" id="chat" />
    	</div>
    	<div id="console-container">
      		<div id="console"></div>
    	</div>
	</div>
  </body>
</html>
