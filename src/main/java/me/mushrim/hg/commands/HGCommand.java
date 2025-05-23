package me.mushrim.hg.commands;

import me.mushrim.hg.HGPlugin;
import me.mushrim.hg.game.GameManager;
import me.mushrim.hg.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HGCommand implements CommandExecutor {

    private final GameManager gameManager;

    public HGCommand(HGPlugin plugin) {
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtils.color("&cApenas jogadores podem usar este comando!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatUtils.color("&6Comandos do MushrimHG:"));
            player.sendMessage(ChatUtils.color("&a/hg start &7- Inicia o jogo"));
            player.sendMessage(ChatUtils.color("&a/hg stop &7- Para o jogo"));
            player.sendMessage(ChatUtils.color("&a/hg setlobby &7- Define o lobby"));
            player.sendMessage(ChatUtils.color("&a/hg setspawn &7- Adiciona um spawn point"));
            return true;
        }

        if (!player.hasPermission("mushrimhg.admin")) {
            player.sendMessage(ChatUtils.color("&cVocê não tem permissão para isso!"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                gameManager.startGame();
                break;

            case "stop":
                gameManager.stopGame();
                break;

            case "setlobby":
                gameManager.setLobbyLocation(player.getLocation());
                player.sendMessage(ChatUtils.color("&aLobby definido com sucesso!"));
                break;

            case "setspawn":
                gameManager.addSpawnPoint(player.getLocation());
                player.sendMessage(ChatUtils.color("&aSpawn point adicionado com sucesso!"));
                break;

            default:
                player.sendMessage(ChatUtils.color("&cComando desconhecido! Use /hg para ajuda."));
                break;
        }

        return true;
    }
}