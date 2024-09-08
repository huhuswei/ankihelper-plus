package com.stardict.StarDictUtils.cn.star.dict;

import java.io.Serializable;

/** 词典描述文件，主要记录一些词典的基本信息（对应文件：.ifo） */
public class DictInfo implements Serializable {
	private static final long serialVersionUID = 6815656965266019884L;
	/**
	 * required 现在的官方解析软件只解析2.4.2和3.0.0 拒绝读取替他版本。<br>
	 * 如果是3.0.0版本，会用到idxOffsetBits属性。
	 */
	private String version;
	/** required 词典名称，必须项。 */
	private String bookName;
	/** required 词典收词数，idx中的总词数。注意不包括.syn中的词数 */
	private int wordCount;
	/** required if ".syn" file exists. 同义词词数，多用于日文中的假名等 */
	private int synWordCount; //
	/** required 索引文件大小。为压缩前的大小。 */
	private int idxFileSize; //
	/** New in 3.0.0 设定值为32或64。指定索引文件中 每 个 索引的 长度位数 */
	private int idxOffsetBits;
	/** 作者 */
	private String author;
	/** 邮箱 */
	private String email;
	/** 博客等地址 */
	private String website;
	/**
	 * 描述 You can use &lt;br&gt; for new line.
	 */
	private String description;
	/** 日期 */
	private String date;
	/**
	 * very important. <br>
	 * e.g. sametypesequence=W <br>
	 * .dict数据文件全部由 .wav声音文件组成 声音数据文件格式,<br>
	 * 所以读取时可以直接忽略w 类型和长度,按照idx文件中的数据长度读取即可。<br>
	 * word_1_data word_2_data word_3_data …… <br>
	 * sametypesequence=tm word_1_data_1_data +/0 <br>
	 * t:音标格式 word_1_data_2_data 　 <br>
	 * m:纯文本数据格式
	 */
	private String sameTypesEquence;

	public DictInfo() {
	}

	public DictInfo(String version, String bookName, int wordCount, int idxFileSize, String author, String description) {
		super();
		this.version = version;
		this.bookName = bookName;
		this.wordCount = wordCount;
		this.idxFileSize = idxFileSize;
		this.author = author;
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getSynWordCount() {
		return synWordCount;
	}

	public void setSynWordCount(int synWordCount) {
		this.synWordCount = synWordCount;
	}

	public int getIdxFileSize() {
		return idxFileSize;
	}

	public void setIdxFileSize(int idxFileSize) {
		this.idxFileSize = idxFileSize;
	}

	public int getIdxOffsetBits() {
		return idxOffsetBits;
	}

	public void setIdxOffsetBits(int idxOffsetBits) {
		this.idxOffsetBits = idxOffsetBits;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSameTypesEquence() {
		return sameTypesEquence;
	}

	public void setSameTypesEquence(String sameTypesEquence) {
		this.sameTypesEquence = sameTypesEquence;
	}

}
