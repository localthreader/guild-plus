package net.threader.guildplus.api.event.post;

import net.threader.guildplus.api.event.GuildEvent;
import net.threader.guildplus.model.Guild;

public class GuildPostCreateEvent extends GuildEvent {

    public GuildPostCreateEvent(Guild guild) {
        super(guild);
    }
}
