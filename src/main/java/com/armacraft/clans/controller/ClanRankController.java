package com.armacraft.clans.controller;

import com.armacraft.clans.model.Clan;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ClanRankController {
    void startUpdateTask();
    Map<UUID, Integer> getCachedRank();
}
