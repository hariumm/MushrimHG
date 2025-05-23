package me.mushrim.hg.adapter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.mushrim.hg.HGPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MessageAdapter {

    private final HGPlugin plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public void createMessagesFile() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
            if (!messagesFile.exists()) {
                try {
                    plugin.getDataFolder().mkdirs();
                    messagesFile.createNewFile();
                } catch (IOException e) {
                    plugin.getServer().getConsoleSender().sendMessage("§cErro ao criar messages.yml: " + e.getMessage());
                }
            }
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        String msg = messagesConfig.getString("messages." + path);
        if (msg == null) return "§cMensagem não encontrada: " + path;
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public List<String> getListMessage(String path) {
        List<String> list = messagesConfig.getStringList("messages." + path);
        return list.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }

    public void save() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            plugin.getServer().getConsoleSender().sendMessage("§cErro ao salvar messages.yml: " + e.getMessage());
        }
    }
}
