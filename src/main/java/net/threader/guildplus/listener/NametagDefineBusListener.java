package net.threader.guildplus.listener;

import com.armacraft.armalib.api.event.NametagDefineBusEvent;
import com.armacraft.armalib.config.UserData;
import net.threader.guildplus.api.event.post.ClanPostDisbandEvent;
import net.threader.guildplus.api.event.post.ClanPostKickEvent;
import net.threader.guildplus.api.event.post.PlayerPostJoinClanEvent;
import net.threader.guildplus.api.event.post.PlayerPostLeaveClanEvent;
import net.threader.guildplus.model.Member;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class NametagDefineBusListener implements Listener {

    @EventHandler
    public void handleNametagBus(NametagDefineBusEvent event) {
        Member.of(event.getPlayer().getUniqueId()).ifPresent(member ->{
            member.getClan().getMembers().stream().map(Member::getName).forEach(event::add);
            member.getClan().getAlliances().forEach(ally ->
                    ally.getMembers().stream().map(Member::getName).forEach(event::add));
        });
    }

    @EventHandler
    public void handleKick(ClanPostKickEvent event) {
        UserData.from(event.getKicked().getPlayer().getUniqueId()).resendNametags();
        Set<Member> affectedMembers = new HashSet<>(event.getClan().getMembers());
        event.getClan().getAlliances().forEach(clan -> affectedMembers.addAll(clan.getMembers()));
        affectedMembers.stream().filter(player -> player.getOfflinePlayer().isOnline())
                .map(Member::getOfflinePlayer).map(OfflinePlayer::getPlayer)
                .forEach(player -> UserData.from(player.getUniqueId()).resendNametags());
    }

    @EventHandler
    public void handleLeave(PlayerPostLeaveClanEvent event) {
        UserData.from(event.getPlayer().getPlayer().getUniqueId()).resendNametags();
        Set<Member> affectedMembers = new HashSet<>(event.getClan().getMembers());
        event.getClan().getAlliances().forEach(clan -> affectedMembers.addAll(clan.getMembers()));
        affectedMembers.stream().filter(player -> player.getOfflinePlayer().isOnline())
                .map(Member::getOfflinePlayer).map(OfflinePlayer::getPlayer)
                .forEach(player -> UserData.from(player.getUniqueId()).resendNametags());
    }

    @EventHandler
    public void handleJoin(PlayerPostJoinClanEvent event) {
        UserData.from(event.getPlayer().getPlayer().getUniqueId()).resendNametags();
        Set<Member> affectedMembers = new HashSet<>(event.getClan().getMembers());
        event.getClan().getAlliances().forEach(clan -> affectedMembers.addAll(clan.getMembers()));
        affectedMembers.stream().filter(player -> player.getOfflinePlayer().isOnline())
                .map(Member::getOfflinePlayer).map(OfflinePlayer::getPlayer)
                .forEach(player -> UserData.from(player.getUniqueId()).resendNametags());
    }

    @EventHandler
    public void handleDisband(ClanPostDisbandEvent event) {
        UserData.from(event.getPlayer().getPlayer().getUniqueId()).resendNametags();
        event.getAffectedPlayers().forEach(player -> UserData.from(player.getUniqueId()).resendNametags());
    }

    @EventHandler
    public void onClanJoin(PlayerPostJoinClanEvent event) {
        UserData.from(event.getPlayer().getPlayer().getUniqueId()).resendNametags();
        Set<Member> affectedMembers = new HashSet<>(event.getClan().getMembers());
        event.getClan().getAlliances().forEach(clan -> affectedMembers.addAll(clan.getMembers()));
        affectedMembers.stream().filter(player -> player.getOfflinePlayer().isOnline())
                .map(Member::getOfflinePlayer).map(OfflinePlayer::getPlayer)
                .forEach(player -> UserData.from(player.getUniqueId()).resendNametags());
    }
}
