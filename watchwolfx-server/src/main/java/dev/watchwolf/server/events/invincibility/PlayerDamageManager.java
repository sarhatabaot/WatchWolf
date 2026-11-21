package dev.watchwolf.server.events.invincibility;

import org.bukkit.event.entity.EntityDamageEvent;

public interface PlayerDamageManager {
    boolean shouldBeCancelled(EntityDamageEvent e);
}
