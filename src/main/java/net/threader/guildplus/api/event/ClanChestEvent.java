package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Member;
import org.bukkit.event.Cancellable;

public class ClanChestEvent extends ClanEvent implements Cancellable {

    private Member member;
    private boolean canceled;
    private boolean isChestLeader;

    public ClanChestEvent(Member member, boolean isChestLeader) {
        super(member.getClan());
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
