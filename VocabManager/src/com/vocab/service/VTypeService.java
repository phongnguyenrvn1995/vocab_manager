package com.vocab.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vocab.consts.APIConsts;
import com.vocab.model.Response;
import com.vocab.model.VocabType;

public class VTypeService {
	public static void main(String[] ar) {
		System.out.println("OK");
		VocabType v = new VocabType();
		v.setVocab_type_name("giấc mơ thần tiên");
		save(v);
	}

	public static List<VocabType> gets(){
		String url = APIConsts.BASE_URL + APIConsts.API_VTYPE_GETS;
		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
		
		Type listType = new TypeToken<ArrayList<VocabType>>(){}.getType();
		
		List<VocabType> list = new Gson().fromJson(json, listType);
		return list;
	}
	

	public static Response save(VocabType vocabType){
		String url = APIConsts.BASE_URL + APIConsts.API_VTYPE_SAVE;
		String method = HttpMethod.PUT;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "vocab_type_name=" + vocabType.getVocab_type_name();
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}

	public static Response delete(int id){
		String url = APIConsts.BASE_URL + APIConsts.API_VTYPE_DELETE + id;
		String method = HttpMethod.DELETE;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String json = HttpUtils.request(url, method, contentType, null);
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}

	
}
