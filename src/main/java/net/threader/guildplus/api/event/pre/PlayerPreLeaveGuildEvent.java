package net.threader.guildplus.api.event.pre;

import net.threader.guildplus.api.event.GuildEvent;
import net.threader.guildplus.model.Guild;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerPreLeaveGuildEvent extends GuildEvent implements Cancellable {
    private boolean cancelled = false;
    private Player player;

    public PlayerPreLeaveGuildEvent(Player player, Guild guild) {
        super(guild);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
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
