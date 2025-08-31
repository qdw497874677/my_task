package com.qdw.task.feishu;

import com.qdw.task.feishu.command.FeishuTaskCommandRegistry;
import com.qdw.task.feishu.command.commands.EchoCommand;
import com.qdw.task.feishu.command.commands.HelpCommand;
import com.qdw.task.feishu.command.commands.StatusCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FeishuDemoTaskCommandTest {
    
    @Autowired
    private FeishuTaskCommandRegistry commandRegistry;
    
    @Autowired
    private HelpCommand helpCommand;
    
    @Autowired
    private EchoCommand echoCommand;
    
    @Autowired
    private StatusCommand statusCommand;
    
    @Test
    public void testCommandRegistration() {
        // 测试命令是否正确注册
        assertNotNull(commandRegistry.getCommand("help"));
        assertNotNull(commandRegistry.getCommand("echo"));
        assertNotNull(commandRegistry.getCommand("status"));
        
        // 测试命令数量
        assertEquals(3, commandRegistry.getAllCommands().size());
        
        System.out.println("Registered commands:");
        commandRegistry.getAllCommands().forEach((name, command) -> 
            System.out.println("- " + command.getCommandName() + ": " + command.getDescription()));
    }
    
    @Test
    public void testHelpCommand() {
        // 测试帮助命令
        String helpMessage = helpCommand.execute(null);
        assertNotNull(helpMessage);
        assertTrue(helpMessage.contains("可用命令列表"));
        assertTrue(helpMessage.contains("help"));
        assertTrue(helpMessage.contains("echo"));
        assertTrue(helpMessage.contains("status"));
    }
    
    @Test
    public void testEchoCommand() {
        // 测试回声命令
        String result = echoCommand.getCommandName();
        assertEquals("echo", result);
        
        result = echoCommand.getDescription();
        assertEquals("将用户输入的文本原样返回", result);
    }
    
    @Test
    public void testStatusCommand() {
        // 测试状态命令
        String result = statusCommand.getCommandName();
        assertEquals("status", result);
        
        result = statusCommand.getDescription();
        assertEquals("显示系统状态信息", result);
    }
}
