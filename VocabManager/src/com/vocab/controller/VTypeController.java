package com.vocab.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vocab.model.VocabType;
import com.vocab.service.VTypeService;

/**
 * Servlet implementation class VTypeController
 */
@WebServlet("/VTypeController")
public class VTypeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
		
	}
	

	private void gotoVTypeMng(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<VocabType> list = VTypeService.gets();
		request.setAttribute("list", list);
		request.getRequestDispatcher("/vtype_mng.jsp").forward(request, response);
	}

}
