
function getRootPath()   
{   
	var pathName = window.location.pathname.substring(1);   
	var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'));   
	return window.location.protocol + '//' + window.location.host + '/'+ webName + '/';   
}   

function jsonTimeStamp(milliseconds) {
	if (milliseconds != "" && milliseconds != null
			&& milliseconds != "null") {
		var datetime = new Date();
		datetime.setTime(milliseconds);
		var year = datetime.getFullYear();
		var month = datetime.getMonth() + 1 < 10 ? "0"
				+ (datetime.getMonth() + 1) : datetime.getMonth() + 1;
				var date = datetime.getDate() < 10 ? "0" + datetime.getDate()
						: datetime.getDate();
				var hour = datetime.getHours() < 10 ? "0" + datetime.getHours()
						: datetime.getHours();
				var minute = datetime.getMinutes() < 10 ? "0"
						+ datetime.getMinutes() : datetime.getMinutes();
						var second = datetime.getSeconds() < 10 ? "0"
								+ datetime.getSeconds() : datetime.getSeconds();
								return year + "-" + month + "-" + date + " " + hour + ":" + minute
								+ ":" + second;
	} else {
		return "";
	}
}

function jsonYearMonthDay(milliseconds) {
	var datetime = new Date();
	datetime.setTime(milliseconds);
	var year = datetime.getFullYear();
	var month = datetime.getMonth() + 1 < 10 ? "0"
			+ (datetime.getMonth() + 1) : datetime.getMonth() + 1;
			var date = datetime.getDate() < 10 ? "0" + datetime.getDate()
					: datetime.getDate();
			return year + "-" + month + "-" + date;
}


function HandleException(data){
	if(data.code == 405){
		$.messager.alert("提示消息", data.message,"error");
	}else if(data.code == 401){
		$.messager.alert("提示消息", data.message,"question",function () {
			window.location.href="/login";
		});
	}
}

function loadForSelect(combobox,url,valueField,textField,autoSelect){
	combobox.combobox({  
		method:"POST",
		url:getRootPath() + url,  
		valueField:valueField,  
		textField:textField,
		editable:false,
		loadFilter: function(data){
			console.log("loadForSelect:"+data);
			if (data.code == 200){
				return data.data;
			}else{
				HandleException(data);
			}
		},
		onLoadSuccess: function () { 
			var data = $(this).combobox("getData");
			if(autoSelect){
				if(data.length > 0){
					$(this).combobox("select", data[0][valueField]);
				}
			}
		}
	});  
}
