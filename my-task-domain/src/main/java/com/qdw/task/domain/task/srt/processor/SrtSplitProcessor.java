package com.qdw.task.domain.task.srt.processor;

import com.qdw.task.domain.task.srt.SrtFlowStatus;
import com.qdw.task.domain.task.srt.SrtParser;
import com.qdw.task.domain.task.srt.SrtTask;
import com.qdw.task.domain.task.srt.SrtTaskContext;
import com.qdw.task.domain.task.base.ProcessResult;
import com.qdw.task.domain.task.base.TaskProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SRT文件切分处理器
 *
 * 功能：将SRT文件按100条字幕切分为多个块
 * 输入状态：已创建
 * 输出状态：已切分
 */
@Slf4j
@Service
public class SrtSplitProcessor implements TaskProcessor<SrtFlowStatus, SrtTaskContext, SrtTask> {

    private static final int CHUNK_SIZE = 100;

    @Override
    public ProcessResult<SrtFlowStatus, SrtTaskContext> process(SrtTask task) {
        log.info("开始切分SRT文件，任务ID: {}", task.getTaskId());

        try {
            SrtTaskContext context = task.getContext();
            if (context == null) {
                throw new IllegalArgumentException("任务上下文不能为空");
            }

            // 解析SRT文件
            String content = context.getOriginalContent();
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("SRT文件内容不能为空");
            }

            // 解析字幕条目
            List<SrtParser.SrtEntry> entries = SrtParser.parseSrt(content);
            log.info("解析出 {} 条字幕", entries.size());

            if (entries.isEmpty()) {
                throw new IllegalArgumentException("未能解析出有效的字幕条目");
            }

            // 切分字幕
            List<List<SrtParser.SrtEntry>> chunks = SrtParser.splitIntoChunks(entries, CHUNK_SIZE);
            log.info("切分为 {} 个块", chunks.size());

            // 转换为任务上下文中的块格式
            List<SrtTaskContext.SrtChunk> srtChunks = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                List<SrtParser.SrtEntry> chunk = chunks.get(i);
                SrtTaskContext.SrtChunk srtChunk = new SrtTaskContext.SrtChunk();
                srtChunk.setChunkId(UUID.randomUUID().toString());
                srtChunk.setStartSequence(chunk.get(0).getSequence());
                srtChunk.setEndSequence(chunk.get(chunk.size() - 1).getSequence());
                srtChunk.setContent(SrtParser.toSrtString(chunk));
                srtChunks.add(srtChunk);
            }

            // 更新上下文
            context.setSrtChunks(srtChunks);

            // 更新进度信息
            if (context.getProgressInfo() == null) {
                context.setProgressInfo(new SrtTaskContext.ProgressInfo());
            }
            context.getProgressInfo().setTotalSubtitles(entries.size());
            context.getProgressInfo().setProcessedCount(0);
            context.getProgressInfo().setCurrentPhase("文件切分完成");
            context.getProgressInfo().setCompletionPercentage(10.0); // 切分完成占10%

            log.info("SRT文件切分完成，共 {} 个块", srtChunks.size());

            return new ProcessResult<>(nextStatus(), context);

        } catch (Exception e) {
            log.error("SRT文件切分失败", e);
            SrtTaskContext errorContext = task.getContext();
            if (errorContext != null && errorContext.getProgressInfo() != null) {
                errorContext.getProgressInfo().setCurrentPhase("切分失败: " + e.getMessage());
            }
            return new ProcessResult<>(SrtFlowStatus.异常, task.getContext());
        }
    }

    @Override
    public SrtFlowStatus getStatus() {
        return SrtFlowStatus.已创建;
    }

    @Override
    public SrtFlowStatus nextStatus() {
        return SrtFlowStatus.已切分;
    }
}