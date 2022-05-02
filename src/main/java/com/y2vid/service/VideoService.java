package com.y2vid.service;

import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface VideoService {
    String getVideoIdFromLink(String link);

    VideoInfo getVideoInfo(String videoId);

    byte[] downloadVideo(VideoInfo video, String extension, String quality);

    List<Format> getVideoExtensions(VideoInfo videoInfo);
}
