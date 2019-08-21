package com.vrv.ieas.action;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
/**
 * 
 * 说 明： 处理文件上传类
 * @author 作 者：chenjun
 * 		   E-mail: chenjun@vrvmail.com.cn
 * @version V1.0 
 * 			创建时间：2014年3月24日 下午2:52:10
 */
public class FileOperatorAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	/**
	 * 保存文件
	 * @param proRealPath  项目相对路径 /WH-TEST/
	 * @return
	 */
	public String add() throws IOException {
		msg = "false";
		try {
			msg = "true";
		} catch (Exception e) {
			msg = "false";
			e.printStackTrace();
		}
		return SUCCESS;
	}

	/**
	 * 方法  listenPresent 监听文件上传的进度
	 * @return perStr String 上传进度百分比
	 */
	public String listenPresent() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String perStr = (String) request.getSession().getAttribute("percent");
		msg = perStr;
		return SUCCESS;
	}
	/**
	 * 方法 clearlistenPresent 清空进度缓存数据
	 * @return
	 */
	public String clearlistenPresent() {
		HttpServletRequest request = ServletActionContext.getRequest();
		request.getSession().setAttribute("percent", "0%");
		msg = "true";
		return SUCCESS;
	} 
	
	private String msg;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
