package net.threader.guildplus.api.event.pre;

import net.threader.guildplus.api.event.GuildEvent;
import net.threader.guildplus.model.Guild;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class GuildPreDisbandEvent extends GuildEvent implements Cancellable {
    private boolean cancelled = false;
    private Player disbanding;

    public GuildPreDisbandEvent(Player disbanding, Guild guild) {
        super(guild);
        this.disbanding = disbanding;
    }

    public Player getPlayer() {
        return disbanding;
    }

    public Guild getGuild() {
        return guild;
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
