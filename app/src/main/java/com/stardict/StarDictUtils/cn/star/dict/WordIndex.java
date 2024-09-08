package com.stardict.StarDictUtils.cn.star.dict;

import java.io.Serializable;

/** 词典索引 */
public class WordIndex implements Serializable {
	private static final long serialVersionUID = 5199644831895615335L;

	/** 单词内容（以\0结尾） */
	private String word;
	/** 单词数据在dict文件中的偏移 */
	private Integer offset;
	/** 单词数据的长度 */
	private Integer length;

	public WordIndex() {
	}

	public WordIndex(String word, Integer offset, Integer length) {
		super();
		this.word = word;
		this.offset = offset;
		this.length = length;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String toString() {
		return String.format("{ word:\"%s\", offset:\"%d\", length:\"%d\" }", this.word, this.offset, this.length);
	}
}
