package com.armacraft.clans.api.event;

import com.armacraft.clans.model.Member;
import org.bukkit.event.Cancellable;

public class ClanDemoteEvent extends ClanEvent implements Cancellable {

    private Member demoter;
    private Member demoted;
    private boolean canceled;

    public ClanDemoteEvent(Member demoter, Member demoted) {
        super(demoter.getClan());
        this.demoted = demoted;
        this.demoter = demoter;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }
}
