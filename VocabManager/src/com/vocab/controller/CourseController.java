package com.vocab.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vocab.consts.ResponseConst;
import com.vocab.model.Course;
import com.vocab.model.Response;
import com.vocab.model.Status;
import com.vocab.service.CourseService;
import com.vocab.service.StatusService;

/**
 * Servlet implementation class CourseController
 */
@WebServlet("/CourseController")
public class CourseController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int itemLimited = 5;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CourseController() {
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
				gotoCourseMng(request, response);
			}
		} else {
			gotoCourseMng(request, response);
		}
		gotoCourseMng(request, response);
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
		}
	}

	private void gotoCourseMng(HttpServletRequest request, HttpServletResponse response, int ...page)
			throws ServletException, IOException {
		String q = request.getParameter("q");
		q = q == null ? "" : q;
		int countItems = CourseService.getsCount(q);
		List<Status> statuses = StatusService.gets();
		if(countItems != 0) {
			int total_page = (int)Math.ceil((float)countItems / (float)itemLimited);
			int pageNo = page.length == 0 ? parseToInt(request.getParameter("page"), 1) : page[0];
			pageNo = pageNo > total_page ? total_page : pageNo;
			
			int offset = pageNo * itemLimited - itemLimited;
			offset = offset >= 0 ? offset : 0;
			
			List<Course> list = CourseService.gets(q, itemLimited, offset);
			request.setAttribute("list", list);
			request.setAttribute("page", pageNo);
			request.setAttribute("total_page", total_page);
		}
		request.setAttribute("statuses", statuses);
		request.getRequestDispatcher("/course_mng.jsp").forward(request, response);
	}

	private void gotoMain(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String course_name = request.getParameter("course_name");
		String course_desc = request.getParameter("course_desc");
		String course_status = request.getParameter("course_status");
		System.out.println(course_name);
		System.out.println(course_desc);
		System.out.println(course_status);

		String actionStatus = "";
		boolean isScf = false;
		if (course_name != null && !course_name.isEmpty() && course_desc != null && !course_desc.isEmpty()
				&& course_status != null && !course_status.isEmpty()) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String date = dtf.format(now);

			Course course = new Course();
			course.setCourse_name(course_name);
			course.setCourse_description(course_desc);
			course.setCourse_date_creat(date);
			course.setCourse_status(Integer.parseInt(course_status));

			Response resp = CourseService.save(course);
			actionStatus = resp.getResponse_description();
			isScf = resp.getResponse_id() == ResponseConst.SUCCESS;
		} else {
			if (course_name == null || course_name.isEmpty())
				actionStatus = "Course name must be not empty!";
			else if (course_desc == null || course_desc.isEmpty())
				actionStatus = "Course desc must be not empty!";
			else if (course_status == null || course_status.isEmpty())
				actionStatus = "Course status must be not empty!";
		}
		request.setAttribute("is_successful", isScf);
		request.setAttribute("action_status", actionStatus);
		gotoCourseMng(request, response, Integer.MAX_VALUE);
	}

	public static int parseToInt(String stringToParse, int defaultValue) {
	    try {
	       return Integer.parseInt(stringToParse);
	    } catch(NumberFormatException ex) {
	       return defaultValue; //Use default value if parsing failed
	    }
	}
}
