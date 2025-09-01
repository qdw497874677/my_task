package com.qdw.task.domain.ai.image;

import lombok.Data;

import java.util.List;

@Data
public class AiImageResponse {


    private String model;

    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private List<Image> images;
    }

    @Data
    public static class Image {
        private ImageUrl imageUrl;
    }

    @Data
    public static class ImageUrl {
        String url;
    }




}
