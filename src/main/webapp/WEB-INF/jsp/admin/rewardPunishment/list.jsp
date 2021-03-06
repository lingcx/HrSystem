<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@	taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<base href="${pageContext.request.contextPath }/">
<meta charset="UTF-8" />
<title>数字办公系统</title>
<jsp:include page="../include_header.jsp"></jsp:include>
</head>
<body class="easyui-layout" fit="true" border="false">

	<div data-options="region:'north'" style="height: 100px;">
		<fieldset style="border-radius: 10px; border: 1px solid #C3C3C3;">
			<legend style="font-size: 14px;">信息检索</legend>
			<form id="article-search-form" method="post">
				<table cellspacing="0" cellpadding="0">
					<tr>
						<td class="td_name">员工姓名：<input class="easyui-combobox"
						editable="false" id="search-type-combox" style="width: 200px;"/></td>
						<td colspan="2" style="padding-left: 20px;"><a
							href="javascript:;" class="easyui-linkbutton"
							data-options="iconCls:'icon-search'" id="btnSearch">查询</a></td>
					</tr>
				</table>
			</form>
		</fieldset>
	</div>

	<div id="toolbar">
	<shiro:hasPermission name="system:rewardPunishment:insert">
			<a href="javascript:;" class="easyui-linkbutton"
				iconCls="icon-application-form-add" plain="true" id="add">新增</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="system:rewardPunishment:update">
			<a href="javascript:;" class="easyui-linkbutton"
				iconCls="icon-application-form-edit" plain="true" id="edit">编辑</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="system:rewardPunishment:delete">
			<a href="javascript:;" class="easyui-linkbutton"
				iconCls="icon-application-form-delete" plain="true" id="delete">删除</a>
		</shiro:hasPermission>
	</div>
	<div data-options="region:'center',split:false">
		<table id="data-table"></table>
	</div>
	<!-- 新增表单 -->
	<div class="box" id="add-box" style="display: none;">
		<form id="add-form" method="post">
			<table class="rb-org" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="td_key">月份：</td>
					<td class="td_val"><input class="easyui-datebox datetime1"
							editable="false" style="width: 204px;" id="edit-apMonth"
							 name="apMonth"></td>
				</tr>
				<tr>
					<td class="td_key">奖惩员工：</td>
					<td class="td_val"><input class="easyui-combobox"
						editable="false" id="add-type-combox" style="width: 200px;"
						name="apEmId" /></td>
				</tr>
				<tr>
					<td class="td_key">奖惩项目：</td>
					<td class="td_val"><input class="easyui-combobox"
						editable="false" id="ap-type-combox" style="width: 200px;"
						name="apProject" /></td>
				</tr>
				<tr>
					<td class="td_key">奖惩金额：</td>
					<td class="td_val"><input class="easyui-textbox" type="text"
						name="alAllowance" data-options="required:true"
						missingMessage="请填写奖惩金额" /></td>
				</tr>
				<tr>
					<td class="td_key">描述：</td>
					<td class="td_val"><input class="easyui-textbox"
						data-options="multiline:true" type="text" name="apDescription"
						style="width: 200px; height: 100px" /></td>
				</tr>
			</table>
		</form>
	</div>
	
	<!-- 编辑表单 -->
	<div class="box" id="edit-box" style="display: none;">
		<form id="edit-form" method="post">
			<table class="rb-org" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td class="td_key">月份：</td>
					<td class="td_val">
					<input type="hidden" name="apId" id="edit-apId"/>
					<span class="edit-apMonth"></span></td>
				</tr>
				<tr>
					<td class="td_key">奖惩员工：</td>
					<td class="td_val"><input class="easyui-combobox"
						editable="false" id="edit-em-combox" style="width: 200px;"
						name="apEmId" /></td>
				</tr>
				<tr>
					<td class="td_key">奖惩项目：</td>
					<td class="td_val"><input class="easyui-combobox"
						editable="false" id="edit-ap-type-combox" style="width: 200px;"
						name="apProject" /></td>
				</tr>
				<tr>
					<td class="td_key">奖惩金额：</td>
					<td class="td_val"><input class="easyui-textbox" type="text"
						name="alAllowance" data-options="required:true" id="edit-alAllowance"
						missingMessage="请填写奖惩金额" /></td>
				</tr>
				<tr>
					<td class="td_key">描述：</td>
					<td class="td_val"><input class="easyui-textbox"
						data-options="multiline:true" type="text" name="apDescription" id="edit-apDescription"
						style="width: 200px; height: 100px" /></td>
				</tr>
			</table>
		</form>
	</div>

	<script type="text/javascript" src="static/easyui/easy-date-yearmonth.js"></script>
	<script type="text/javascript" src="static/js/rewardPunishment-manage.js"></script>
</body>
</html>