/**
 * 常用的公共JS类
 * 
 */
Common = new function() {
	var _this = this;

	/**
	 * 指定位置显示
	 */
	$.extend($.messager, {
		showBySite : function(options, param) {
			var site = $.extend({
				left : "",
				top : "",
				right : 0,
				bottom : -document.body.scrollTop
						- document.documentElement.scrollTop
			}, param || {});
			var win = $("body > div .messager-body");
			if (win.length <= 0)
				$.messager.show(options);
			win = $("body > div .messager-body");
			win.window("window").css({
				left : site.left,
				top : site.top,
				right : site.right,
				zIndex : $.fn.window.defaults.zIndex++,
				bottom : site.bottom
			});
		}
	});

	this.showBySite = function(param1Tit, param2Msg) {
		$.messager.showBySite({
			title : param1Tit,
			msg : param2Msg,
			showType : 'slide',
			timeout : 2000
		}, {
			top : 0,
			left : ($(document.body).width() / 2 - 100),
			bottom : ""
		});
	};
}