package dev.watchwolf.server.events.invincibility;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class InvincibleMode implements PlayerDamageManager {
    @Override
    public boolean shouldBeCancelled(EntityDamageEvent e) {
        if (!(e instanceof EntityDamageByEntityEvent)) return true; // a player didn't hit the player, so cancel it

        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        return !(event.getDamager() instanceof Player); // cancel it if a player hit the player
    }
}
