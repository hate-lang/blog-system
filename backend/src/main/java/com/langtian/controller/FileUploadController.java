package com.langtian.controller;

import com.langtian.pojo.Result;
import com.langtian.utils.AliOssUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {

        try {
            // 1. 拿后缀名 (比如 .jpg)
            String originalFilename = file.getOriginalFilename();

            // 2. 生成全球唯一的 UUID 新名字
            if (file.isEmpty()){
                return Result.error("null");
            }
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + ext;

            // 返回的就是一个全网可以直接访问的 https://... 的网址！
            String url = AliOssUtil.uploadFile(newFileName, file.getInputStream());

            // 4. 把这个高大上的网址返回给前端
            return Result.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传云端失败，请检查配置!");
        }

    }
}
