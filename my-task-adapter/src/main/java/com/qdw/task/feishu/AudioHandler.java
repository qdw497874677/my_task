package com.qdw.task.feishu;

public class AudioHandler {

    public String getOrders() {
        StringBuilder sb = new StringBuilder();
        sb.append("语音转文字\n");
        sb.append("audio create[文件]\n");
        sb.append("audio check:[id]\n");
        sb.append("audio result:[id]");
        return sb.toString();
    }

    public String process(String msg) {

        return "";
    }

}
