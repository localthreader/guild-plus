package net.threader.guildplus.listener;

import net.threader.guildplus.model.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamagedByEntityListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Member.of(event.getDamager().getUniqueId()).ifPresent(damagerMember -> {
                Member.of(event.getEntity().getUniqueId()).ifPresent(damagedMember -> {
                    String worldName = event.getEntity().getWorld().getName();
                    if(worldName.contains("cidade_") || worldName.contains("arenapvp")) {
                        if(damagedMember.getGuild().equals(damagerMember.getGuild())) {
                            event.setCancelled(true);
                        }
                    }
                });
            });
        }
    }
}
