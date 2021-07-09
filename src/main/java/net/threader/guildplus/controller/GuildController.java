package net.threader.guildplus.controller;

import net.threader.guildplus.model.Guild;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface GuildController {
    Map<UUID, Guild> getGuilds();
    void destroy(Guild guild);
    void downloadGuilds();
    Guild registerGuild(Player leader, String tag, String name);
}
