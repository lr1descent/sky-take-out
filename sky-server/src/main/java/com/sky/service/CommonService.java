package com.sky.service;


import com.sky.result.Result;
import org.springframework.web.multipart.MultipartFile;

public interface CommonService {
    /**
     * 上传文件
     * @param file
     * @return
     */
    Result<String> upload(MultipartFile file);
}
