package dev.watchwolf.server.events.invincibility;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnPlayerDamage implements Listener {
    private PlayerDamageManager damageManager;

    public OnPlayerDamage(JavaPlugin plugin) {
        this.setInvincible(false); // by default, disabled

        Server server = plugin.getServer();
        server.getPluginManager().registerEvents(this, plugin); // call onDamage event
    }

    public void setInvincible(boolean invincible) {
        if (invincible) this.damageManager = new InvincibleMode();
        else this.damageManager = (e) -> { return false; };
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return; // not a player
        event.setCancelled(this.damageManager.shouldBeCancelled(event));
    }
}
