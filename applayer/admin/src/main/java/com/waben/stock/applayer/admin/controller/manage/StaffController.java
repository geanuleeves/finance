package com.waben.stock.applayer.admin.controller.manage;

import com.waben.stock.applayer.admin.business.manage.RoleBusiness;
import com.waben.stock.applayer.admin.business.manage.StaffBusiness;
import com.waben.stock.applayer.admin.security.SecurityUtil;
import com.waben.stock.interfaces.dto.manage.RoleDto;
import com.waben.stock.interfaces.dto.manage.StaffDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StaffQuery;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.vo.manage.StaffVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author zengzhiwei
 * @Create 2018/4/23 14:57
 */
@RestController
@RequestMapping("/staff")
public class StaffController {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RoleBusiness roleBusiness;
    @Autowired
    private StaffBusiness staffBusiness;

    @RequestMapping("/index")
    public String user() {
        return "manage/staff/index";
    }

    @RequestMapping("/pages")
    @ApiImplicitParam(paramType = "query", dataType = "StaffQuery", name = "query", value = "查询对象", required = true)
    @ApiOperation(value = "员工分页")
    public Response<PageInfo<StaffDto>> pages(StaffQuery query) {
        PageInfo<StaffDto> response = staffBusiness.staffs(query);
        return new Response<>(response);
    }

    @RequestMapping("/modify")
    @ApiImplicitParam(paramType = "query", dataType = "StaffDto", name = "query", value = "员工对象", required = true)
    @ApiOperation(value = "修改员工")
    public Response<Integer> modify(StaffDto staffDto){
        Integer result = staffBusiness.revision(staffDto);
        return new Response<>(result);
    }

    @RequestMapping("/delete/{id}")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "员工id", required = true)
    @ApiOperation(value = "删除员工")
    public Response<Integer> delete(Long id){
        staffBusiness.delete(id);
        return new Response<>(1);
    }

    @RequestMapping("/save")
    @ApiImplicitParam(paramType = "query", dataType = "StaffDto", name = "query", value = "员工对象", required = true)
    @ApiOperation(value = "添加员工")
    public Response<StaffDto> add(StaffDto staffDto){
        StaffDto response = staffBusiness.save(staffDto);
        return new Response<>(response);
    }

    @PostMapping("/password/{password}")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "password", value = "员工密码", required = true)
    @ApiOperation(value = "修改密码")
    public Response<StaffDto> password(@PathVariable String password) {
       StaffDto staffDto = (StaffDto) SecurityUtil.getUserDetails().getAuthorities();
       staffDto.setPassword(password);
       staffBusiness.modif(staffDto);
       return new Response<>(staffDto);
    }

    @GetMapping("/{id}")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "员工id", required = true)
    @ApiOperation(value = "通过员工id获取员工")
    public Response<StaffDto> fetchById(@PathVariable Long id) {
        StaffDto respone = staffBusiness.findById(id);
        return new Response<>(respone);
    }
}
