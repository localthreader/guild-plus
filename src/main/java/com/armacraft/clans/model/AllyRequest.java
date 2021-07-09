package com.armacraft.clans.model;

import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public interface AllyRequest {
    void start();

    BukkitTask getTask();

    Clan getInvited();

    Clan getInviter();
}
