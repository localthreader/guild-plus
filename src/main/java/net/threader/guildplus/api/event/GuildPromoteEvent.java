package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Member;
import org.bukkit.event.Cancellable;

public class GuildPromoteEvent extends GuildEvent implements Cancellable {

    private Member promoter;
    private Member promoted;
    private boolean canceled;

    public GuildPromoteEvent(Member promoter, Member promoted) {
        super(promoter.getGuild());
        this.promoted = promoted;
        this.promoter = promoter;
    }

    public Member getPromoter() {
        return promoter;
    }

    public Member getPromoted() {
        return promoted;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
}
