package com.armacraft.clans.controller;

import com.armacraft.clans.model.Clan;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ClanController {
    Map<UUID, Clan> getClans();
    void destroy(Clan clan);
    void downloadClans();
    Clan registerClan(Player leader, String tag, String name);
}
