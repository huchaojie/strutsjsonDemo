package com.vrv.ieas.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.ProgressListener;


public class FileListener implements ProgressListener {

	private double megaBytes = -1;
	private HttpSession session;

	public FileListener(HttpServletRequest request) {
		session = request.getSession();
	}

	public void update(long pBytesRead, long pContentLength, int pItems) {
		double mBytes = pBytesRead;
		if (megaBytes == mBytes) {
			return;
		}
		megaBytes = mBytes;
		if (pContentLength != -1) {
			int percent = (int) (((float) mBytes / (float) (pContentLength)) * 100);
			session.setAttribute("percent", percent + "%");
		}
	}
}
