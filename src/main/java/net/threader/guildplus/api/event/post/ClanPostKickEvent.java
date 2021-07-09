package net.threader.guildplus.api.event.post;

import net.threader.guildplus.api.event.ClanEvent;
import net.threader.guildplus.model.Member;
import org.bukkit.OfflinePlayer;

public class ClanPostKickEvent extends ClanEvent {

    private Member kicking;
    private OfflinePlayer kicked;

    public ClanPostKickEvent(Member kicking, OfflinePlayer kicked) {
        super(kicking.getClan());
        this.kicking = kicking;
        this.kicked = kicked;
    }

    public Member getKicking() {
        return kicking;
    }

    public OfflinePlayer getKicked() {
        return kicked;
    }

}
