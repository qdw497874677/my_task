package com.qdw.task.domain.task.base;

import lombok.Data;

@Data
public class TaskTextRow {

    private String text;
    private String type;

//    private List<TextOne> textList;
//
//    @Data
//    static public class TextOne {
//        private String text;
//        private String type;
//
//        public TextOne() {}
//
//        public TextOne(String text) {
//            this.text = text;
//        }
//    }
//
//    public TaskTextRow() {
//
//    }

    public TaskTextRow(String text) {
        this.text = text;
//        textList = Collections.singletonList(new TextOne(text));
    }


}
