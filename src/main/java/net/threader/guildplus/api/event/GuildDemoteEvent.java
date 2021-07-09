package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Member;
import org.bukkit.event.Cancellable;

public class GuildDemoteEvent extends GuildEvent implements Cancellable {

    private Member demoter;
    private Member demoted;
    private boolean canceled;

    public GuildDemoteEvent(Member demoter, Member demoted) {
        super(demoter.getGuild());
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
