package com.waben.stock.applayer.admin.business.manage;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.admin.security.CustomUserDetails;
import com.waben.stock.applayer.admin.security.SecurityUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.manage.StaffDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StaffQuery;
import com.waben.stock.interfaces.service.manage.StaffInterface;
import com.waben.stock.interfaces.util.PasswordCrypt;

/**
 * 后台管理用户 Business
 * 
 * @author lma
 */
@Service
public class StaffBusiness {

	@Autowired
	@Qualifier("staffInterface")
	private StaffInterface reference;

	public StaffDto fetchByUserName(String username) {
		Response<StaffDto> response = reference.fetchByUserName(username);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<StaffDto> staffs(StaffQuery staffQuery) {
		Response<PageInfo<StaffDto>> response = reference.pagesByQuery(staffQuery);
		String code = response.getCode();
		if ("200".equals(code)) {
			return response.getResult();
		}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}

	public StaffDto findById(Long id) {
		Response<StaffDto> response = reference.fetchById(id);
		String code = response.getCode();
		if ("200".equals(code)) {
			return response.getResult();
		}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}

	public StaffDto revision(StaffDto requestDto) {
		requestDto.setPassword(PasswordCrypt.crypt(requestDto.getPassword()));
		if(requestDto.getRoleId()!=null){
			Response<StaffDto> response = reference.modify(requestDto);
			String code = response.getCode();
			if ("200".equals(code)) {
				return response.getResult();
			}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
				throw new NetflixCircuitException(code);
			}
			throw new ServiceException(response.getCode());
		}
		throw new ServiceException("1001");
	}

	public void delete(Long id) {
		reference.delete(id);
	}

	public StaffDto save(StaffDto requestDto) {
		requestDto.setPassword(PasswordCrypt.crypt(requestDto.getPassword()));
		Response<StaffDto> response = reference.saveStaff(requestDto);
		String code = response.getCode();
		if ("200".equals(code)) {
			return response.getResult();
		}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}

	public StaffDto modif(String originalPassword,String password) {
		CustomUserDetails userDetails = SecurityUtil.getUserDetails();
		StaffDto staffDto = reference.fetchById(userDetails.getUserId()).getResult();
		if(PasswordCrypt.match(originalPassword,staffDto.getPassword())) {
			staffDto.setPassword(PasswordCrypt.crypt(password));
			staffDto.setRoleId(staffDto.getRoleDto().getId());
			staffDto.setUpdateTime(new Date());
			Response<StaffDto> response = reference.saveStaff(staffDto);
			String code = response.getCode();
			if ("200".equals(code)) {
				return response.getResult();
			}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
				throw new NetflixCircuitException(code);
			}
		}
		throw new ServiceException(ExceptionConstant.ORIGINAL_PASSWORD_MISMATCH_EXCEPTION);
	}
	
	public List<StaffDto> findAll(){
		Response<List<StaffDto>> response = reference.findAll();
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
}
