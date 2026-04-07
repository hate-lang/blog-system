package com.langtian.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import jakarta.annotation.Nonnull;

import java.io.InputStream;

public class AliOssUtil {

    // 1. 换成你的 Endpoint (在阿里云 OSS 控制台的 Bucket 概览里找，比如：oss-cn-hangzhou.aliyuncs.com)
    private static final String ENDPOINT = "oss-cn-beijing.aliyuncs.com";

    // 2. 换成你刚才建的 Bucket 名字
    private static final String BUCKET_NAME = "my-blog-info";

    @Nonnull
    public static String uploadFile(String objectName, InputStream inputStream) {
        // 大厂级安全做法：从你的电脑环境变量中自动读取 AK 和 SK！
        // 前提是你必须保证环境变量的名字叫 OSS_ACCESS_KEY_ID 和 OSS_ACCESS_KEY_SECRET
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");

        // 建立和阿里云服务器的连接
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, accessKeyId, accessKeySecret);

        try {
            // 核心：把文件流传到云端！
            ossClient.putObject(BUCKET_NAME, objectName, inputStream);

            // 拼接出这张图片的公网访问网址 (公式：https://bucket名字.endpoint/文件名)
            String url = "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + objectName;

            return url;
        } finally {
            // 养成好习惯：无论成功失败，一定要关掉连接，释放资源
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}
