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

public class ResponseService {
	public static void main(String[] ar) {
		System.out.println("OK");
		gets();
	}
	
	public static List<Response> gets(){
		String url = APIConsts.BASE_URL + APIConsts.API_RESP_GETS;
		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
		
		Type listType = new TypeToken<ArrayList<Response>>(){}.getType();
		
		List<Response> list = new Gson().fromJson(json, listType);
		return list;
	}

}
