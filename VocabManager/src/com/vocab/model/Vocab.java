package com.vocab.model;

public class Vocab {
	private int vocab_id;
	private int vocab_type;
	private int vocab_lesson;
	private String vocab_en;
	private String vocab_ipa;
	private String vocab_vi;
	private String vocab_description;
	private String vocab_sound_url;

	public int getVocab_id() {
		return vocab_id;
	}

	public void setVocab_id(int vocab_id) {
		this.vocab_id = vocab_id;
	}

	public int getVocab_type() {
		return vocab_type;
	}

	public void setVocab_type(int vocab_type) {
		this.vocab_type = vocab_type;
	}

	public int getVocab_lesson() {
		return vocab_lesson;
	}

	public void setVocab_lesson(int vocab_lesson) {
		this.vocab_lesson = vocab_lesson;
	}

	public String getVocab_en() {
		return vocab_en;
	}

	public void setVocab_en(String vocab_en) {
		this.vocab_en = vocab_en;
	}

	public String getVocab_ipa() {
		return vocab_ipa;
	}

	public void setVocab_ipa(String vocab_ipa) {
		this.vocab_ipa = vocab_ipa;
	}

	public String getVocab_vi() {
		return vocab_vi;
	}

	public void setVocab_vi(String vocab_vi) {
		this.vocab_vi = vocab_vi;
	}

	public String getVocab_description() {
		return vocab_description;
	}

	public void setVocab_description(String vocab_description) {
		this.vocab_description = vocab_description;
	}

	public String getVocab_sound_url() {
		return vocab_sound_url;
	}

	public void setVocab_sound_url(String vocab_sound_url) {
		this.vocab_sound_url = vocab_sound_url;
	}
}
