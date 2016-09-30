package com.keyking.chat.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UserRegist extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//String account = req.getParameter("account");
		//String pwd = req.getParameter("pwd");
		String result = "{\"result\":1}";
		//具体的注册逻辑
		PrintWriter writer = resp.getWriter();
		writer.write(result);
	}
}
