package com.stardict.StarDictUtils.cn.star.dict;

public class Word {
	/** 词条 */
	private String word;
	/** 解释 */
	private String explanation;
	/** 发音 */
	private byte[] sound;
	/** 同义词 */
	private Word[] syn;

	public Word() {
	}

	public Word(String word, String explanation) {
		super();
		this.word = word;
		this.explanation = explanation;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public byte[] getSound() {
		return sound;
	}

	public void setSound(byte[] sound) {
		this.sound = sound;
	}

	public Word[] getSyn() {
		return syn;
	}

	public void setSyn(Word[] syn) {
		this.syn = syn;
	}

	public String toString() {
		return String.format("{word:\"%s\",explanation:\"%s\"}", this.word, this.explanation);
	}
}
