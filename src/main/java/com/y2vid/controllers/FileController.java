package com.y2vid.controllers;

import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.y2vid.service.VideoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

@RestController
@Slf4j
public class FileController {
    @Autowired
    private VideoServiceImpl service;

    @PostMapping("api/download")
    public ResponseEntity<Object> getFile(
            @RequestParam("link") String link,
            @RequestParam("formats") String extension,
            @RequestParam("qualities") String quality,
            HttpServletResponse httpServletResponse) throws IOException {
        String videoId = service.getVideoIdFromLink(link);
        VideoInfo videoInfo = service.getVideoInfo(videoId);
        httpServletResponse.setContentType("application/" + extension);
        httpServletResponse.addHeader("Content-Disposition",
                "attachment; filename=" + videoInfo.details().videoId() + "." + extension
        );
        byte[] byteArray = service.downloadVideo(videoInfo, extension, quality);
        httpServletResponse.setContentLength(byteArray.length);
        OutputStream os = httpServletResponse.getOutputStream();
        try {
            os.write(byteArray, 0, byteArray.length);
        } catch (Exception ex) {
            log.error("Exception in FileController: " + ex.getMessage());
        } finally {
            os.close();
        }
        httpServletResponse.flushBuffer();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:8080/")).build();
    }
}
