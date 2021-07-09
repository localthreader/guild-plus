package net.threader.guildplus.api.event.post;

import net.threader.guildplus.api.event.GuildEvent;
import net.threader.guildplus.model.Guild;
import org.bukkit.entity.Player;

public class PlayerPostLeaveGuildEvent extends GuildEvent {
    private Player player;

    public PlayerPostLeaveGuildEvent(Guild guild, Player player) {
        super(guild);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
