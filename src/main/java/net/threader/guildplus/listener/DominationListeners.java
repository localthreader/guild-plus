package net.threader.guildplus.listener;

import com.armacraft.armadomination.event.DominationEndEvent;
import com.armacraft.armadomination.event.DominationStartEvent;
import com.armacraft.armadomination.event.PlayerOpenDominationSpotEvent;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.ClanStat;
import net.threader.guildplus.utils.ExperienceCalc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class DominationListeners implements Listener {
    @EventHandler
    public void onDominationOpen(PlayerOpenDominationSpotEvent event) {
        event.getSpot().getDominator().flatMap(dominator -> Member.of(UUID.fromString(event.getSpot().getDominator().get().getUniqueIdentifier()))).ifPresent(currentDominator -> {
            Member.of(event.getPlayer().getUniqueId()).ifPresent(dominatorMember -> {
                if (dominatorMember.getClan().equals(currentDominator.getClan())) {
                    event.getPlayer().sendMessage("§cEsse ponto já está dominado pelo seu clan.");
                    event.setCancelled(true);
                }
            });
        });
    }

    @EventHandler
    public void onDominationStart(DominationStartEvent event) {
        event.getPreviousDominator().ifPresent(previous -> {
            Member.of(event.getDominator().getUniqueId()).ifPresent(dominatorMember -> {
                Member.of(previous.getUniqueId()).ifPresent(previousMember -> {
                    if(previousMember.getClan().equals(dominatorMember.getClan())) {
                        event.getDominator().getPlayer().sendMessage("§cEsse ponto já está dominado pelo seu clan.");
                        event.setCancelled(true);
                    }
                });
            });
        });
    }

    @EventHandler
    public void onDominationEnd(DominationEndEvent event) {
        Member.of(event.getDominator().getUniqueId()).ifPresent(dominatorMember -> {
            Clan clan = dominatorMember.getClan();
            int experience = ExperienceCalc.getExperienceFor(clan, 10.5F);
            clan.increaseStat(ClanStat.EXP, experience);
            clan.increaseStat(ClanStat.POINTS, 80);
            clan.broadcast(new Message.Builder().fromConfig("domination_end", GuildPlus.instance())
                            .addVariable("player", dominatorMember.getName())
                            .addVariable("xp_quantity", experience).build());
        });
    }
}
