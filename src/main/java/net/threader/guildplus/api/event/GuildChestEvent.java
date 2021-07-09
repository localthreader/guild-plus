package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Member;
import org.bukkit.event.Cancellable;

public class GuildChestEvent extends GuildEvent implements Cancellable {

    private Member member;
    private boolean canceled;
    private boolean isChestLeader;

    public GuildChestEvent(Member member, boolean isChestLeader) {
        super(member.getGuild());
        this.member = member;
        this.isChestLeader = isChestLeader;
    }

    public Member getMember() {
        return member;
    }

    public boolean isChestLeader() {
        return isChestLeader;
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
