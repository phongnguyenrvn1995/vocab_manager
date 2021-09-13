package com.vocab.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.vocab.consts.APIConsts;
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

	private boolean isMultipart;
	private String filePath;
//	private int maxFileSize = 50 * 1024;
	private int maxMemSize = 4 * 1024;
	private File file;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VocabController() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	    filePath = getServletContext().getInitParameter("file-upload"); 
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
	    isMultipart = ServletFileUpload.isMultipartContent(request);
	    if(isMultipart) {
	    	parseMultiPartForm(request, response);
	    	return;
	    }
		String _action = (String) request.getParameter("_action");
		System.out.println(_action);
		switch (_action) {
		case "save":
//			save(request, response);
			break;
		case "update":
			update(request, response);
			break;
		case "delete":
			delete(request, response);
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
	

	private void save(HttpServletRequest request, HttpServletResponse response,List<FileItem> fileItems) throws ServletException, IOException {
//		String vocab_id = getFieldDataMultiPartForm(fileItems, "vocab_id");
		String vocab_type = getFieldDataMultiPartForm(fileItems, "vocab_type");
		String vocab_lesson = getFieldDataMultiPartForm(fileItems, "vocab_lesson");
		String vocab_en = getFieldDataMultiPartForm(fileItems, "vocab_en");
		String vocab_ipa = getFieldDataMultiPartForm(fileItems, "vocab_ipa");
		String vocab_vi = getFieldDataMultiPartForm(fileItems, "vocab_vi");
		String vocab_description = getFieldDataMultiPartForm(fileItems, "vocab_description");
//		String vocab_sound_url = getFieldDataMultiPartForm(fileItems, "vocab_sound_url");
		FileItem soundFile = getFileItemMultiPartForm(fileItems, "vocab_sound_url");
//		System.out.println("vocab_id"            + " " + vocab_id           );
		System.out.println("vocab_type"          + " " + vocab_type         );
		System.out.println("vocab_lesson"        + " " + vocab_lesson       );
		System.out.println("vocab_en"            + " " + vocab_en           );
		System.out.println("vocab_ipa"           + " " + vocab_ipa          );
		System.out.println("vocab_vi"            + " " + vocab_vi           );
		System.out.println("vocab_description"   + " " + vocab_description  );
//		System.out.println("vocab_sound_url"     + " " + vocab_sound_url    );
		

		String actionStatus = "";
		boolean isScf = false;
		if (vocab_type != null && !vocab_type.isEmpty()
						&& vocab_lesson != null && !vocab_lesson.isEmpty()
								&& vocab_en != null && !vocab_en.isEmpty()
										&& vocab_ipa != null && !vocab_ipa.isEmpty()
												&& vocab_vi != null && !vocab_vi.isEmpty()
														&& vocab_description != null && !vocab_description.isEmpty() 
																&& soundFile != null && isAudioFile(soundFile)
//																&& vocab_sound_url != null && !vocab_sound_url.isEmpty()
																) {

			String soundFileName = new Date().getTime() + "_" + soundFile.getName();
			// Write the file
            if( soundFileName.lastIndexOf("\\") >= 0 ) {
               file = new File( filePath + soundFileName.substring( soundFileName.lastIndexOf("\\"))) ;
            } else {
               file = new File( filePath + soundFileName.substring(soundFileName.lastIndexOf("\\")+1)) ;
            }
            try {
				soundFile.write( file ) ;
				Vocab vocab = new Vocab();
				vocab.setVocab_type(parseToInt(vocab_type, -1));
				vocab.setVocab_lesson(parseToInt(vocab_lesson, -1));
				vocab.setVocab_en(vocab_en);
				vocab.setVocab_ipa(vocab_ipa);
				vocab.setVocab_vi(vocab_vi);
				vocab.setVocab_description(vocab_description);
				vocab.setVocab_sound_url(APIConsts.SOUND_DIR + soundFileName);

				Response resp = VocabService.save(vocab);
				actionStatus = resp.getResponse_description();
				isScf = resp.getResponse_id() == ResponseConst.SUCCESS;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				actionStatus = "Upload audio file failed, pls try again!";
			}
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
//			else if (vocab_sound_url == null || vocab_sound_url.isEmpty())
			else if(soundFile == null)
				actionStatus = "Sound must be not empty!";
			else if(!isAudioFile(soundFile))
				actionStatus = "Sound must be an audio file!";
		}
		request.setAttribute("is_successful", isScf);
		request.setAttribute("action_status", actionStatus);
		gotoVocabMng(request, response, Integer.MAX_VALUE);
	}
	

	private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vocab_id = request.getParameter("vocab_id");
		String vocab_type = request.getParameter("vocab_type");
		String vocab_lesson = request.getParameter("vocab_lesson");
		String vocab_en = request.getParameter("vocab_en");
		String vocab_ipa = request.getParameter("vocab_ipa");
		String vocab_vi = request.getParameter("vocab_vi");
		String vocab_description = request.getParameter("vocab_description");
		String vocab_sound_url = request.getParameter("vocab_sound_url");		
		int pageNo = parseToInt(request.getParameter("page"), 1);

		System.out.println("vocab_id"            + " " + vocab_id           );
		System.out.println("vocab_type"          + " " + vocab_type         );
		System.out.println("vocab_lesson"        + " " + vocab_lesson       );
		System.out.println("vocab_en"            + " " + vocab_en           );
		System.out.println("vocab_ipa"           + " " + vocab_ipa          );
		System.out.println("vocab_vi"            + " " + vocab_vi           );
		System.out.println("vocab_description"   + " " + vocab_description  );
		System.out.println("vocab_sound_url"     + " " + vocab_sound_url    );
		System.out.println(pageNo);

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
			vocab.setVocab_id(parseToInt(vocab_id, -1));
			vocab.setVocab_type(parseToInt(vocab_type, -1));
			vocab.setVocab_lesson(parseToInt(vocab_lesson, -1));
			vocab.setVocab_en(vocab_en);
			vocab.setVocab_ipa(vocab_ipa);
			vocab.setVocab_vi(vocab_vi);
			vocab.setVocab_description(vocab_description);
			vocab.setVocab_sound_url(vocab_sound_url);

			Response resp = VocabService.update(vocab);
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
		gotoVocabMng(request, response, pageNo);
	}

	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int vocab_id = parseToInt(request.getParameter("vocab_id"), -1 );
		int pageNo = parseToInt(request.getParameter("page"), 1);
		System.out.println("vocab_id = " + vocab_id);
		System.out.println("pageNo = " + pageNo);
		
		String actionStatus;
		boolean isScf = false;
		Response resp = VocabService.delete(vocab_id);
		actionStatus = resp.getResponse_description();
		isScf = resp.getResponse_id() == ResponseConst.SUCCESS;

		request.setAttribute("is_successful", isScf);
		request.setAttribute("action_status", actionStatus);
		gotoVocabMng(request, response, pageNo);
	}

	private void parseMultiPartForm(HttpServletRequest request, HttpServletResponse response) {
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);

		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File("c:\\temp"));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// maximum file size to be uploaded.
		try {
			// Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);
			String _action = getFieldDataMultiPartForm(fileItems, "_action");
			System.out.println(_action);
			switch (_action) {
			case "save":
				save(request, response, fileItems);
				break;
			case "update":
				update(request, response);
				break;
			case "delete":
				delete(request, response);
				break;
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private String getFieldDataMultiPartForm(List<FileItem> fileItems, String fieldName) {
		String ret = null;
		try {
			// Process the uploaded file items
			Iterator<FileItem> i = fileItems.iterator();

			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				InputStream input = fi.getInputStream();
				if(fi.isFormField() && fi.getFieldName().equals(fieldName)) {
					byte[] str = new byte[input.available()];
	                input.read(str);
	                ret = new String(str, StandardCharsets.UTF_8);
					System.out.println(fieldName + " = " + ret);
					System.out.println("==========================");
	                break;
				}				
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return ret;
	}
	

	private FileItem getFileItemMultiPartForm(List<FileItem> fileItems, String fieldName) {
		FileItem ret = null;
		try {
			// Process the uploaded file items
			Iterator<FileItem> i = fileItems.iterator();

			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if(!fi.isFormField() && fi.getFieldName().equals(fieldName)) {
	               System.out.println("fi.getName() = " + fi.getName());
	               System.out.println("fi.getContentType() = " + fi.getContentType());
	               System.out.println("fi.isInMemory() = " + fi.isInMemory());
	               System.out.println("fi.getSize() = " + fi.getSize());
					ret = fi;
	                break;
				}				
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return ret;
	}
	
	private boolean isAudioFile(FileItem fileItem) {
		return (fileItem.getContentType().startsWith("audio/"));
	}
	
	public static int parseToInt(String stringToParse, int defaultValue) {
		try {
			return Integer.parseInt(stringToParse);
		} catch (NumberFormatException ex) {
			return defaultValue; // Use default value if parsing failed
		}
	}

}
