package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ClanInviteEvent extends ClanEvent implements Cancellable {

    private Player invited;
    private Member inviter;
    private boolean canceled;

    public ClanInviteEvent(Clan clan, Player invited, Member inviter) {
        super(clan);
        this.invited = invited;
        this.inviter = inviter;
    }

    public Player getInvited() {
        return invited;
    }

    public Member getInviter() {
        return inviter;
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
