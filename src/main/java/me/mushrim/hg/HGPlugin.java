package me.mushrim.hg;

import lombok.Getter;
import me.mushrim.hg.adapter.MessageAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.mushrim.hg.commands.HGCommand;
import me.mushrim.hg.commands.StartCommand;
import me.mushrim.hg.listeners.GameEvents;
import me.mushrim.hg.listeners.PlayerEvents;
import me.mushrim.hg.game.GameManager;

@Getter
public class HGPlugin extends JavaPlugin {

    private MessageAdapter messageAdapter;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messageAdapter = new MessageAdapter(this);
        messageAdapter.createMessagesFile();

        gameManager = new GameManager(this);

        getCommand("hg").setExecutor(new HGCommand(this));
        getCommand("starthg").setExecutor(new StartCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new GameEvents(this), this);

        getLogger().info("MushrimHG habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MushrimHG desabilitado.");
    }
}