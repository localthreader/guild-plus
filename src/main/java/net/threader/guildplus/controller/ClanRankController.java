package net.threader.guildplus.controller;

import java.util.Map;
import java.util.UUID;

public interface ClanRankController {
    void startUpdateTask();
    Map<UUID, Integer> getCachedRank();
}
