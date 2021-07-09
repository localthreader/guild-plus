package com.armacraft.clans.api.event.post;

import com.armacraft.clans.api.event.ClanEvent;
import com.armacraft.clans.model.Clan;
import org.bukkit.event.HandlerList;

public class ClanPostCreateEvent extends ClanEvent {

    public ClanPostCreateEvent(Clan clan) {
        super(clan);
    }
}
