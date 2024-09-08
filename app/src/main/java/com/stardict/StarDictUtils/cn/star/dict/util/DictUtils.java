package com.stardict.StarDictUtils.cn.star.dict.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DictUtils {
	public static void unGzipFile(File srcFile,File desFile){
		FileInputStream is;
		FileOutputStream os;
		InputStream gzis;
		final int MAX_BYTE = 1024*1000;
		int len = 0;
		byte [] b = new byte[MAX_BYTE];
		try {
			is = new FileInputStream(srcFile);
			os = new FileOutputStream(desFile);
			try {
				gzis = new GZIPInputStream(is);
				while((len = gzis.read(b))!=-1)
					os.write(b,0,len);
				os.flush();
				gzis.close();
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
//		srcFile.delete();
	}

	public static void gzipFile(File srcFile,File desFile) {

		FileInputStream fis;
		FileOutputStream fos;
		GZIPOutputStream gzos;

		final int MAX_BYTE = 1024 * 1000;
		int len = 0;
		byte[] b = new byte[MAX_BYTE];

		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(desFile);
			gzos = new GZIPOutputStream(fos);
			while ((len = fis.read(b)) != -1)
				gzos.write(b, 0, len);
			gzos.flush();
			gzos.close();
			fos.close();
			fis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/** 解压数据，将gzip（.gz , .dz）压缩的文件解压到输出流中 */
	public static void unZip(File dz, OutputStream out) {
		GZIPInputStream gin = null;
		byte[] buffer = new byte[1024];
		try {
			int i = 0;
			gin = new GZIPInputStream(new FileInputStream(dz));
			while ((i = gin.read(buffer)) > 0) {
				out.write(buffer, 0, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (gin != null) {
				try {
					gin.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/** 获取文件扩展名 ,不包含(.)点 */
	public static String getExtend(String fileName) {
		String ext = null;
		int index = fileName.lastIndexOf(".");
		if (index > 0 && index + 1 < fileName.length())
			ext = fileName.substring(index + 1);
		return ext;
	}

	/** 获取文件名字部分，不包含扩展名 */
	public static String getName(String fileName) {
		String ext = null;
		int index = fileName.lastIndexOf(".");
		if (index > 0 && index < fileName.length())
			ext = fileName.substring(0, index);
		else
			ext = fileName;
		return ext;
	}

	/**
	 * 格式化数据长度，保留2位小数
	 * 
	 * @param length
	 *            数据长度
	 * @return 格式化的值，带单位
	 */
	public static String formatSize(long length) {
		double size = length;
		String[] format = { "B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
		int num = 0;
		while (size > 1024) {
			size /= 1024.0;
			num++;
		}

		return String.format("%.2f %s", size, format[num]);
	}

	public static void main(String[] args) {
		System.out.println(formatSize(Integer.MAX_VALUE));
	}
}
