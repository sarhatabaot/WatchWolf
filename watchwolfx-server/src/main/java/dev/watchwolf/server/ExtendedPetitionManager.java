package dev.watchwolf.server;

import org.bukkit.plugin.Plugin;

public class ExtendedPetitionManager {
    private Server watchwolf;
    private Plugin plugin;

    public ExtendedPetitionManager(Server watchwolf, Plugin plugin) {
        this.watchwolf = watchwolf;
        this.plugin = plugin;
    }

    public Server getWatchWolf() {
        return watchwolf;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
