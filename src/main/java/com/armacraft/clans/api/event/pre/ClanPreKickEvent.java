package com.armacraft.clans.api.event.pre;

import com.armacraft.clans.api.event.ClanEvent;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class ClanPreKickEvent extends ClanEvent implements Cancellable {

    private Member beingKicked;
    private boolean cancelled;
    private Member kicking;

    public ClanPreKickEvent(Clan clan, Member beingKicked, Member kicking) {
        super(clan);
        this.beingKicked = beingKicked;
        this.kicking = kicking;
    }

    public Member getBeingKicked() {
        return beingKicked;
    }

    public Member getKicking() {
        return kicking;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
