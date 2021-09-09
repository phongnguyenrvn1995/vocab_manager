package com.vocab.service;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vocab.consts.APIConsts;
import com.vocab.model.Response;
import com.vocab.model.Vocab;
import com.vocab.utils.HttpUtils;

public class VocabService {

	public static void main(String[] ar) {
		System.out.println("OK");
//		System.out.println(gets("en 1", "", "").size());
//		System.out.println(getsCount("en ", "", ""));
//		Vocab vocab = new Vocab();
//		vocab.setVocab_id(112);
//		vocab.setVocab_en("en");
//		vocab.setVocab_ipa("ipa");
//		vocab.setVocab_vi("vi");
//		vocab.setVocab_lesson(2);
//		vocab.setVocab_type(2);
//		vocab.setVocab_sound_url("url");
//		vocab.setVocab_description("desc");
//		update(vocab);
//		delete(102);
	}

	public static int getsCount(String q, String typeID, String lessonID){
		q = URLEncoder.encode(q, StandardCharsets.UTF_8);
		String url = APIConsts.BASE_URL + APIConsts.API_VOCAB_GETS_COUNT;
		
		url += "?" + APIConsts.KEY_SEARCH_STR + q + 
				"&" + APIConsts.KEY_TYPE_ID + typeID +
				"&" + APIConsts.KEY_LESSON_ID + lessonID;

		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String count = HttpUtils.request(url, method, contentType, msg);
		System.out.println(count);
		return Integer.parseInt(count);
	}

	public static List<Vocab> gets(String q, String typeID, String lessonID, int ...limitAndOffset){
		q = URLEncoder.encode(q, StandardCharsets.UTF_8);
		String url = APIConsts.BASE_URL + APIConsts.API_VOCAB_GETS;
		
		if(limitAndOffset.length == 2) {
			url += "/" + limitAndOffset[0] + "/" + limitAndOffset[1];
		}
		
		url += "?" + APIConsts.KEY_SEARCH_STR + q + 
				"&" + APIConsts.KEY_TYPE_ID + typeID +
				"&" + APIConsts.KEY_LESSON_ID + lessonID;
		
		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
		
		Type listType = new TypeToken<ArrayList<Vocab>>(){}.getType();
		
		List<Vocab> list = new Gson().fromJson(json, listType);
		return list;
	}
	

	public static Response save(Vocab vocab){
		String url = APIConsts.BASE_URL + APIConsts.API_VOCAB_SAVE;
		String method = HttpMethod.PUT;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "vocab_type=" + vocab.getVocab_type() +
				"&vocab_lesson=" + vocab.getVocab_lesson() +
				"&vocab_en=" + vocab.getVocab_en() +
				"&vocab_ipa=" + vocab.getVocab_ipa() + 
				"&vocab_vi=" + vocab.getVocab_vi() + 
				"&vocab_description=" + vocab.getVocab_description() +
				"&vocab_sound_url=" +vocab.getVocab_sound_url();
		
		
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}
	

	public static Response update(Vocab vocab){
		String url = APIConsts.BASE_URL + APIConsts.API_VOCAB_UPDATE;
		String method = HttpMethod.POST;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "&vocab_id=" + vocab.getVocab_id() +
				"&vocab_type=" + vocab.getVocab_type() +
				"&vocab_lesson=" + vocab.getVocab_lesson() +
				"&vocab_en=" + vocab.getVocab_en() +
				"&vocab_ipa=" + vocab.getVocab_ipa() + 
				"&vocab_vi=" + vocab.getVocab_vi() + 
				"&vocab_description=" + vocab.getVocab_description() +
				"&vocab_sound_url=" +vocab.getVocab_sound_url();
		
		
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}
	
	public static Response delete(int id){
		String url = APIConsts.BASE_URL + APIConsts.API_VOCAB_DELETE + id;
		String method = HttpMethod.DELETE;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		
		
		String json = HttpUtils.request(url, method, contentType, "");
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}
}
