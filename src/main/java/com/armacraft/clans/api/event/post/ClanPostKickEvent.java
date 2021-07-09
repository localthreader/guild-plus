package com.armacraft.clans.api.event.post;

import com.armacraft.clans.api.event.ClanEvent;
import com.armacraft.clans.model.Member;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

public class ClanPostKickEvent extends ClanEvent {

    private Member kicking;
    private OfflinePlayer kicked;

    public ClanPostKickEvent(Member kicking, OfflinePlayer kicked) {
        super(kicking.getClan());
        this.kicking = kicking;
        this.kicked = kicked;
    }

    public Member getKicking() {
        return kicking;
    }

    public OfflinePlayer getKicked() {
        return kicked;
    }

}
