package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.CommonUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.util.FileUpload;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;


/**
 * Created by flyong86 on 2016/5/6.
 */
@Controller
@RequestMapping("/res")
public class ResourceController extends BaseController {

    @RequestMapping("/upload")
    @ResponseBody
    public ViewData upload(@RequestParam(value = "type" ,defaultValue = "other") String type, HttpServletRequest request){

        Calendar ca = Calendar.getInstance(); 
        if (request instanceof MultipartHttpServletRequest) {
            String url = "";
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> multipartFileMap = multipartHttpServletRequest.getFileMap();
            if (multipartFileMap != null) {
                for (Map.Entry<String, MultipartFile> entry : multipartFileMap.entrySet()) {
                    MultipartFile multipartFile = entry.getValue();
//                    String filename = multipartFile.getOriginalFilename();
                    String filePath = "upload/" + ca.get(Calendar.YEAR) + "/" + ca.get(Calendar.MONTH) + "/" + ca.get(Calendar.DAY_OF_MONTH) + "/";
                    String resName = FileUpload.fileUp(multipartFile, filePath, CommonUtil.get32UUID());

                    StringBuffer picURL = new StringBuffer();
//                    picURL.append(request.getScheme() + "://");
//                    picURL.append(request.getServerName() + ":");
//                    picURL.append(request.getServerPort() + "");
//                    picURL.append(request.getContextPath() + "/");
                    picURL.append(filePath + resName);
                    url = picURL.toString();
					if (null != type && type.trim().length() > 0 && "1".equals(type)) {
						String path = System.getProperty("user.dir").replaceAll("bin","webapps");
						String iconPath = path + "/" + filePath + "/icon";
						File file = new File(iconPath);
						if (!file.isDirectory()) {
							file.mkdir();
						}
						//压缩图片
//						ImgCompress.reduceImg(path+"/"+url,iconPath+"/"+resName,32,32,null);
					}
                }
            }
            Map<String,Object> data = new HashMap<String, Object>();
            data.put("url", url);
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"上传成功", data);
        }
            return buildFailureJson(ViewData.FlagEnum.ERROR, 202,"上传失败");

    }
    
}
