package net.threader.guildplus.api.event.post;

import net.threader.guildplus.api.event.ClanEvent;
import net.threader.guildplus.model.Clan;

public class ClanPostCreateEvent extends ClanEvent {

    public ClanPostCreateEvent(Clan clan) {
        super(clan);
    }
}
