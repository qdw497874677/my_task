package com.qdw.task.feishu.command;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;

/**
 * 飞书任务命令接口
 * 用于处理飞书消息并执行相应的业务逻辑
 */
public interface FeishuTaskCommand {
    
    /**
     * 获取命令名称
     * @return 命令名称
     */
    String getCommandName();
    
    /**
     * 获取命令描述
     * @return 命令描述
     */
    String getDescription();
    
    /**
     * 获取命令使用示例
     * @return 命令使用示例，如果无参数则返回null
     */
    default String getUsage() {
        return null;
    }
    
    /**
     * 执行命令
     * @param event 飞书消息事件
     * @return 执行结果
     */
    String execute(P2MessageReceiveV1 event);
}
