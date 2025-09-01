package com.qdw.task.domain.task.summary;

import com.qdw.task.domain.task.base.TaskFileRow;
import lombok.Data;

@Data
public class SummaryTaskContext {

    private TaskFileRow audio;
    private String rawText;
    private String cleanedText;
    private String summary;

}
