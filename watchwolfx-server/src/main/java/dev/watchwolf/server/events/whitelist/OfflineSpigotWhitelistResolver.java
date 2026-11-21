package dev.watchwolf.server.events.whitelist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spigot handles the whitelist by checking the user UUID to the authentication Mojang servers.
 * By setting offline mode to false (so bots can join) the UUID is randomly generated, so it
 * doesn't match by the official server UUID.
 * This class will disable the Spigot whitelist and will check itself the "whitelisted" users.
 */
public class OfflineSpigotWhitelistResolver implements ServerWhitelistResolver, Listener {

    private final Set<String> whitelistedUsers;

    public OfflineSpigotWhitelistResolver(JavaPlugin plugin) {
        this.whitelistedUsers = ConcurrentHashMap.newKeySet();

        org.bukkit.Server server = plugin.getServer();
        server.setWhitelist(false); // the whitelist will be handled internally
        server.getPluginManager().registerEvents(this, plugin); // call onJoin event
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (!this.whitelistedUsers.contains(event.getName())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "You're not whitelisted!");
        }
    }

    @Override
    public void addToWhitelist(String name) {
        this.whitelistedUsers.add(name);
    }

}
