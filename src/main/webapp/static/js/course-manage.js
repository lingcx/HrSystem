/**
 * 
 */

$(function(){
	var datagrid; //定义全局变量datagrid
	var editRow = undefined; //定义全局变量：当前编辑的行
	datagrid = $("#data-table").datagrid({
		dnd: true,
		method:"POST",
		url:getRootPath() + "admin/course/select",
		idField:'conId',
		rownumbers: true,
		checkOnSelect : true,  
		width: $(window).width() - 6,
		height: $(window).height() - 110,
		striped: true, //行背景交换
		fitColumns:false,
		singleSelect:true,
		pagination:true,//分页控件 
		pageSize: 20,//每页显示的记录条数，默认为10 
		pageList: [10,20,50,100],//可以设置每页记录条数的列表 
		loadFilter: function(data){
			console.log(data);
			if (data.code == 200){
				return data.data;
			}else{
				HandleException(data);
			}
		},
		loadFilter: function(data){
			console.log(data);
			if (data.code == 200){
				return data.data;
			}else{
				HandleException(data);
			}
		},
		columns:[[
			{
				field:'couId',
				title:"编号",
				width:100,
				align:'center'
			},{
				field:'couName',
				title:"课程名称",
				width:150,
				align:'center',
			},{
				field:'couSponsor',
				title:"主办单位",
				width:150,
				align:'center',
			},{
				field:'mTrainMode',
				title:"培训方式",
				width:150,
				align:'center',
				formatter:function(val){  
					if(val){  
						return val.dictName; 
					}  
				},  
			},{
				field:'couLearner',
				title:"授课对象",
				width:100,
				align:'center',
			},{
				field:'mTeachMode',
				title:"授课方式",
				width:150,
				align:'center',
				formatter:function(val){  
					if(val){  
						return val.dictName; 
					}  
				},  
			},{
				field:'couTeachinghours',
				title:"课时数",
				width:100,
				align:'center',
			},{
				field:'couFee',
				title:"课时费用",
				width:100,
				align:'center',
			},{
				field:'couStarttime',
				title:"开课时间",
				width:100,
				align:'center',
				formatter:function(val){  
					return jsonYearMonthDay(val);  
				}
			},{
				field:'couEndtime',
				title:"结课时间",
				width:100,
				align:'center',
				formatter:function(val){  
					return jsonYearMonthDay(val);  
				}
			}
			]],
			toolbar:'#toolbar',
			onLoadSuccess: function(row){
				datagrid.datagrid("unselectAll");
			}
	});
	
	//############################	搜索开始	###############################
	$("#btnSearch").click(function(){
		console.log("search");
		doSearch();
	});

	function doSearch(){
		var type = $("#search-type").val();
		if(type == 0){
			$('#data-table').datagrid('load',{
				stype: 0,
				skey: $('#search-name').val()
			});
		}
	}
	
	//############################	搜索结束	###############################

	//###########################	新增开始	############################

	$("#add").click(function(){
		loadForSelect($('#train-type-combox'),"admin/selectType/TrainingMode","dictId","dictName",true);
		loadForSelect($('#teach-type-combox'),"admin/selectType/TeachingMode","dictId","dictName",true);
		$('#add-box').dialog("open");
		$("#add-form").form("disableValidation");
	});

	$('#add-box').dialog({
		title: '培训课程信息新增',
		width: 400,
		height: 500,
		closed: true,
		cache: false,
		modal: true,
		buttons:[{
			text:'保存',
			iconCls:'icon-ok',
			handler:function(){
				formAddSubmit();
			}
		},{
			text:'取消',
			iconCls:'icon-cancel',
			handler:function(){
				$('#add-box').dialog("close");
			}
		}]
	});


	function formAddSubmit(){
		$('#add-form').form('submit', {
			url:getRootPath() + 'admin/course/insert',
			onSubmit: function(){
				return $(this).form('enableValidation').form('validate');
			},
			success:function(data){
				$('#add-box').dialog("close");
				$('#add-form').form("clear");
				datagrid.datagrid("reload");
			}
		});
	}

	//###########################	新增结束	############################
	
	//###########################	编辑开始	############################

	$("#edit").click(function(){
		articleEdit();
	});

	function articleEdit(){
		var selectRows =datagrid.treegrid("getSelections");
		if (selectRows.length < 1) {
			$.messager.alert("提示消息", "请选择要编辑的培训课程信息!");
			return;
		}else if(selectRows.length > 1){
			$.messager.alert("提示消息", "只能选择一条的记录!");
			return;
		}else{
			loadForSelect($('#edit-train-type-combox'),"admin/selectType/TrainingMode","dictId","dictName",false);
			loadForSelect($('#edit-teach-type-combox'),"admin/selectType/TeachingMode","dictId","dictName",false);
			$.ajax({
				url: getRootPath() + "admin/course/select/"+selectRows[0].couId,
				type: "post",
				dataType: "json",
				success: function (data) {
					if(data.code == 200){
						var dict = data.data;
						console.log(dict);
						$("#edit-couId").val(dict.couId);
						$("#edit-couName").textbox('setValue',dict.couName);
						$("#edit-couSponsor").textbox("setValue", dict.couSponsor);
						$("#edit-couLearner").textbox('setValue',dict.couLearner);
						$("#edit-couTeachinghours").textbox("setValue", dict.couTeachinghours);
						$("#edit-couFee").textbox('setValue',dict.couFee);
						
						$("#edit-train-type-combox").combobox("select", dict.couTrainingmethods);
						$("#edit-teach-type-combox").combobox("select", dict.couTeachingmethods);
						
						$('#edit-couStarttime').datebox('setValue',jsonYearMonthDay(dict.couStarttime));
						$('#edit-couEndtime').datebox('setValue',jsonYearMonthDay(dict.couEndtime));
						
						$("#edit-form").form("disableValidation");
						$('#edit-box').dialog("open");
					}else{
						HandleException(data);
					}
				}
			});

		}
	}

	$('#edit-box').dialog({
		title: '培训课程编辑',
		width: 400,
		height: 400,
		closed: true,
		cache: false,
		modal: true,
		buttons:[{
			text:'提交',
			iconCls:'icon-ok',
			handler:function(){
				formEditSubmit();
			}
		},{
			text:'取消',
			iconCls:'icon-cancel',
			handler:function(){
				$('#edit-box').dialog("close");
			}
		}]
	});

	function formEditSubmit(){
		$('#edit-form').form('submit', {
			url:getRootPath() + 'admin/course/update',
			onSubmit: function(){
				return $(this).form('enableValidation').form('validate');
			},
			success:function(data){
				$("#edit-form").form('clear');
				$('#edit-box').dialog("close");
				datagrid.datagrid("reload");
			},
			error:function(){
				alert("error");
			}
		});
	}

	//###########################	编辑结束	############################

	//###########################	删除开始	############################

	$("#delete").click(function(){
		doDelete();
	});

	//删除数据
	function doDelete() {
		var selectRows =datagrid.treegrid("getSelections");
		if (selectRows.length < 1) {
			$.messager.alert("提示消息", "请选择要删除的培训课程!");
			return;
		}
		//提醒用户是否是真的删除数据
		$.messager.confirm("确认消息", "您确定要删除培训课程【"+selectRows[0].couName+"】吗？", function (r) {
			if (r) {
				MaskUtil.mask();
				$.ajax({
					url: getRootPath() + "admin/course/delete/"+selectRows[0].couId,
					type: "post",
					dataType: "json",
					success: function (data) {
						MaskUtil.unmask();
						if(data.code == 200){
							datagrid.datagrid("reload");
							datagrid.datagrid("clearSelections");
						}else{
							HandleException(data);
						}
					}
				});
			}
		});
	}

	//###########################	删除结束	############################

	//###########################	加载类别开始	############################

	function loadType(combobox,type){
		combobox.combobox({  
			method:"POST",
			url:getRootPath() + 'admin/dictionaryType/selectAll',  
			valueField:'dtId',  
			textField:'dtName',
			editable:false,
			loadFilter: function(data){
				if (data.code == 200){
					return data.data;
				}else{
					HandleException(data);
				}
			},
			onLoadSuccess: function () { 
				var data = $(this).combobox("getData");
				if(type == 1){
					if(data.length > 0){
						$(this).combobox("select", data[0].dtId);
					}
				}
			}
		});  
	}
	//###########################	加载类别结束	############################
	
	function getType(combobox,code){
		combobox.combobox({  
			method:"POST",
			url:getRootPath() + 'admin/selectType/'+code,  
			valueField:'dictId',  
			textField:'dictName',
			editable:false,
			loadFilter: function(data){
				if (data.code == 200){
					return data.data;
				}else{
					HandleException(data);
				}
			},
			onLoadSuccess: function () { 
				var data = $(this).combobox("getData");
				console.log("combobox:"+data);
				if(data.length > 0){
					$(this).combobox("select", data[0].dictId);
				}
			}
		});  
	}
});