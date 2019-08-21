package com.vrv.ieas.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 
 * 说 明： 文件上传处理单元类
 * @author 作 者：chenjun  E-mail: chenjun@vrvmail.com.cn
 * @version V1.0  创建时间：2014年3月24日 下午2:40:31
 */
public class FileOperatorUtil {
	// 定义文件存放项目路径,默认为：/WH-TEST/resources/
	private static final String fileSaveRealPath = "resources/";

	/**
	 * 方法：isDoubleFileNameAndCreate 判断上传的文件是否存在重名,如果已经存在，存在则修改其文件名
	 * @param proRealPath  String 文件目录路径 ：savePath + /resources/
	 * @param filename String 上传的原文件名
	 */
	public static void isDoubleFileNameAndNew(String proRealPath,String filename) {
		try {
			String orgfileNameAndPath = "";
			orgfileNameAndPath = proRealPath + filename;
			File file = new File(orgfileNameAndPath);
			if (file.exists()) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HHmmss");
				String systime = df.format(new Date());
				String fileAppeadName = systime.replace("/", "");// Create  format 20130319103000
				Integer inde = filename.lastIndexOf(".");
				// 更改已存在的文件名称
				String fileNameStr = filename.substring(0, inde) + "-" + fileAppeadName + filename.substring(inde, filename.length());
				file.renameTo(new File(proRealPath + fileNameStr));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		 
	/**
	 * 方法：createScrrenManagerFolder String 根据目录路径创建文件目录并赋予权限,支持Win/Linux OS
	 * @param savePath String  当前项目根路径
	 * @return lastsFolderPath  String 文件路径：文件目录路径 （savePath + /resources/ ）
	 */
	public static String createScrrenManagerFolder(String savePath) {
		String lastsFolderPath = "";
		try {
			lastsFolderPath = getProjectRoot(savePath) + fileSaveRealPath;// E:\Tomcat7\WH-TEST\resources\
			File fp = new File(lastsFolderPath);
			// 创建目录
			if (!fp.exists()) {
				fp.mkdirs();// 目录不存在的情况下，创建目录。
				if (-1 != System.getProperties().getProperty("os.name")
						.toLowerCase().indexOf("windows")) {
					// 1 windows OS：通过io File类对文件路径赋予读写权限
					if (!fp.canRead()) {
						fp.setReadable(true);
					}
					if (!fp.canWrite()) {
						fp.setWritable(true);
					}
					if (!fp.canExecute()) {
						fp.setExecutable(true);
					}
				} else {
					// 1 2 其它操作系统
					// :通过untime.getRuntime().exec()执行command对文件路径赋予读写权限
					String filepath = lastsFolderPath;
					String command = "chmod -R 755 " + filepath;
					@SuppressWarnings("unused")
					Runtime runtime = Runtime.getRuntime();
					try {
						@SuppressWarnings("unused")
						Process exec = Runtime.getRuntime().exec(command);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastsFolderPath;
	}

	/**
	 * 获取项目当前路径 eclipse 兼容测试时路径 + TOMCAT 部署路径
	 * @param savePath  String 项目当前根路径 /
	 * @return realpathStr String 项目当前根路径 /WH_TEST
	 */
	public static String getProjectRoot(String savePath) {
		String realpathStr = "";
		if (savePath.indexOf("wtpwebapps") != -1) {
			int index1 = savePath.indexOf("\\.");
			int index2 = savePath.lastIndexOf("\\");
			realpathStr = savePath.substring(0, index1) + savePath.substring(index2, savePath.length()) + "\\WebContent\\";
		} else {
			realpathStr = savePath + "/";
		}
		return realpathStr;
	}
}
