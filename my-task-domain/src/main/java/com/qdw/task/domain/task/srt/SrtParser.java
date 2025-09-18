package com.qdw.task.domain.task.srt;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SRT文件解析工具类
 */
@Slf4j
public class SrtParser {

    private static final Pattern SRT_ENTRY_PATTERN = Pattern.compile(
            "(\\d+)\\s*(\\d{2}:\\d{2}:\\d{2},\\d{3})\\s*-->\\s*(\\d{2}:\\d{2}:\\d{2},\\d{3})\\s*([\\s\\S]*?)(?=\\n\\n\\d+|$)",
            Pattern.MULTILINE
    );

    /**
     * 解析SRT文件内容
     *
     * @param content SRT文件内容
     * @return 字幕条目列表
     */
    public static List<SrtEntry> parseSrt(String content) {
        List<SrtEntry> entries = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            return entries;
        }

        Matcher matcher = SRT_ENTRY_PATTERN.matcher(content);
        while (matcher.find()) {
            try {
                int sequence = Integer.parseInt(matcher.group(1));
                String startTime = matcher.group(2);
                String endTime = matcher.group(3);
                String text = matcher.group(4).trim();

                SrtEntry entry = new SrtEntry(sequence, startTime, endTime, text);
                entries.add(entry);
            } catch (Exception e) {
                log.warn("Failed to parse SRT entry: {}", matcher.group(), e);
            }
        }

        return entries;
    }

    /**
     * 将字幕条目列表转换为SRT格式字符串
     *
     * @param entries 字幕条目列表
     * @return SRT格式字符串
     */
    public static String toSrtString(List<SrtEntry> entries) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < entries.size(); i++) {
            SrtEntry entry = entries.get(i);
            sb.append(entry.getSequence()).append("\n");
            sb.append(entry.getStartTime()).append(" --> ").append(entry.getEndTime()).append("\n");
            sb.append(entry.getText()).append("\n");

            if (i < entries.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * 将字幕列表切分为指定大小的块
     *
     * @param entries 字幕条目列表
     * @param chunkSize 块大小（条数）
     * @return 切分后的块列表
     */
    public static List<List<SrtEntry>> splitIntoChunks(List<SrtEntry> entries, int chunkSize) {
        List<List<SrtEntry>> chunks = new ArrayList<>();

        for (int i = 0; i < entries.size(); i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, entries.size());
            chunks.add(entries.subList(i, endIndex));
        }

        return chunks;
    }

    /**
     * 合并多个块为一个列表
     *
     * @param chunks 块列表
     * @return 合并后的字幕条目列表
     */
    public static List<SrtEntry> mergeChunks(List<List<SrtEntry>> chunks) {
        List<SrtEntry> merged = new ArrayList<>();

        for (List<SrtEntry> chunk : chunks) {
            merged.addAll(chunk);
        }

        return merged;
    }

    /**
     * SRT字幕条目
     */
    public static class SrtEntry {
        private final int sequence;
        private final String startTime;
        private final String endTime;
        private final String text;

        public SrtEntry(int sequence, String startTime, String endTime, String text) {
            this.sequence = sequence;
            this.startTime = startTime;
            this.endTime = endTime;
            this.text = text;
        }

        public int getSequence() {
            return sequence;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getText() {
            return text;
        }

        /**
         * 创建新的字幕条目（复制时间信息，更新文本）
         */
        public SrtEntry withText(String newText) {
            return new SrtEntry(sequence, startTime, endTime, newText);
        }

        @Override
        public String toString() {
            return String.format("%d\n%s --> %s\n%s", sequence, startTime, endTime, text);
        }
    }
}