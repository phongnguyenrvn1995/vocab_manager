package com.vocab.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vocab.consts.ResponseConst;
import com.vocab.model.Lesson;
import com.vocab.model.Response;
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
		String _action = (String) request.getParameter("_action");
		System.out.println(_action);
		switch (_action) {
		case "save":
			save(request, response);
			break;
		case "update":
//			update(request, response);
			break;
		case "delete":
//			delete(request, response);
			break;
		}
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
	

	private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String vocab_id = request.getParameter("vocab_id");
		String vocab_type = request.getParameter("vocab_type");
		String vocab_lesson = request.getParameter("vocab_lesson");
		String vocab_en = request.getParameter("vocab_en");
		String vocab_ipa = request.getParameter("vocab_ipa");
		String vocab_vi = request.getParameter("vocab_vi");
		String vocab_description = request.getParameter("vocab_description");
		String vocab_sound_url = request.getParameter("vocab_sound_url");
//		System.out.println("vocab_id"            + " " + vocab_id           );
		System.out.println("vocab_type"          + " " + vocab_type         );
		System.out.println("vocab_lesson"        + " " + vocab_lesson       );
		System.out.println("vocab_en"            + " " + vocab_en           );
		System.out.println("vocab_ipa"           + " " + vocab_ipa          );
		System.out.println("vocab_vi"            + " " + vocab_vi           );
		System.out.println("vocab_description"   + " " + vocab_description  );
		System.out.println("vocab_sound_url"     + " " + vocab_sound_url    );
		

		String actionStatus = "";
		boolean isScf = false;
		if (vocab_type != null && !vocab_type.isEmpty()
						&& vocab_lesson != null && !vocab_lesson.isEmpty()
								&& vocab_en != null && !vocab_en.isEmpty()
										&& vocab_ipa != null && !vocab_ipa.isEmpty()
												&& vocab_vi != null && !vocab_vi.isEmpty()
														&& vocab_description != null && !vocab_description.isEmpty()
																&& vocab_sound_url != null && !vocab_sound_url.isEmpty()) {

			Vocab vocab = new Vocab();
			vocab.setVocab_type(parseToInt(vocab_type, -1));
			vocab.setVocab_lesson(parseToInt(vocab_lesson, -1));
			vocab.setVocab_en(vocab_en);
			vocab.setVocab_ipa(vocab_ipa);
			vocab.setVocab_vi(vocab_vi);
			vocab.setVocab_description(vocab_description);
			vocab.setVocab_sound_url(vocab_sound_url);

			Response resp = VocabService.save(vocab);
			actionStatus = resp.getResponse_description();
			isScf = resp.getResponse_id() == ResponseConst.SUCCESS;
		} else {
			if (vocab_type == null || vocab_type.isEmpty())
				actionStatus = "Type id must be not empty!";
			else if (vocab_lesson == null || vocab_lesson.isEmpty())
				actionStatus = "Lesson id must be not empty!";
			else if (vocab_en == null || vocab_en.isEmpty())
				actionStatus = "ENG must be not empty!";
			else if (vocab_ipa == null || vocab_ipa.isEmpty())
				actionStatus = "IPA must be not empty!";
			else if (vocab_vi == null || vocab_vi.isEmpty())
				actionStatus = "VI must be not empty!";
			else if (vocab_description == null || vocab_description.isEmpty())
				actionStatus = "Description must be not empty!";
			else if (vocab_sound_url == null || vocab_sound_url.isEmpty())
				actionStatus = "Sound URL must be not empty!";
		}
		request.setAttribute("is_successful", isScf);
		request.setAttribute("action_status", actionStatus);
		gotoVocabMng(request, response, Integer.MAX_VALUE);
	}
	
	public static int parseToInt(String stringToParse, int defaultValue) {
		try {
			return Integer.parseInt(stringToParse);
		} catch (NumberFormatException ex) {
			return defaultValue; // Use default value if parsing failed
		}
	}

}
