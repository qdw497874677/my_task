package com.qdw.task.feishu.command.commands;

import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.qdw.task.common.utils.FeishuMessageUtils;
import com.qdw.task.feishu.command.FeishuTaskCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 恶魔轮盘游戏命令
 * 实现恶魔轮盘派对游戏功能
 */
@Slf4j
@Component
public class DemonRouletteCommand implements FeishuTaskCommand {

    // 游戏状态管理
    private final Map<String, GameState> gameStates = new ConcurrentHashMap<>();

    @Override
    public String getCommandName() {
        return "demon";
    }

    @Override
    public String getDescription() {
        return "恶魔轮盘游戏 - 体验刺激的俄罗斯轮盘赌游戏";
    }

    @Override
    public String getUsage() {
        return "demon [start|shoot|end|status] [子弹数量]";
    }

    @Override
    public boolean isInstantResponseEnabled() {
        return true;
    }

    @Override
    public String getInstantResponseMessage() {
        return "🎯 正在处理恶魔轮盘游戏...";
    }

    @Override
    public String execute(P2MessageReceiveV1 event) {
        try {
            String content = event.getEvent().getMessage().getContent();
            String commandText = extractCommandText(content);

            if (commandText == null || commandText.isEmpty()) {
                return showGameHelp();
            }

            String[] parts = commandText.split("\\s+");
            String action = parts.length > 1 ? parts[1].toLowerCase() : "help";

            String chatId = event.getEvent().getMessage().getChatId();
            String senderId = event.getEvent().getSender().getSenderId().getOpenId();

            switch (action) {
                case "start":
                    return startGame(chatId, senderId, parts);
                case "shoot":
                    return shoot(chatId, senderId);
                case "end":
                    return endGame(chatId);
                case "status":
                    return getGameStatus(chatId);
                case "help":
                default:
                    return showGameHelp();
            }
        } catch (Exception e) {
            log.error("Error executing demon roulette command", e);
            return "游戏执行出错: " + e.getMessage();
        }
    }

    private String extractCommandText(String content) {
        try {
            String textContent = FeishuMessageUtils.parseMessageContent(content);
            // 过滤掉飞书@机器人的标识（如@_user_1）
            textContent = textContent.replaceAll("@_user_\\d+", "").trim();
            return textContent;
        } catch (Exception e) {
            log.error("Error extracting command text", e);
            return "";
        }
    }

    private String startGame(String chatId, String senderId, String[] parts) {
        int bulletCount = 1; // 默认1发子弹

        if (parts.length > 2) {
            try {
                bulletCount = Integer.parseInt(parts[2]);
                if (bulletCount < 1 || bulletCount > 6) {
                    return "🔫 子弹数量必须在1-6之间！";
                }
            } catch (NumberFormatException e) {
                return "🔫 子弹数量必须是数字！";
            }
        }

        GameState gameState = new GameState(senderId, bulletCount);
        gameStates.put(chatId, gameState);

        return buildGameStartMessage(gameState, bulletCount);
    }

    private String shoot(String chatId, String senderId) {
        GameState gameState = gameStates.get(chatId);
        if (gameState == null) {
            return "🔫 没有正在进行的游戏！请先输入 'demon start' 开始新游戏。";
        }

        if (gameState.isGameOver()) {
            return "💀 游戏已结束！请输入 'demon start' 开始新游戏。";
        }

        // 执行射击
        boolean isHit = gameState.shoot();

        if (isHit) {
            gameStates.remove(chatId);
            return buildGameOverMessage(senderId, gameState);
        } else {
            return buildShootResultMessage(gameState);
        }
    }

    private String endGame(String chatId) {
        GameState gameState = gameStates.remove(chatId);
        if (gameState == null) {
            return "🔫 没有正在进行的游戏。";
        }
        return "🎮 游戏已结束。最终得分: " + gameState.getScore();
    }

    private String getGameStatus(String chatId) {
        GameState gameState = gameStates.get(chatId);
        if (gameState == null) {
            return "🔫 没有正在进行的游戏。";
        }
        return buildGameStatusMessage(gameState);
    }

    private String showGameHelp() {
        return "🎯 **恶魔轮盘游戏规则** 🎯\n\n" +
               "🔫 **基本规则**:\n" +
               "- 6个弹膛的左轮手枪，装填1-6发子弹\n" +
               "- 玩家轮流选择是否对自己开枪\n" +
               "- 存活获得分数，死亡游戏结束\n\n" +
               "🎮 **命令列表**:\n" +
               "- `demon start [子弹数]` - 开始新游戏 (默认1发子弹)\n" +
               "- `demon shoot` - 对自己开枪\n" +
               "- `demon status` - 查看游戏状态\n" +
               "- `demon end` - 结束当前游戏\n" +
               "- `demon help` - 显示帮助信息\n\n" +
               "⚡ **提示**: 存活次数越多，分数越高！";
    }

    private String buildGameStartMessage(GameState gameState, int bulletCount) {
        return "🎯 **恶魔轮盘游戏开始！** 🎯\n\n" +
               "🎭 **游戏背景**:\n" +
               "欢迎来到恶魔轮盘派对！这是一个刺激的生存游戏，测试你的勇气和运气。\n" +
               "在俄罗斯传说中，这是魔鬼与凡人之间的赌局，胜者可以获得荣耀，败者将面临命运的安排...\n\n" +
               "🔫 **游戏规则**:\n" +
               "• 一把6发弹膛的左轮手枪，随机装填 " + bulletCount + " 发子弹\n" +
               "• 子弹位置完全随机，每一轮都是未知的挑战\n" +
               "• 你需要对自己开枪，每存活一轮获得 10 分\n" +
               "• 弹膛会按顺序旋转：1→2→3→4→5→6→1...\n" +
               "• 如果击中子弹，游戏结束，但你的勇气将被铭记！\n\n" +
               "🎲 **当前状态**:\n" +
               "• 子弹数量: " + bulletCount + " 发\n" +
               "• 起始弹膛: " + gameState.getCurrentChamber() + " 号\n" +
               "• 存活奖励: 每轮 +10 分\n\n" +
               "⚡ **勇气时刻**: 面对未知的恐惧，你会选择开枪吗？\n" +
               "输入 `demon shoot` 来证明你的勇气！\n\n" +
               "🎮 **其他命令**: `demon status` 查看状态 | `demon end` 结束游戏";
    }

    private String buildShootResultMessage(GameState gameState) {
        return "🎲 **咔哒...** 🎲\n\n" +
               "✅ 你活了下来！\n" +
               "🎯 当前弹膛: " + gameState.getCurrentChamber() + " / 6\n" +
               "💎 当前得分: " + gameState.getScore() + " 分\n" +
               "🔫 还敢继续吗？输入 `demon shoot` 继续！";
    }

    private String buildGameOverMessage(String senderId, GameState gameState) {
        return "💥 **BANG! 游戏结束！** 💥\n\n" +
               "💀 你被淘汰了！\n" +
               "🎯 最终得分: " + gameState.getScore() + " 分\n" +
               "🔫 存活轮数: " + (gameState.getShotsTaken() - 1) + " 轮\n\n" +
               "输入 `demon start` 开始新游戏！";
    }

    private String buildGameStatusMessage(GameState gameState) {
        return "🎯 **游戏状态** 🎯\n\n" +
               "🎲 当前弹膛: " + gameState.getCurrentChamber() + " / 6\n" +
               "💎 当前得分: " + gameState.getScore() + " 分\n" +
               "🔫 已开枪次数: " + gameState.getShotsTaken() + " 次\n" +
               "🎮 游戏状态: " + (gameState.isGameOver() ? "已结束" : "进行中");
    }

    // 游戏状态类
    private static class GameState {
        private final String playerId;
        private final int totalBullets;
        private final List<Integer> bulletPositions;
        private int currentChamber;
        private int score;
        private int shotsTaken;
        private boolean gameOver;

        public GameState(String playerId, int bulletCount) {
            this.playerId = playerId;
            this.totalBullets = bulletCount;
            this.score = 0;
            this.currentChamber = 1;
            this.shotsTaken = 0;
            this.gameOver = false;

            // 随机分配子弹位置
            this.bulletPositions = new ArrayList<>();
            Random random = new Random();
            while (bulletPositions.size() < bulletCount) {
                int position = random.nextInt(6) + 1;
                if (!bulletPositions.contains(position)) {
                    bulletPositions.add(position);
                }
            }
            Collections.sort(bulletPositions);
        }

        public boolean shoot() {
            if (gameOver) {
                return false;
            }

            shotsTaken++;
            boolean isHit = bulletPositions.contains(currentChamber);

            if (isHit) {
                gameOver = true;
            } else {
                score += 10; // 存活奖励
                currentChamber++;
                if (currentChamber > 6) {
                    currentChamber = 1; // 重新开始
                }
            }

            return isHit;
        }

        public String getPlayerId() {
            return playerId;
        }

        public int getTotalBullets() {
            return totalBullets;
        }

        public int getCurrentChamber() {
            return currentChamber;
        }

        public int getScore() {
            return score;
        }

        public int getShotsTaken() {
            return shotsTaken;
        }

        public boolean isGameOver() {
            return gameOver;
        }

        public List<Integer> getBulletPositions() {
            return bulletPositions;
        }
    }
}