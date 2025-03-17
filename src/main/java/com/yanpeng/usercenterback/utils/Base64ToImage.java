package com.yanpeng.usercenterback.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

import java.awt.image.BufferedImage;

import java.io.*;


import java.util.Base64;
import java.util.UUID;


import javax.imageio.ImageIO;

 public class Base64ToImage {

     @Value("${spring.profiles.active}")
     private  String env;

     private static final Logger log = LoggerFactory.getLogger(Base64ToImage.class);

     public static String generateImage(String base64) {
         // 解密
         try {
             String savePath = "";


             savePath = "D:\\YupiProjects\\user-center\\user-center-back\\src\\main\\resources\\static\\";


             // 图片分类路径+图片名+图片后缀
             String imgClassPath = UUID.randomUUID().toString().concat(".jpg");
             // 去掉base64前缀 data:image/jpeg;base64,
             base64 = base64.substring(base64.indexOf(",", 1) + 1);
             // 解密，解密的结果是一个byte数组
             Base64.Decoder decoder = Base64.getDecoder();
             byte[] imgbytes = decoder.decode(base64);
             for (int i = 0; i < imgbytes.length; ++i) {
                 if (imgbytes[i] < 0) {
                     imgbytes[i] += 256;
                 }
             }
             // 保存图片
                 OutputStream out = new FileOutputStream(savePath.concat(imgClassPath));
                 out.write(imgbytes);
                 out.flush();
                 out.close();

             return  "http://localhost:8080/api/static/"+imgClassPath;
         } catch (IOException e) {
             log.info("图片保存失败", e);
             return null;
         }
     }


}