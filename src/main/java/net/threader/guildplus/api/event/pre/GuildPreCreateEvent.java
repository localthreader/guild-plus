package net.threader.guildplus.api.event.pre;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GuildPreCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private String name;
    private String tag;
    private Player player;
    private boolean cancelled = false;

    public GuildPreCreateEvent(String name, String tag, Player player) {
        this.name = name;
        this.tag = tag;
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
