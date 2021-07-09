package net.threader.guildplus.model;

import org.bukkit.scheduler.BukkitTask;

public interface AllyRequest {
    void start();

    BukkitTask getTask();

    Clan getInvited();

    Clan getInviter();
}
