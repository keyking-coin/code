/*
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.keyking.chat.servlet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.keyking.chat.StartInit;
import com.keyking.chat.util.HTMLFilter;

@ServerEndpoint(value = "/websocket/chat")
public class ChatAnnotation {
	private static final String GUEST_PREFIX = "Guest";
	private static final AtomicInteger connectionIds = new AtomicInteger(0);
	private static final List<ChatAnnotation> connections = new CopyOnWriteArrayList<>();
	private final String nickname;
	private Session session;

	public ChatAnnotation() {
		nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
	}

	@OnOpen
	public void start(Session session) {
		this.session = session;
		connections.add(this);
		String message = String.format("* %s %s", nickname, "加入聊天室");
		broadcast(this,message);
		StartInit.context.log(nickname + " join room");
	}

	@OnClose
	public void end() {
		connections.remove(this);
		_end();
	}

	private void _end() {
		String message = String.format("* %s %s", nickname, "退出聊天室");
		broadcast(this,message);
		StartInit.context.log(nickname + " exit room");
		try {
			session.close();
		} catch (Exception e) {

		}
	}

	@OnMessage
	public void incoming(String message) {
		String filteredMessage = String.format("%s: %s", nickname, HTMLFilter.filter(message.toString()));
		broadcast(this,filteredMessage);
	}

	@OnError
	public void onError(Throwable t) throws Throwable {
		StartInit.context.log(nickname + " happend error " + t.toString(), t);
	}

	private static void broadcast(Object locker,String msg) {
		synchronized (locker) {
			for (int i = 0; i < connections.size();) {
				ChatAnnotation client = connections.get(i);
				try {
					client.session.getBasicRemote().sendText(msg);
					i++;
				} catch (Exception e) {
					StartInit.context.log("there is one error when send message to " + client.nickname, e);
					client._end();
					connections.remove(i);
				}
			}
		}
	}
}
