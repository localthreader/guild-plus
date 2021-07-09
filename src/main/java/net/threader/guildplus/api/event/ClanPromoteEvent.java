package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Member;
import org.bukkit.event.Cancellable;

public class ClanPromoteEvent extends ClanEvent implements Cancellable {

    private Member promoter;
    private Member promoted;
    private boolean canceled;

    public ClanPromoteEvent(Member promoter, Member promoted) {
        super(promoter.getClan());
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
