package com.armacraft.clans.api.event.pre;

import com.armacraft.clans.api.event.ClanEvent;
import com.armacraft.clans.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerPreLeaveClanEvent extends ClanEvent implements Cancellable {
    private boolean cancelled = false;
    private Player player;

    public PlayerPreLeaveClanEvent(Player player, Clan clan) {
        super(clan);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Clan getClan() {
        return clan;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
