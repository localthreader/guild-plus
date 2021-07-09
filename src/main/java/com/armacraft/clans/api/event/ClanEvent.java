package com.armacraft.clans.api.event;

import com.armacraft.clans.model.Clan;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class ClanEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    protected Clan clan;

    public ClanEvent(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
