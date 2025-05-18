package me.mushrim.hg;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.mushrim.hg.commands.HGCommand;
import me.mushrim.hg.commands.StartCommand;
import me.mushrim.hg.events.GameEvents;
import me.mushrim.hg.events.PlayerEvents;
import me.mushrim.hg.game.GameManager;

public class Main extends JavaPlugin {

    private static Main instance;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        gameManager = new GameManager(this);

        getCommand("hg").setExecutor(new HGCommand(gameManager));
        getCommand("starthg").setExecutor(new StartCommand(gameManager));

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new GameEvents(gameManager), this);

        getLogger().info("MushrimHG habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MushrimHG desabilitado.");
    }

    public static Main getInstance() {
        return instance;
    }
}