#!/bin/bash

# 执行git操作
git add .

git commit -m "$(cat <<'EOF'
修复飞书群聊消息路由和AI服务配置

- 修复AI命令在群聊中的响应路由问题
- 添加@Primary注解解决多IAiService实现冲突
- 更新AI命令使用anthropic/claude-3.5-sonnet模型
- 完善OpenRouterAiServiceImpl实现
- 清理过度调试日志，简化命令注册流程
- 添加app.log到.gitignore文件
- 修复测试文件中的依赖引用

🤖 Generated with [Claude Code](https://claude.ai/code)
via [Happy](https://happy.engineering)

Co-Authored-By: Claude <noreply@anthropic.com>
Co-Authored-By: Happy <yesreply@happy.engineering>
EOF
)"

git status