package dev.watchwolf.utils;

import org.bukkit.Bukkit;

public class ServerTypeGetter {
    public static boolean isPaper() {
        if (Bukkit.getVersion().contains("1.8")) {
            // in 1.8 Paper also has Spigot as name
            try {
                Class.forName("io.papermc.paperclip.PatchData");
                return true;
            } catch (ClassNotFoundException ex) {
                return false; // Spigot
            }
        }
        return Bukkit.getName().equals("Paper");
    }
}
