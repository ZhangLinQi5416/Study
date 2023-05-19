package com.xiaofei.reggie.controller;

import com.xiaofei.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传与下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommenController {
    @Value("${reggie.path}")
    private String Path;

    /**
     * 文件上传,MutipartFile名字（file）需与上传名一致
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info("上传文件=====> {}",file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID生成文件名，防止文件名重复
        String FileName = UUID.randomUUID().toString() + suffix;

        //创建目录对象
        File f = new File(Path);
        //判断目录是否存在，若不存在则创建目录
        if(!f.exists()){
            f.mkdirs();
        }

        try {
            file.transferTo(new File(Path + FileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(FileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        log.info("文件下载=====> {}",response.toString());
        try {
            //输入流，获取照片文件
            FileInputStream fileInputStream = new FileInputStream(new File(Path + name));

            //输出流，通过输出流将文件回写到浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置发送到客户端的响应的内容类型
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];

            //如果读完，fileInputStream.read(bytes)将返回-1，所以当不等于-1的时候，就是没有将文件读完
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭流
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
