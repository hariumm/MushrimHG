package me.mushrim.hg.listeners;

import me.mushrim.hg.HGPlugin;
import me.mushrim.hg.game.GameManager;
import me.mushrim.hg.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    private final HGPlugin plugin;
    private final GameManager gameManager;

    public PlayerEvents(HGPlugin plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (gameManager.getGameState() == GameState.WAITING) {
            event.setJoinMessage(plugin.getMessageAdapter().getMessage("join-message").replace("{PLAYER}", player.getName()));
            player.teleport(gameManager.getLobbyLocation());
            gameManager.getScoreboardManager().createScoreboard(player);
        } else {
            event.setJoinMessage(null);
            player.kickPlayer(plugin.getMessageAdapter().getMessage("already-game-in-progress"));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (gameManager.getGameState() != GameState.WAITING) {
            if (gameManager.getPlayers().contains(player.getUniqueId())) {
                gameManager.getPlayers().remove(player.getUniqueId());
                event.setQuitMessage(plugin.getMessageAdapter().getMessage("exit-message").replace("{PLAYER}", player.getName()));

                // Verificar se o jogo acabou
                if (gameManager.getPlayers().size() <= 1) {
                    gameManager.stopGame();
                }
            }
        } else {
            event.setQuitMessage(plugin.getMessageAdapter().getMessage("exit-message").replace("{PLAYER}", player.getName()));
        }

        gameManager.getScoreboardManager().removeScoreboard(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (gameManager.getGameState() == GameState.ACTIVE && gameManager.getPlayers().contains(player.getUniqueId())) {
            gameManager.getPlayers().remove(player.getUniqueId());

            if (killer != null && gameManager.getPlayers().contains(killer.getUniqueId())) {
                gameManager.addKill(killer);
                event.setDeathMessage(plugin.getMessageAdapter().getMessage("player-killed")
                        .replace("{PLAYER}", player.getName())
                        .replace("{KILLER}", killer.getName()));
            } else {
                event.setDeathMessage(plugin.getMessageAdapter().getMessage("player-died")
                        .replace("{PLAYER}", player.getName()));
            }

            if (gameManager.getPlayers().size() == 1) {
                Player winner = Bukkit.getPlayer(gameManager.getPlayers().get(0));
                if (winner != null) {
                    Bukkit.broadcastMessage(plugin.getMessageAdapter().getMessage("player-win").replace("{PLAYER}", player.getName()));
                    gameManager.stopGame();
                }
            }
        }
    }
}