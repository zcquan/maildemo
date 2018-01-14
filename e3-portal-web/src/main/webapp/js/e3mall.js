var E3 = E3MALL = {
	checkLogin : function(){
		//获取cookie中token
		var _ticket = $.cookie("E3_TOKEN");
		if(!_ticket){
			return ;
		}
		//向单点登录系统发送请求,获取用户身份信息
		$.ajax({
			url : "http://localhost:8088/user/token/" + _ticket,
			dataType : "jsonp",
			type : "GET",
			success : function(data){
				if(data.status == 200){
					var username = data.data.username;
					var html = username + "，欢迎来到宜立方！<a href=\"http://www.taotao.com/user/logout.html\" class=\"link-logout\">[退出]</a>";
					//页面回显
					$("#loginbar").html(html);
				}
			}
		});
	}
}

$(function(){
	// 查看是否已经登录，如果已经登录查询登录信息
	E3.checkLogin();
});