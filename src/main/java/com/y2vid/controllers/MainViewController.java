package com.y2vid.controllers;

import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.y2vid.service.VideoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.Set;

@Controller
public class MainViewController {
    @Autowired
    VideoServiceImpl service;

    @GetMapping
    public String getMainPage() {
        return "Landing";
    }
    @GetMapping("/start")
    public String getStartPage() {
        return "StartPage";
    }

    @GetMapping("/download")
    public ModelAndView getDownloadPage(@RequestParam("link") String link, @RequestParam("types") String type){

        String videoId = service.getVideoIdFromLink(link);
        VideoInfo videoInfo = service.getVideoInfo(videoId);

        Set<String> qualityLabelSet = new HashSet<>();
        Set<String> extensionSet = new HashSet<>();
        if (type.equalsIgnoreCase("video")){
            service.setType(type);
            for(VideoFormat format: videoInfo.videoWithAudioFormats()){
                extensionSet.add(format.extension().value());
                qualityLabelSet.add(format.qualityLabel());
            }
        } else if (type.equalsIgnoreCase("audio")) {
            AudioFormat format = videoInfo.bestAudioFormat();
            service.setType(type);
            extensionSet.add("mp3");
            videoInfo.audioFormats().stream()
                    .filter(i -> i==format)
                    .forEach(i -> qualityLabelSet.add(i.audioSampleRate().toString()));
        }
        return new ModelAndView("DownloadPage")
                .addObject("extensions", extensionSet)
                .addObject("qualityLabels", qualityLabelSet)
                .addObject("link", link)
                .addObject("type", type);
    }
}
