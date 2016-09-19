package com.keyking.coin.service.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.Service;
import com.keyking.coin.service.domain.Controler;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.thread.UserThread;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.StringUtil;

public class Terminal implements Instances{
	public boolean logic(Socket socket){
		try {
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer.println("**************************************");
			writer.println("welcome to console");
			writer.println("**************************************");
			writer.println("please input command:");
			writer.flush();
			String cmd = null;
			while ((cmd = reader.readLine()) != null){
				StringBuffer buf = new StringBuffer(cmd.length());
				char[] chars = cmd.toCharArray();
				int arrLen = chars.length;
				char c;
				for (int i = 0; i < arrLen; i++) {
					c = chars[i];
					if (c == '') {
						i++;
						i++;
						continue;
					}
					if (c != 8) {
						buf.append(c);
					} else {
						int idx = buf.length() - 1;
						if (idx >= 0) {
							buf.deleteCharAt(idx);
						}
					}
				}
				cmd = buf.toString();
				String cmds[] = cmd.split(" ");
				switch(cmds[0]){
				case "exit":
					writer.println("exit console system");
					writer.flush();
					writer.close();
					reader.close();
					return true;
				case "close":
					HTTP.stop();
					UserThread.isRunning = false;
					System.exit(0);
					break;
				case "save":
					writer.println("save ok");
					writer.flush();
					break;
				case "version":
					try {
						Service.load();
					} catch (Exception e) {
						e.printStackTrace();
					}
					writer.println("change version ok");
					writer.flush();
					break;
				case "push":
					try {
						PUSH.init();
					} catch (Exception e) {
						e.printStackTrace();
					}
					writer.println("load push properties ok");
					writer.flush();
					break;
				case "fix" :{
					if (cmds.length < 2){
						writer.println("need param of date");
					}else{
						int date = Integer.parseInt(cmds[1]);
						_fix1(date);
						_fix2(date);
						writer.println("fix ok");
					}
					writer.flush();
					break;
				}
				case "insert":
					if (cmds.length < 3){
						writer.println("please input tel number and nikeName");
						writer.flush();
						return false;
					}
					String result = Controler.getInstance().insertUser(cmds[1],cmds[2]);
					writer.println(result);
					writer.flush();
					break;
				default:
					writer.println("invalid cmd");
					writer.flush();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void _fix1(int date){
		try {
			String fileName = date == 0 ? "./logs/log.log" : ("./logs/log.log.2016-06-" + date);
			LineNumberReader lnr = new LineNumberReader(new FileReader(new File(fileName)));
			int count = 0;
			String preValue = "";
			do {
				count ++;
				String line = lnr.readLine();
				if (StringUtil.isNull(line)){
					break;
				}
				if (preValue.contains("... 28 more")){
					System.out.println("fix -->[" + count + "]" + line);
					if (line.contains("revoke deal ----> id is")){
						int index = line.lastIndexOf(" ");
						String ns = line.substring(index+1,line.length());
						System.out.println("revoke deal : " + ns);
						long dealId = Long.parseLong(ns);
						Deal deal = CTRL.tryToSearch(dealId);
						if (deal != null){
							synchronized (deal) {
								deal.setNum(0);
								deal.setRevoke(true);
								deal.save();
							}
						}
					}else if (line.contains("update deal-order state from")){
						String time = "2016-06-" + date + " " + line.substring(0,8);
						int index = line.indexOf(" to ");
						//String pns = line.substring(index-1,index);
						String nns = line.substring(index+4,index+5);
						byte nn = Byte.parseByte(nns);
						index = line.lastIndexOf(" ");
						String ns = line.substring(index+1,line.length());
						System.out.println("orderId : " + ns);
						long orderId = Long.parseLong(ns);
						DealOrder order = CTRL.searchOrder(orderId);
						if (order != null){
							order.fixState(nn,time);
							order.save();
						}
					}
				}
				preValue = line;
			}while(true);
			lnr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void _fix2(int date){
		try {
			String fileName = date == 0 ? "./logs/log.log" : ("./logs/log.log.2016-06-" + date);
			LineNumberReader lnr = new LineNumberReader(new FileReader(new File(fileName)));
			String preKey = "";
			DealOrder a_order = null;
			String preValue = "";
			do {
				String line = lnr.readLine();
				if (StringUtil.isNull(line)){
					break;
				}
				if(line.contains("org.springframework.dao.DuplicateKeyException: PreparedStatementCallback; SQL []; Duplicate entry ")){
					int index  = line.indexOf("'");
					char[] temp = compute(line,index,'\'');
					preKey = new String(temp);
				}
				/*
				else if (line.contains("at com.keyking.coin.service.http.handler.impl.HttpDealDel")){
					long dealId = Long.parseLong(preKey);
					Deal deal = CTRL.tryToSearch(dealId);
					if (deal != null){
						deal.setNum(0);
						deal.setRevoke(true);
						deal.save();
						System.out.println("revoke deal");
					}
				}*/
				else if (line.contains("at com.keyking.coin.service.net.logic.admin.AdminLockOrder")){
					long orderId = Long.parseLong(preKey);
					DealOrder order = CTRL.searchOrder(orderId);
					if (order != null){
						order.addRevoke(DealOrder.ORDER_REVOKE_BUYER);
						order.addRevoke(DealOrder.ORDER_REVOKE_SELLER);
						order.save();
						System.out.println("AdminLockOrder");
					}
				}else if (line.contains("at com.keyking.coin.service.http.handler.impl.HttpAppraise")){
					long orderId = Long.parseLong(preKey);
					a_order = CTRL.searchOrder(orderId);
				}else if (preValue.contains("... 28 more")){
					if (a_order != null && line.contains("appraised : star = ")){
						String time = "2016-06-" + date + " " + line.substring(0,8);
						int index = line.indexOf("context") - 2;
						String bns = line.substring(index,index + 1);
						byte star = Byte.parseByte(bns);
						index = line.lastIndexOf("=");
						String cValue = line.substring(index + 2,line.length());
						index = line.indexOf(" - ") + 2;
						char[] temp = compute(line,index,' ');
						String account = new String(temp);
						UserCharacter user = CTRL.search(account);
						if (user != null){
							Deal deal = CTRL.tryToSearch(a_order.getDealId());
							DealAppraise appraise = null;
							if (deal.checkSeller(user.getId()) || a_order.checkSeller(deal,user.getId())){//买家
								appraise = a_order.getSellerAppraise();
							}else if (deal.checkBuyer(user.getId()) || a_order.checkBuyer(deal,user.getId())){//卖家
								appraise = a_order.getBuyerAppraise();
							}
							appraise.appraise(deal,a_order,user,star,cValue);
							appraise.setTime(time);
							a_order.save();
							System.out.println(account + " appraised " + bns + " * " + cValue + " at " + time);
						}
						a_order = null;
					}
				}
				preValue = line;
			}while(true);
			lnr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private char[] compute(String str,int start,char e){
		int index = start;
		List<Character> cs = new ArrayList<Character>();
		do{
			index++;
			char c = str.charAt(index);
			if (c == e){
				break;
			}
			cs.add(c);
		}while(true);
		char[] temp = new char[cs.size()];
		for (int i = 0 ; i < cs.size() ; i++){
			Character c = cs.get(i);
			temp[i] = c.charValue();
		}
		return temp;
	}
}
 
 
