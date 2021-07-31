package com.vocab.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vocab.model.Response;
import com.vocab.model.VocabType;
import com.vocab.service.VTypeService;

/**
 * Servlet implementation class VTypeController
 */
@WebServlet("/VTypeController")
public class VTypeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int itemLimited = 5;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VTypeController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		gotoVTypeMng(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String _action = (String) request.getParameter("_action");
		System.out.println(_action);
		switch(_action) {
		case "save":
			save(request, response);
			break;
		case "delete":
			System.out.println(request.getParameter("v_id"));
			//save(request, response);
			break;
		}
	}
	
	private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vtype = request.getParameter("vocab_type_desc");
		System.out.println(vtype);
		String actionStatus;
		if(vtype != null && !vtype.isEmpty()) {
			VocabType vocabType = new VocabType();
			vocabType.setVocab_type_name(vtype);
			Response resp = VTypeService.save(vocabType);
			actionStatus = resp.getResponse_description();
		} else {
			actionStatus = "Description must be not empty!";
		}
		request.setAttribute("action_status", actionStatus);
		gotoVTypeMng(request, response, Integer.MAX_VALUE);
	}
	

	private void gotoVTypeMng(HttpServletRequest request, HttpServletResponse response, int ...page) throws ServletException, IOException {		
		List<VocabType> list = VTypeService.gets();
		int total_page = (int)Math.ceil((float)list.size() / (float)itemLimited);
		int pageNo = page.length == 0 ? parseToInt(request.getParameter("page"), 1) : page[0];
		pageNo = pageNo > total_page ? total_page : pageNo;
		
		List<VocabType> filterList = list.stream().skip(
				pageNo * itemLimited - itemLimited
				).limit(itemLimited).collect(Collectors.toList());
		
		request.setAttribute("list", filterList);
		request.setAttribute("page", pageNo);
		request.setAttribute("total_page", total_page);
		request.getRequestDispatcher("/vtype_mng.jsp").forward(request, response);
	}
	
	public static int parseToInt(String stringToParse, int defaultValue) {
	    try {
	       return Integer.parseInt(stringToParse);
	    } catch(NumberFormatException ex) {
	       return defaultValue; //Use default value if parsing failed
	    }
	}
}
