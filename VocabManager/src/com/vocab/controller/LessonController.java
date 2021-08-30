package com.vocab.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vocab.consts.ResponseConst;
import com.vocab.model.Course;
import com.vocab.model.Lesson;
import com.vocab.model.Response;
import com.vocab.model.Status;
import com.vocab.service.CourseService;
import com.vocab.service.LessonService;
import com.vocab.service.StatusService;

/**
 * Servlet implementation class LessonController
 */
@WebServlet("/LessonController")
public class LessonController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int itemLimited = 5;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LessonController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String _action = (String) request.getParameter("_action");
		System.out.println(_action);
		switch (_action) {
		case "save":
			save(request, response);
			break;
		case "update":
			break;
		case "delete":
			break;
		}
	}

	private void gotoMain(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	private void gotoLessonMng(HttpServletRequest request, HttpServletResponse response, int... page)
			throws ServletException, IOException {
		String q = request.getParameter("q");
		q = q == null ? "" : q;

		String course_id = request.getParameter("course_id");
		course_id = course_id == null ? "" : course_id;

		String status_id = request.getParameter("status_id");
		status_id = status_id == null ? "" : status_id;

		int countItems = LessonService.getsCount(q, course_id, status_id);
		List<Status> statuses = StatusService.gets();
		List<Course> courses = CourseService.gets("");

		if (countItems != 0) {
			int total_page = (int) Math.ceil((float) countItems / (float) itemLimited);
			int pageNo = page.length == 0 ? parseToInt(request.getParameter("page"), 1) : page[0];
			pageNo = pageNo > total_page ? total_page : pageNo;

			int offset = pageNo * itemLimited - itemLimited;
			offset = offset >= 0 ? offset : 0;

			List<Lesson> list = LessonService.gets(q, course_id, status_id, itemLimited, offset);
			request.setAttribute("list", list);
			request.setAttribute("page", pageNo);
			request.setAttribute("total_page", total_page);
		}
		request.setAttribute("statuses", statuses);
		request.setAttribute("courses", courses);
		request.getRequestDispatcher("/lesson_mng.jsp").forward(request, response);
	}

	private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String lesson_name = request.getParameter("lesson_name");
		int lesson_course = parseToInt(request.getParameter("lesson_course"), -1);
		int lesson_status = parseToInt(request.getParameter("lesson_status"), -1);
		System.out.println(lesson_name);
		System.out.println(lesson_course);
		System.out.println(lesson_status);

		String actionStatus = "";
		boolean isScf = false;
		if (lesson_name != null && !lesson_name.isEmpty()) {

			Lesson lesson = new Lesson();
			lesson.setLesson_name(lesson_name);
			lesson.setLesson_course(lesson_course);
			lesson.setLesson_status(lesson_status);

			Response resp = LessonService.save(lesson);
			actionStatus = resp.getResponse_description();
			isScf = resp.getResponse_id() == ResponseConst.SUCCESS;
		} else {
			actionStatus = "Course name must be not empty!";
		}
		request.setAttribute("is_successful", isScf);
		request.setAttribute("action_status", actionStatus);
		gotoLessonMng(request, response, Integer.MAX_VALUE);
	}

	public static int parseToInt(String stringToParse, int defaultValue) {
		try {
			return Integer.parseInt(stringToParse);
		} catch (NumberFormatException ex) {
			return defaultValue; // Use default value if parsing failed
		}
	}
}
