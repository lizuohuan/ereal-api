var YZ = function () {};
YZ.prototype = {
	ip: 'http://'+window.location.host+'/api',
	//ipImg:'http://'+window.location.host+'/images',
	ipImg:'http://'+window.location.host+'/images',
	ipUrl: location.href.split('#')[0],
	//手机号码正则表达式
	isMobile : /^(((13[0-9]{1})|(18[0-9]{1})|(17[6-9]{1})|(15[0-9]{1}))+\d{8})$/,
	//电话号码正则表达式
	isPhone : /[0-9-()（）]{7,18}/,
	//身份证正则表达式
	isIHCIard :   /^\d{15}(\d{2}[\d|X])?$/,
	//6-12的密码
	isPwd : /[A-Za-z0-9]{6,20}/,
	//输入的是否为数字
	isNumber : /^[0-9]*$/,
	//检查小数
	isDouble : /^\d+(\.\d+)?$/,
	//输入的只能为数字和字母
	isNumberChar: /^[0-9a-zA-Z]*$/,
	//用户名
	isUserName : /[\w\u4e00-\u9fa5]/,
	//emoji 表情正则
	isEmoji : /\uD83C[\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F]/g,
	//验证邮箱
	isEmail : /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/,
	//只能输入汉字
	isChinese : /[\u4e00-\u9fa5]/gm,
	//获取url中的参数
	getUrlParam : function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
		var r = window.location.search.substr(1).match(reg); //匹配目标参数
		if(r != null){
			return unescape(r[2]);
		}else{
			return null; //返回参数值
		}
	},
	//时间戳转日期
	timeStampConversion : function (timestamp){
		var d = new Date(timestamp);    //根据时间戳生成的时间对象
		var month = (d.getMonth() + 1);
		if(month < 10) month = "0" +"" + month;
		var day = d.getDate();
		if(day < 10) day = "0" +"" + day;
		var date = (d.getFullYear()) + "年" +
			month + "月" +
			day + "日";
		return date;
	},
	//日期转换为时间戳
	getTimeStamp : function (time){
		time=time.replace(/-/g, '/');
		var date=new Date(time);
		return date.getTime();
	},
	//判断是否是json对象
	isJson : function (obj){
		var isjson = typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length;
		return isjson;
	},
	//获取登录人信息
	getUserInfo : function () {
		var userInfo = JSON.parse(localStorage.getItem("userInfo"));
		return userInfo;
	},
	//补十位 相加 得整十
	getForTen : function (number) {
		if (number < 1) { //小于 1 的不做添加
			return number + 10;
		}
		var digit = 0;
		number = number + ""; //转字符串
		var nums = number.split(".");
		switch (nums[0].length) {
			case 2:
				digit = 10;
				break;
			case 3:
				digit = 100;
				break;
			case 4:
				digit = 1000;
				break;
		}
		var num = nums[0].substring(nums[0].length - 1, nums[0].length);
		if (Number(num) + 1 == 10) return Number(number) + digit + 1;
		if (Number(num) + 2 == 10) return Number(number) + digit + 2;
		if (Number(num) + 3 == 10) return Number(number) + digit + 3;
		if (Number(num) + 4 == 10) return Number(number) + digit + 4;
		if (Number(num) + 5 == 10) return Number(number) + digit + 5;
		if (Number(num) + 6 == 10) return Number(number) + digit + 6;
		if (Number(num) + 7 == 10) return Number(number) + digit + 7;
		if (Number(num) + 8 == 10) return Number(number) + digit + 8;
		if (Number(num) + 9 == 10) return Number(number) + digit + 9;
		if (Number(num) + 10 == 10) return Number(number) + digit + 10;
		return Number(number) + digit;
	},
	//ajax请求数据  get/post方式
	ajaxRequestData : function(method,async,requestUrl,arr,token,callback){
		$.ajax({
			type: method,
			async: async,
			url: requestUrl,
			data: arr,
			//dataType:"jsonp",    //跨域json请求一定是jsonp
			headers: {
				"token":token,
			},
			success:function(json){
				console.log(json);
				if (!YZ.isJson(json)) {
					json = JSON.parse(json);
				}
				if(json.flag==0 && json.code ==200){
					if (callback) {
						callback(json);
					}
				}
				else if(json.code == 1005){
					alert(json.msg);
				}
				else {
					alert(json.msg);
				}
			},
			error: function(json) {
				alert(json.responseText);
				console.log("请求出错了.");
			}
		})
	},
};

var YZ = new YZ();

//转时间戳
Date.prototype.format = function (fmt) { //author: meizz
	var o = {
		"M+": this.getMonth() + 1, //月份
		"d+": this.getDate(), //日
		"h+": this.getHours(), //小时
		"m+": this.getMinutes(), //分
		"s+": this.getSeconds(), //秒
		"q+": Math.floor((this.getMonth() + 3) / 3), //季度
		"S": this.getMilliseconds() //毫秒
	};
	if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	for (var k in o)
		if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}
