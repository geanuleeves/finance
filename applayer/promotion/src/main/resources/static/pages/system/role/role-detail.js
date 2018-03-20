/**
 * 添加角色
 */
$(function() {
    var roleId = parent.currentRoleId;

	// 加载layui
	layui.use(['element', 'table', 'form'], function() {});

	//初始化权限信息
    $.ajax({
        type: "GET",
        url: "/promotion/role/"+roleId,
        dataType: "json",
        success: function (jsonResult) {
            if("200" == jsonResult.code) {
                var permissions = jsonResult.result.permissionVos;
                var html = '';
                $.each(permissions,function (index,permission){
                    if(permission.pid==0) {
                        html += '<span>'+permission.name+'</span><br>&nbsp;&nbsp;&nbsp;&nbsp;';
                        $.each(permissions,function (index,cpermission){
                            if(cpermission.pid==permission.id) {
                                html += '<span>'+cpermission.name+'  <input value='+cpermission.id+' type="checkbox" name="permission" disabled="disabled"/></span>&nbsp;&nbsp;&nbsp;&nbsp;';
                            }
                        })
                        html += "<br>";
                    }
                });
                $("#permissions").append(html);
            } else {
                parent.layer.msg(jsonResult.message);
            }
        },
        error: function (jsonResult) {
            parent.layer.msg(jsonResult.responseJSON.message)
        }
    });
});