package com.vocab.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vocab.model.Course;
import com.vocab.model.Lesson;
import com.vocab.service.CourseService;
import com.vocab.service.LessonService;

/**
 * Servlet implementation class LessonController
 */
@WebServlet("/LessonController")
public class LessonController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LessonController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String _action = (String) request.getParameter("_action");
		System.out.println(_action);
		if (_action != null) {
			switch (_action) {
			case "main":
				gotoMain(request, response);
				break;
			default:
				gotoLessonMng(request, response);
			}
		} else {
			gotoLessonMng(request, response);
		}
		gotoLessonMng(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void gotoMain(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
	
	private void gotoLessonMng(HttpServletRequest request, HttpServletResponse response, int ...page)
			throws ServletException, IOException {
		List<Lesson> list = LessonService.gets("");
		request.setAttribute("list", list);
		request.getRequestDispatcher("/lesson_mng.jsp").forward(request, response);
	}
}
