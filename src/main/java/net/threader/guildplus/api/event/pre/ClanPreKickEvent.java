package net.threader.guildplus.api.event.pre;

import net.threader.guildplus.api.event.ClanEvent;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import org.bukkit.event.Cancellable;

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
