package com.qdw.task.api.ai;

public interface AiImageProcessService {

    /**
     * 输入图片地址，修改图片，将结果写入飞书云盘，返回文件地址
     * @param imageUrl
     * @param prompt
     * @return
     */
    String processImage(String imageUrl, String prompt);

}
