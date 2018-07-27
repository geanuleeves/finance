package com.waben.stock.applayer.admin.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.waben.stock.applayer.admin.business.UploadBusiness;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.exception.ExceptionMap;
import com.waben.stock.interfaces.exception.ServiceException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/file")
@Api(description = "图片上传")
public class UploadController {

    @Autowired
    private UploadBusiness uploadBusiness;
    
    @Value("${uploadFilePath}")
    private String uploadFilePath;

    @PostMapping("/upload")
    @ResponseBody
    @ApiImplicitParam(paramType = "query", dataType = "MultipartFile", name = "file", value = "文件对象", required = true)
    @ApiOperation(value = "上传图片")
    public String upload(@RequestParam("file") MultipartFile file){
        String resultPath;
        try {
           resultPath =  uploadBusiness.upload(file);
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        }
        return resultPath;
    }
    
    @PostMapping("/uploadPC")
    @ResponseBody
    @ApiImplicitParam(paramType = "query", dataType = "MultipartFile", name = "file", value = "文件对象", required = true)
    @ApiOperation(value = "PC端上传")
    public String uploadPC(@RequestParam("file") MultipartFile file){
        String resultPath;
        try {
           resultPath =  uploadBusiness.uploadPC(file);
        } catch (IOException e) {
            e.printStackTrace();
            return "上传失败";
        }
        return resultPath;
    }
    
    @RequestMapping(value = "/dowload", method = RequestMethod.GET)
	@ApiOperation(value = "导出期货订单信息")
	public void dowload(String downloadLink, HttpServletResponse svrResponse) {
    	String paths[] = downloadLink.split("/");
    	String filename = paths[paths.length-2]+"/"+paths[paths.length-1];
    	File file = new File(uploadFilePath + "/" + filename);
    	
    	if(file.exists()){
    		svrResponse.setContentType("application/force-download");
    		svrResponse.setHeader("Content-Disposition",
					"attachment;filename=\"" + paths[paths.length-1]+ "\"");
 
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
 
            OutputStream os = null; //输出流
            try {
                os = svrResponse.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer);
                    i = bis.read(buffer);
                }
 
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download" + filename);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServiceException(ExceptionConstant.FAILED_TO_DOWLOAD_PICTURES);
            }

    	}else{
    		throw new ServiceException(ExceptionConstant.FILE_ISNOT_FOUND);
    	}
    }

}
