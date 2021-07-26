package com.vocab.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vocab.model.Response;
import com.vocab.service.ResponseService;

/**
 * Servlet implementation class MyTesting
 */
@WebServlet("/VocabManagerController")
public class VocabManagerController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public VocabManagerController() {
        super();
    }
    
	public void init(ServletConfig config) throws ServletException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		switch (id) {
		case "course":	
			gotoCourseMng(request, response);
			break;
		case "lesson":	
			gotoLessonMng(request, response);
			break;
		case "vocab":	
			gotoVocabMng(request, response);
			break;
		case "resp":	
			gotoRespMng(request, response);
			break;
		case "status":			
			gotoStatusMng(request, response);
			break;
		case "vtype":
			gotoVTypeMng(request, response);
			break;
		default:
			break;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void gotoCourseMng(HttpServletRequest request, HttpServletResponse response) {
		
	}


	private void gotoLessonMng(HttpServletRequest request, HttpServletResponse response) {
		
	}


	private void gotoVocabMng(HttpServletRequest request, HttpServletResponse response) {
		
	}


	private void gotoRespMng(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Response> list = ResponseService.gets();
		request.setAttribute("list", list);
		request.getRequestDispatcher("/status_mng.jsp").forward(request, response);
	}


	private void gotoStatusMng(HttpServletRequest request, HttpServletResponse response) {
		
	}


	private void gotoVTypeMng(HttpServletRequest request, HttpServletResponse response) {
		
	}
}
