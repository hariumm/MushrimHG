package me.mushrim.hg.utils;

import org.bukkit.ChatColor;

public class ChatUtils {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}