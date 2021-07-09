package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class GuildInviteEvent extends GuildEvent implements Cancellable {

    private Player invited;
    private Member inviter;
    private boolean canceled;

    public GuildInviteEvent(Guild guild, Player invited, Member inviter) {
        super(guild);
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
