package com.huadian.controller;

import com.huadian.context.UserContext;
import com.huadian.exception.BaseException2;
import com.huadian.messages.ErrorConstant;
import com.huadian.service.FileService;
import com.huadian.util.ErrorUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class AIKnowledgeController {
    @RequestMapping(value = "/api/process",
            method = {
                    RequestMethod.GET,
                    RequestMethod.DELETE,
                    RequestMethod.POST
            }
    )
    public ModelMap process(@RequestHeader(name = "action") String action,
                            @RequestBody(required = false) Map map,
                            @RequestParam(required = false) Map getMap,
                            HttpServletRequest request
    ) {

        String method =  request.getMethod();
        String userId = UserContext.getUserId();
        if(method.equalsIgnoreCase("post")) {

            Object obj = null;

            if (action.equals("createKnowledge")) {

                String name          = (String) map.get("name");
                String description   = (String) map.get("description");
                map.put("created_by",userId);


                ModelMap ret = ErrorUtil.retErrMsg(ErrorConstant.SUCCESS, obj);

                return ret;
            }
            else {
                throw new BaseException2("invalid.action.error", new Object[]{action});
            }
        }else if(method.equalsIgnoreCase("get")) {

            Object obj = null;

            if (action.equals("workflow:getFlowStatus")) {
                ModelMap ret = ErrorUtil.retErrMsg(ErrorConstant.SUCCESS, obj);

                return ret;
            }else{
                throw new BaseException2("invalid.action.error", new Object[]{action});
            }
        }
        throw new BaseException2("invalid.action.error", new Object[]{action});
    }
    @Autowired
    FileService fileService;

    private static final String UPLOAD_DIR = "uploads/";
    //@RequestParam 是获取 For HTTP servlets, parameters are contained in the query string or posted form data.
    //具体可看 org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver
    //org.springframework.web.method.annotation.RequestParamMethodArgumentResolver
    //url中的?后面添加参数即可用，form-data、x-www-form-urlencoded时候可用
    @RequestMapping(value = "/api/process/file",
            method = RequestMethod.POST
    )
    public Map genericProcessorWithFile(HttpServletRequest request,
                                        @RequestParam Map map
    ) {
        Map result = new HashMap();
        ModelMap ret = ErrorUtil.retErrMsg(ErrorConstant.SUCCESS, result);
        if (isMultipartRequest(request))
        {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultiValueMap<String, MultipartFile> multiFileMap = multipartHttpServletRequest.getMultiFileMap();

            for (Map.Entry<String, List<MultipartFile>> each : multiFileMap.entrySet()) {
                String fileName = each.getKey();
                List<MultipartFile> files = each.getValue();

                try {
                    for (MultipartFile file: files){
                        String serverPath = fileService.storeFile(file);
                        result.put(file.getOriginalFilename(), serverPath);
                    }
                } catch (Exception ex) {
                    throw new BaseException2(ErrorConstant.FAILED_TO_SAVEFILE, ex, new String[]{fileName});
                }
            }
        }
        return ret;
    }

    // 判断请求是否为multipart/form-data类型
    private boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith("multipart/");
    }



}
