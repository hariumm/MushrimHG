package me.mushrim.hg.game;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Countdown {

    private final JavaPlugin plugin;
    private final GameManager gameManager;
    private int timeLeft;

    public Countdown(JavaPlugin plugin, GameManager gameManager, int timeLeft) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.timeLeft = timeLeft;
    }

    public abstract void onTick(int timeLeft);
    public abstract void onFinish();

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameManager.getGameState() != GameState.STARTING) {
                    cancel();
                    return;
                }

                if (timeLeft <= 0) {
                    onFinish();
                    cancel();
                    return;
                }

                onTick(timeLeft);
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}