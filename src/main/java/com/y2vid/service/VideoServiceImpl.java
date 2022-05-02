package com.y2vid.service;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.request.RequestVideoStreamDownload;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@Getter
@Setter
public class VideoServiceImpl implements VideoService {
    @Autowired
    private YoutubeDownloader downloader;

    private String type;



    @Override
    public String getVideoIdFromLink(String link) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed/)[^#&?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(link);
        if(matcher.find()){
            log.info("Got videoID: " + matcher.group());
            return matcher.group();
        } else {
            log.error("Video ID is empty!");
            return "";
        }
    }

    @Override
    public VideoInfo getVideoInfo(String videoId) {
        RequestVideoInfo requestVideoInfo = new RequestVideoInfo(videoId)
                .callback(new YoutubeCallback<>() {
                    @Override
                    public void onFinished(VideoInfo data) {
                        log.info("Finished parsing");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.error("Error: " + throwable.getMessage());
                    }
                })
                .async();
        Response<VideoInfo> videoInfoResponse = downloader.getVideoInfo(requestVideoInfo);
        return videoInfoResponse.data();
    }

    @Override
    public List<Format> getVideoExtensions(VideoInfo videoInfo) {
        return videoInfo.formats();
    }

    public Format getFormat(VideoInfo video, String extension, String quality){
        Optional<? extends Format> format = Optional.empty();
        if (type.equalsIgnoreCase("video")){
            format = video.videoWithAudioFormats().stream().filter(i -> Objects.equals(i.qualityLabel(), quality)).findAny();
        } else if (type.equalsIgnoreCase("audio")){
            format = video.audioFormats().stream().filter(
                    i -> Objects.equals(i.audioSampleRate().toString(), quality)
            ).findAny();
        }
        if(format.isPresent()) {
            return format.get();
        } else {
            log.error("No format is present for extension: " + extension + " and quality: " + quality);
            throw new RuntimeException("No format exception");
        }
    }

    @Override
    public byte[] downloadVideo(VideoInfo video, String extension, String quality) {
        try {
            Format format = getFormat(video, extension, quality);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            RequestVideoStreamDownload request = new RequestVideoStreamDownload(format, os);
            Response<Void> response = downloader.downloadVideoStream(request);
            log.info("Download response status: " + response.status().toString());
            return os.toByteArray();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new byte[0];
        }
    }
}
