package com.qdw.task.domain.task.srt;

import lombok.Data;

import java.util.List;

@Data
public class SrtTaskContext {

    /**
     * 原始SRT文件内容
     */
    private String originalContent;

    /**
     * SRT文件URL或路径
     */
    private String srtFileUrl;

    /**
     * 切分后的字幕块列表
     */
    private List<SrtChunk> srtChunks;

    /**
     * 校准后的英文字幕内容
     */
    private String calibratedContent;

    /**
     * 翻译后的中英双语内容
     */
    private String translatedContent;

    /**
     * 处理进度信息
     */
    private ProgressInfo progressInfo;

    /**
     * 字幕块信息
     */
    @Data
    public static class SrtChunk {
        /**
         * 块ID
         */
        private String chunkId;

        /**
         * 起始序号
         */
        private int startSequence;

        /**
         * 结束序号
         */
        private int endSequence;

        /**
         * 原始内容
         */
        private String content;

        /**
         * 校准后的内容
         */
        private String calibratedContent;

        /**
         * 翻译后的内容
         */
        private String translatedContent;
    }

    /**
     * 进度信息
     */
    @Data
    public static class ProgressInfo {
        /**
         * 总字幕条数
         */
        private int totalSubtitles;

        /**
         * 已处理条数
         */
        private int processedCount;

        /**
         * 当前处理阶段
         */
        private String currentPhase;

        /**
         * 完成百分比
         */
        private double completionPercentage;
    }
}