/**
 * 机构列表
 */
window.renderTable = function(){};
window.currentOrgId = "17";
$(function() {
	// 获取当前登陆的用户信息
	$.ajax({
        type: "GET",
        url: "/promotion/user/getCurrent",
        dataType: "json",
        async: false,
        success: function (jsonResult) {
        	window.currentOrgId = jsonResult.result.org.id;
        	window.level = jsonResult.result.org.level;
        	window.currentOrgCode = jsonResult.result.org.code;
        	window.searchData = { 
        		states: ['3', '4', '5']
        	}
        	if(level > 1) {
        		searchData.orgId = window.currentOrgId;
        	}
        }
    });
	// 加载数据
	function retrieveData(sSource, aoData, fnCallback, oSettings) {
		var draw = (aoData[3].value / 10) + 1;
		oSettings.iDraw = draw;
		// 搜索
		var keyword = aoData[5].value.value;
		searchData.page = (draw - 1);
		searchData.size = 10;
		$.ajax({
            type: "POST",
            url: "/promotion/withdrawalsApply/pages",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(searchData),
            success: function (jsonResult) {
            	var dtData = {
            		"draw": draw,
          			"recordsTotal": jsonResult.result.totalElements,
					"recordsFiltered": jsonResult.result.totalElements,
					"data": jsonResult.result.content
            	};
            	fnCallback(dtData);
            }
        });
		searchData = {
			states: searchData.states
		};
		if(window.level > 1) {
    		searchData.orgId = window.currentOrgId;
    	}
	}
	// 渲染表格
	renderTable = function(id) {
		if($(id + "_wrapper").length > 0) {
			$(id).dataTable().fnDraw();
		} else {
			var columns = [
	            { "data": "id", "title": "申请ID", orderable: false},
	            { "data": "amount", "title": "提现金额", orderable: false},
	            { "data": "applyTime", "title": "申请时间", orderable: false},
	            { "data": "orgName", "title": "申请机构代码/名称", orderable: false, "render": function(data, type, full, meta) {
	            	return full.orgCode + "/" + full.orgName;
	            }},
	            { "data": "state", "title": "状态", orderable: false, "render": function(data, type, full, meta) {
	                var state = full.state;
	                if(state == "TOBEAUDITED") {
	                	return "待审核";
	                } else if(state == "REFUSED") {
	                	return "已拒绝";
	                } else if(state == "PROCESSING") {
	                	return "提现中";
	                } else if(state == "SUCCESS") {
	                	return "提现成功";
	                } else if(state == "FAILURE") {
	                	return "提现失败";
	                } else {
	                	return state;
	                }
	            }}
	        ];
			$(id).dataTable({
				"responsive": true,
		        "processing": true,
		        "serverSide": true,
		        "bPaginate": true,
		        "dom": "<'row'<'col-sm-6'><'col-sm-6'>><'row'<'col-sm-12'tr>><'row'<'col-sm-3'i><'col-sm-9'p>>",
		        "fnServerData": retrieveData,
		        "columns": columns,
		        "oLanguage": {                        //汉化     
	                "sLengthMenu": "每页显示 _MENU_ 条记录",
	                "sSearch": '<span>搜索：</span>',
	                "sZeroRecords": "没有检索到数据",     
	                "sInfo": "当前数据为从第 _START_ 到第 _END_ 条数据；总共有 _TOTAL_ 条记录",
	                "sInfoEmtpy": "没有数据",     
	                "sProcessing": "正在加载数据...",     
	                "oPaginate": {     
	                    "sFirst": "首页",     
	                    "sPrevious": "前页",     
	                    "sNext": "后页",     
	                    "sLast": "尾页"    
	                }     
	            }
		    });
		}
	}
	// 执行
	renderTable("#withdrawals-record-list-table");
	// 加载layui
	layui.use(['element', 'table'], function() {
	});
	// 搜索
	$('#search-btn').on('click', function(){
		var formDataArr = $("#search-form").serializeArray();
		for(var i = 0; i < formDataArr.length; i++) {
			var name = formDataArr[i].name;
			var value = formDataArr[i].value;
			if(searchData[name]) {
				searchData[name] = searchData[name] + "," + value;
			} else {
				searchData[name] = value;
			}
		}
		if(searchData.state == '0') {
			searchData.states = ['3', '4', '5'];
		} else if(searchData.state == '3') {
			searchData.states = ['3'];
		} else if(searchData.state == '4') {
			searchData.states = ['4'];
		} else if(searchData.state == '5') {
			searchData.states = ['5'];
		} else {
			layer.msg("查询参数异常");
			return;
		}
		renderTable("#withdrawals-record-list-table");
	});
});