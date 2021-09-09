package com.vocab.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vocab.model.Lesson;
import com.vocab.model.Vocab;
import com.vocab.model.VocabType;
import com.vocab.service.LessonService;
import com.vocab.service.VTypeService;
import com.vocab.service.VocabService;

/**
 * Servlet implementation class VocabController
 */
@WebServlet("/VocabController")
public class VocabController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int itemLimited = 5;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VocabController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String _action = (String) request.getParameter("_action");
		System.out.println(_action);
		if (_action != null) {
			switch (_action) {
			case "main":
				gotoMain(request, response);
				break;
			default:
				gotoVocabMng(request, response);
			}
		} else {
			gotoVocabMng(request, response);
		}
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

	private void gotoVocabMng(HttpServletRequest request, HttpServletResponse response, int... page)
			throws ServletException, IOException {
		String q = request.getParameter("q");
		q = q == null ? "" : q;
		
		String vt_id = request.getParameter("vt_id");
		vt_id = vt_id == null ? "" : vt_id;
		
		String lesson_id = request.getParameter("lesson_id");
		lesson_id = lesson_id == null ? "" : lesson_id;

		
		int countItems = VocabService.getsCount(q, vt_id, lesson_id);
		List<Lesson> lessons = LessonService.gets("", "", "");
		List<VocabType> vocabTypes = VTypeService.gets("");
		
		if (countItems != 0) {
			int total_page = (int) Math.ceil((float) countItems / (float) itemLimited);
			int pageNo = page.length == 0 ? parseToInt(request.getParameter("page"), 1) : page[0];
			pageNo = pageNo > total_page ? total_page : pageNo;

			int offset = pageNo * itemLimited - itemLimited;
			offset = offset >= 0 ? offset : 0;

			List<Vocab> list = VocabService.gets(q, vt_id, lesson_id, itemLimited, offset);
			request.setAttribute("list", list);
			request.setAttribute("page", pageNo);
			request.setAttribute("total_page", total_page);
		}

		request.setAttribute("lessons", lessons);
		request.setAttribute("vocab_types", vocabTypes);
		request.getRequestDispatcher("/vocab_mng.jsp").forward(request, response);
	}
	
	public static int parseToInt(String stringToParse, int defaultValue) {
		try {
			return Integer.parseInt(stringToParse);
		} catch (NumberFormatException ex) {
			return defaultValue; // Use default value if parsing failed
		}
	}

}
