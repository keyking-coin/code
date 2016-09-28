package com.keyking.chat;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UserLogin extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String account = req.getParameter("account");
		String pwd = req.getParameter("pwd");
		String result = null;
		if (account.equals("admin") && pwd.equals("123456")){
			result = "{\"result\":1}";
		}else{
			result = "{\"result\":0}";
		}
		//resp.addHeader("contentType","application/json;charset=UTF-8");
		//resp.setStatus(HttpServletResponse.SC_OK);
		PrintWriter writer = resp.getWriter();
		writer.write(result);
		//RequestDispatcher rd = req.getRequestDispatcher(to); 
		//rd.forward(req,resp);
	}
}
