package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Guild;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class GuildEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    protected Guild guild;

    public GuildEvent(Guild guild) {
        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
