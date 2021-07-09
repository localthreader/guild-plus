package net.threader.guildplus.api.event.post;

import net.threader.guildplus.api.event.GuildEvent;
import net.threader.guildplus.model.Member;
import org.bukkit.OfflinePlayer;

public class GuildPostKickEvent extends GuildEvent {

    private Member kicking;
    private OfflinePlayer kicked;

    public GuildPostKickEvent(Member kicking, OfflinePlayer kicked) {
        super(kicking.getGuild());
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
