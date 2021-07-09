package com.armacraft.clans.api.event.post;

import com.armacraft.clans.api.event.ClanEvent;
import com.armacraft.clans.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerPostLeaveClanEvent extends ClanEvent {
    private Player player;

    public PlayerPostLeaveClanEvent(Clan clan, Player player) {
        super(clan);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
