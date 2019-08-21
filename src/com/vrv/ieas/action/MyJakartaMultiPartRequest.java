package com.vrv.ieas.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.StrutsConstants;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import com.opensymphony.xwork2.inject.Inject;
import com.vrv.ieas.utils.FileOperatorUtil;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItem;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;


public class MyJakartaMultiPartRequest implements MultiPartRequest {
	private long lastfileLen = 0L;
	// maps parameter name -> List of FileItem objects
	protected Map<String, List<FileItem>> files = new HashMap<String, List<FileItem>>();
	// maps parameter name -> List of param values
	protected Map<String, List<String>> params = new HashMap<String, List<String>>();
	// any errors while processing this request
	protected List<String> errors = new ArrayList<String>();
	protected long maxSize = 100 * 1024 * 1024l; //in bytes  = 100M

	@Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
	public void setMaxSize(String maxSize) {
		this.maxSize = Long.parseLong(maxSize);
	}

	/***
	 * parseRequest List<FileItem> 重写JakartaMultiPartRequest类，加入文件上传进度监听
	 * @author chenjun 2013-03-21
	 * @return
	 * @throws FileUploadException
	 */
	@SuppressWarnings("unchecked")
	protected List<FileItem> parseRequest(HttpServletRequest request,
			String saveDir) throws FileUploadException {
		long st = System.currentTimeMillis();
		String curProPath = request.getServletContext().getRealPath("");
		String filePath = FileOperatorUtil.createScrrenManagerFolder(curProPath);
		File file = new File(filePath);
		if (!file.exists())
			file.mkdirs();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		// param sizeMax The maximum allowed size, in bytes. The default value of -1 indicates, that there is no limit.
		upload.setFileSizeMax(-1L);
		// Sets the maximum allowed size of a single uploaded file, in bytes
		upload.setSizeMax(maxSize);
		upload.setHeaderEncoding("UTF-8");
		// 设置进度监听器
		upload.setProgressListener(new FileListener(request));
		try {
			String fileName = null;
			List<?> items = upload.parseRequest(request);
			FileItem item = null;
			for (int i = 0; i < items.size(); i++) {
				item = (FileItem) items.get(i);
				if( null != item.getName() && (!"".equals(item.getName())) ){
					FileOperatorUtil.isDoubleFileNameAndNew(filePath,item.getName().trim());
					fileName = filePath + item.getName().replaceAll(" ", "");
					lastfileLen = item.getSize();//Byte
					if (item.isInMemory()) {
						// 处理小文件，数据直接放入内存中
						item.write(new File(fileName));
					} else {
						// 保存文件
						if (!item.isFormField() && item.getName().length() > 0) {
							// item.write(new File(fileName));
							((DiskFileItem) item).getStoreLocation().renameTo(new File(fileName));
						}
					}
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long et = System.currentTimeMillis();
		System.out.println("文件上传速度为：" + (Double.valueOf(lastfileLen)/(1024*1024))/(Double.valueOf(String.valueOf(et - st))/1000) + "兆/秒,即mb/s");
		/** 本机测试80M兆/秒速度情况
		 * 文件上传速度为：113.34725895553785兆/秒,即mb/s
		 * 文件上传速度为：118.41494564505518兆/秒,即mb/s
		 * 文件上传速度为：117.71322744864004兆/秒,即mb/s
		 * 文件上传速度为：120.38852807247277兆/秒,即mb/s
		 * 文件上传速度为：118.76895146163233兆/秒,即mb/s
		 */
		return upload.parseRequest(createRequestContext(request));
	}

	public void parse(HttpServletRequest request, String saveDir)
			throws IOException {
		try {
			processUpload(request, saveDir);
		} catch (FileUploadException e) {
			errors.add(e.getMessage());
		}
	}

	private void processUpload(HttpServletRequest request, String saveDir)
			throws FileUploadException, UnsupportedEncodingException {
		for (FileItem item : parseRequest(request, saveDir)) {
			if (item.isFormField()) {
				processNormalFormField(item, request.getCharacterEncoding());
			} else {
				processFileField(item);
			}
		}
	}

	private void processFileField(FileItem item) {

		// Skip file uploads that don't have a file name - meaning that no file
		// was selected.
		if (item.getName() == null || item.getName().trim().length() < 1) {
			return;
		}

		List<FileItem> values;
		if (files.get(item.getFieldName()) != null) {
			values = files.get(item.getFieldName());
		} else {
			values = new ArrayList<FileItem>();
		}

		values.add(item);
		files.put(item.getFieldName(), values);
	}

	private void processNormalFormField(FileItem item, String charset)
			throws UnsupportedEncodingException {
		List<String> values;
		if (params.get(item.getFieldName()) != null) {
			values = params.get(item.getFieldName());
		} else {
			values = new ArrayList<String>();
		}

		// note: see http://jira.opensymphony.com/browse/WW-633
		// basically, in some cases the charset may be null, so
		// we're just going to try to "other" method (no idea if this
		// will work)
		if (charset != null) {
			values.add(item.getString(charset));
		} else {
			values.add(item.getString());
		}
		params.put(item.getFieldName(), values);
	}

	/*
	 * 重写此方法，加入上传进度的监听 private List<FileItem> parseRequest(HttpServletRequest
	 * servletRequest, String saveDir) throws FileUploadException {
	 * DiskFileItemFactory fac = createDiskFileItemFactory(saveDir);
	 * ServletFileUpload upload = new ServletFileUpload(fac);
	 * upload.setSizeMax(maxSize); return
	 * upload.parseRequest(createRequestContext(servletRequest)); }
	 */
	@SuppressWarnings("unused")
	private DiskFileItemFactory createDiskFileItemFactory(String saveDir) {
		DiskFileItemFactory fac = new DiskFileItemFactory();
		// Make sure that the data is written to file
		fac.setSizeThreshold(0);
		if (saveDir != null) {
			fac.setRepository(new File(saveDir));
		}
		return fac;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileParameterNames()
	 */
	public Enumeration<String> getFileParameterNames() {
		return Collections.enumeration(files.keySet());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType(java.lang.String)
	 */
	public String[] getContentType(String fieldName) {
		List<FileItem> items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<String> contentTypes = new ArrayList<String>(items.size());
		for (FileItem fileItem : items) {
			contentTypes.add(fileItem.getContentType());
		}

		return contentTypes.toArray(new String[contentTypes.size()]);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java.lang.String)
	 */
	public File[] getFile(String fieldName) {
		List<FileItem> items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<File> fileList = new ArrayList<File>(items.size());
		for (FileItem fileItem : items) {
			fileList.add(((DiskFileItem) fileItem).getStoreLocation());
		}

		return fileList.toArray(new File[fileList.size()]);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames(java.lang.String)
	 */
	public String[] getFileNames(String fieldName) {
		List<FileItem> items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<String> fileNames = new ArrayList<String>(items.size());
		for (FileItem fileItem : items) {
			fileNames.add(getCanonicalName(fileItem.getName()));
		}

		return fileNames.toArray(new String[fileNames.size()]);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName(java.lang.String)
	 */
	public String[] getFilesystemName(String fieldName) {
		List<FileItem> items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<String> fileNames = new ArrayList<String>(items.size());
		for (FileItem fileItem : items) {
			fileNames.add(((DiskFileItem) fileItem).getStoreLocation()
					.getName());
		}

		return fileNames.toArray(new String[fileNames.size()]);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String name) {
		List<String> v = params.get(name);
		if (v != null && v.size() > 0) {
			return v.get(0);
		}

		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterNames()
	 */
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(params.keySet());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String name) {
		List<String> v = params.get(name);
		if (v != null && v.size() > 0) {
			return v.toArray(new String[v.size()]);
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	public List getErrors() {
		return errors;
	}

	/***
	 * Returns the canonical name of the given file.
	 * 
	 * @param filename
	 *            the given file
	 * @return the canonical name of the given file
	 */
	private String getCanonicalName(String filename) {
		int forwardSlash = filename.lastIndexOf("/");
		int backwardSlash = filename.lastIndexOf("\\");
		if (forwardSlash != -1 && forwardSlash > backwardSlash) {
			filename = filename.substring(forwardSlash + 1, filename.length());
		} else if (backwardSlash != -1 && backwardSlash >= forwardSlash) {
			filename = filename.substring(backwardSlash + 1, filename.length());
		}

		return filename;
	}

	/***
	 * Creates a RequestContext needed by Jakarta Commons Upload.
	 * 
	 * @param req
	 *            the request.
	 * @return a new request context.
	 */
	private RequestContext createRequestContext(final HttpServletRequest req) {
		return new RequestContext() {
			public String getCharacterEncoding() {
				return req.getCharacterEncoding();
			}

			public String getContentType() {
				return req.getContentType();
			}

			public int getContentLength() {
				return req.getContentLength();
			}

			public InputStream getInputStream() throws IOException {
				InputStream in = req.getInputStream();
				if (in == null) {
					throw new IOException("Missing content in the request");
				}
				return req.getInputStream();
			}
		};
	} 
}
