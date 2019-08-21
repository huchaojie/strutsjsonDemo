<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css"
	href="js/themes/default/easyui.css">
<link href="styles/default.css" rel="stylesheet" type="text/css" />
<link href="styles/jquery.loadmask.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="js/themes/icon.css">
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="js/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/jquery.loadmask.js"></script>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript" src="app/js/Filemanager/filemanager.js"></script>
<script type="text/javascript" src="js/json2.js"></script>
</head>
<body>
	<div>
		<div>
			<div>
				<input name="input" type="button" class="toolbtn" value="上传窗口"
					onclick="fileManager_Main.add();" />
			</div>
		</div>
	</div>

	<div id="dlg_fileinfo" class="easyui-dialog" closed="true" modal="true"
		style="width: 480px; height: 300px; padding: 10px 20px">
		<form id="fm_fileinfo_info" method="post" novalidate
			enctype="multipart/form-data">
			<div>
				<div>
					<div>
						<div>
							<table>
								<tr>
									<td><font color=RED>*</font>文件: <input type="file"
										name="file" class="file" id="fileField" size="28"
										onchange="fileManager_Main.fileInit(this.value);" /> <input
										type="button" class="button" id="save"
										onclick="return fileManager_Main.file_upload()" value="上 传" />
									</td>
								</tr>
							</table>
							<br /> <br /> <br />
							<table class="itemTabEdit">
								<div id="prosbar" class="easyui-progressbar" style="width: 385px; display: none;"></div>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="clear"></div>
		</form>
	</div>
</body>
</html>