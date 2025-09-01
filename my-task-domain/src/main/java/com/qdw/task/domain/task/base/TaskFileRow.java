package com.qdw.task.domain.task.base;

import lombok.Data;

@Data
public class TaskFileRow {
    private String name;
    private double size;
    private String url;
    private String type;
}
