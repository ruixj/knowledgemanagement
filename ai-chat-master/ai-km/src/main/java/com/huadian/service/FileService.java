package com.huadian.service;


import com.huadian.config.FileUploadProperties;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    @Autowired
    FileUploadProperties fileUploadProperties;

    public String storeFile(MultipartFile uploadFile) throws IOException {
        // Normalize file name
        String fileName = StringUtils.cleanPath(uploadFile.getOriginalFilename());

        ApplicationHome applicationHome = new ApplicationHome(getClass());
        File file = applicationHome.getSource();
        String rootDir = file.getParentFile().toString();

        StringBuilder filePath = new StringBuilder();
        filePath
                .append("/")
                .append(fileUploadProperties.getUploadDir())
                .append("/");
        File f = new File(rootDir + filePath.toString());
        if (!f.exists()) {
            // 如果路径不存在,则创建
            f.mkdirs();
        }
        filePath.append(fileName);

        Files.copy(uploadFile.getInputStream(), Paths.get(rootDir + filePath.toString()), StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();

    }


}
