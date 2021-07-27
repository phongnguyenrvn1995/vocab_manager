package com.vocab.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vocab.consts.APIConsts;
import com.vocab.model.VocabType;

public class VTypeService {
	public static void main(String[] ar) {
		System.out.println("OK");
		gets();
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

}
