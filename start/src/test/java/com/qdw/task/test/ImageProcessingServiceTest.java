package com.qdw.task.test;

import com.qdw.task.domain.image.ImageProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImageProcessingServiceTest {

    @Autowired
    private ImageProcessingService imageProcessingService;

    @Test
    public void testProcessImage() {
        // 这里可以添加测试代码
        // 由于需要API密钥和实际的base64图像数据，我们在实际测试时再填充
        System.out.println("ImageProcessingService测试类已创建");
    }
}