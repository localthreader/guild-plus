package com.armacraft.clans.api.event.post;

import com.armacraft.clans.api.event.ClanEvent;
import com.armacraft.clans.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

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
