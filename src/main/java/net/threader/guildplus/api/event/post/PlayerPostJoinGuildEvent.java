package net.threader.guildplus.api.event.post;

import net.threader.guildplus.api.event.GuildEvent;
import net.threader.guildplus.model.Guild;
import org.bukkit.entity.Player;

public class PlayerPostJoinGuildEvent extends GuildEvent {

    private Player player;

    public PlayerPostJoinGuildEvent(Guild guild, Player player) {
        super(guild);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
