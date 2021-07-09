package net.threader.guildplus.api.event.post;

import net.threader.guildplus.api.event.ClanEvent;
import net.threader.guildplus.model.Clan;
import org.bukkit.entity.Player;

public class PlayerPostJoinClanEvent extends ClanEvent {

    private Player player;

    public PlayerPostJoinClanEvent(Clan clan, Player player) {
        super(clan);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
