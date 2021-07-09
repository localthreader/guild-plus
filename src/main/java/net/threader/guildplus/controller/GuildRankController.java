package net.threader.guildplus.controller;

import java.util.Map;
import java.util.UUID;

public interface GuildRankController {
    void startUpdateTask();
    Map<UUID, Integer> getCachedRank();
}
