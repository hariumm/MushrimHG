package me.mushrim.hg.listeners;

import me.mushrim.hg.HGPlugin;
import me.mushrim.hg.game.GameManager;
import me.mushrim.hg.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class GameEvents implements Listener {

    private final GameManager gameManager;

    public GameEvents(HGPlugin plugin) {
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (gameManager.getGameState() != GameState.ACTIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (gameManager.getGameState() != GameState.ACTIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (gameManager.getGameState() != GameState.ACTIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (gameManager.getGameState() != GameState.ACTIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (gameManager.getGameState() != GameState.ACTIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (gameManager.getGameState() != GameState.ACTIVE) {
            event.setCancelled(true);
        }
    }
}