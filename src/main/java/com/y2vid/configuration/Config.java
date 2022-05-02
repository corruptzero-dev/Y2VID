package com.y2vid.configuration;

import com.github.kiulian.downloader.YoutubeDownloader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    YoutubeDownloader youtubeDownloader() {
        return new YoutubeDownloader();
    }
}
