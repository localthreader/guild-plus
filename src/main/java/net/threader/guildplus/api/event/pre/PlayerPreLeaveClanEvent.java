package net.threader.guildplus.api.event.pre;

import net.threader.guildplus.api.event.ClanEvent;
import net.threader.guildplus.model.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

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
