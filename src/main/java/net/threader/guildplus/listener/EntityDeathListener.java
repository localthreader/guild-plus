package net.threader.guildplus.listener;

import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.GuildStat;
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
                    killer.getGuild().increaseStat(GuildStat.KILL, 1);
                });
            }
            if(event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                member.getGuild().increaseStat(GuildStat.DEATH, 1);
            }
        });
    }
}
