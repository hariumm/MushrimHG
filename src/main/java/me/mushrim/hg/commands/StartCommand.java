package me.mushrim.hg.commands;

import me.mushrim.hg.game.GameManager;
import me.mushrim.hg.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommand implements CommandExecutor {

    private GameManager gameManager;

    public StartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        gameManager.startGame();
        sender.sendMessage(ChatUtils.color("&aIniciando o jogo..."));
        return true;
    }
}