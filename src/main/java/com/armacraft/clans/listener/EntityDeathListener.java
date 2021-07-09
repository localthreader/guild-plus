package com.armacraft.clans.listener;

import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.ClanStat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class EntityDeathListener implements Listener {
    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Member.of(event.getEntity().getUniqueId()).ifPresent(member -> {
            if(event.getEntity().getKiller() != null) {
                Member.of(event.getEntity().getKiller().getUniqueId()).ifPresent(killer -> {
                    killer.getClan().increaseStat(ClanStat.KILL, 1);
                });
            }
            if(event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                member.getClan().increaseStat(ClanStat.DEATH, 1);
            }
        });
    }
}
