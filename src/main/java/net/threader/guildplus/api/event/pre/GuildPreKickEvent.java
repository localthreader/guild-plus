package net.threader.guildplus.api.event.pre;

import net.threader.guildplus.api.event.GuildEvent;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import org.bukkit.event.Cancellable;

public class GuildPreKickEvent extends GuildEvent implements Cancellable {

    private Member beingKicked;
    private boolean cancelled;
    private Member kicking;

    public GuildPreKickEvent(Guild guild, Member beingKicked, Member kicking) {
        super(guild);
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
