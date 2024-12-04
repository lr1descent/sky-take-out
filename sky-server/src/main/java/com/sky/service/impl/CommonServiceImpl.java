package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.service.CommonService;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @Override
    public Result<String> upload(MultipartFile file) {
        // 此处可以进行测试...
        log.info("文件上传：{}", file);

        // 调用AliOssUtil中的upload方法，将file文件上传至阿里云

        // 获取文件初始名，通过切片获取文件后缀名
        String originalFilename = file.getOriginalFilename();

        // 获取文件后缀名，例如.png
        String extension =
                originalFilename.substring(originalFilename.lastIndexOf("."));

        // 获取UID，构造上传文件的名称
        String picName = UUID.randomUUID().toString() + extension;

        try {
            String filePath = aliOssUtil.upload(file.getBytes(), picName);
            System.out.println("filePath is " + filePath);

            return Result.success(filePath);
        } catch (IOException e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
