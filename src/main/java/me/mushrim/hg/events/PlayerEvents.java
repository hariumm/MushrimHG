package me.mushrim.hg.events;

import me.mushrim.hg.HGPlugin;
import me.mushrim.hg.game.GameManager;
import me.mushrim.hg.game.GameState;
import me.mushrim.hg.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    private final GameManager gameManager;

    public PlayerEvents(HGPlugin plugin) {
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (gameManager.getGameState() == GameState.WAITING) {
            event.setJoinMessage(ChatUtils.color("&a" + player.getName() + " entrou no jogo!"));
            player.teleport(gameManager.getLobbyLocation());
            gameManager.getScoreboardManager().createScoreboard(player);
        } else {
            event.setJoinMessage(null);
            player.kickPlayer(ChatUtils.color("&cO jogo já está em andamento!"));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (gameManager.getGameState() != GameState.WAITING) {
            if (gameManager.getPlayers().contains(player.getUniqueId())) {
                gameManager.getPlayers().remove(player.getUniqueId());
                event.setQuitMessage(ChatUtils.color("&c" + player.getName() + " saiu do jogo!"));

                // Verificar se o jogo acabou
                if (gameManager.getPlayers().size() <= 1) {
                    gameManager.stopGame();
                }
            }
        } else {
            event.setQuitMessage(ChatUtils.color("&c" + player.getName() + " saiu do jogo!"));
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
                event.setDeathMessage(ChatUtils.color("&c" + player.getName() + " foi morto por " + killer.getName() + "!"));
            } else {
                event.setDeathMessage(ChatUtils.color("&c" + player.getName() + " morreu!"));
            }

            if (gameManager.getPlayers().size() == 1) {
                Player winner = Bukkit.getPlayer(gameManager.getPlayers().get(0));
                if (winner != null) {
                    Bukkit.broadcastMessage(ChatUtils.color("&6&l" + winner.getName() + " venceu o Hunger Games!"));
                    gameManager.stopGame();
                }
            }
        }
    }
}