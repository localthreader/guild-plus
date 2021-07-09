package net.threader.guildplus.controller.instance;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.ConfirmationController;
import net.threader.guildplus.model.Clan;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public enum SingleConfirmationController implements ConfirmationController {
    INSTANCE;

    private final Map<Clan, UUID> OWNERSHIP_TRANSFER_CONFIRMS = new HashMap<>();
    private final Set<Clan> DISBAND_CONFIRMS = new HashSet<>();

    @Override
    public Map<Clan, UUID> getOwnershipTransferConfirmations() {
        return OWNERSHIP_TRANSFER_CONFIRMS;
    }

    @Override
    public Set<Clan> getDisbandConfirmations() {
        return DISBAND_CONFIRMS;
    }

    @Override
    public void addOwnershipTransferConfirmation(Clan clan, UUID uid) {
        OWNERSHIP_TRANSFER_CONFIRMS.put(clan, uid);
        Bukkit.getScheduler().runTaskLater(GuildPlus.instance(), () -> {
            OWNERSHIP_TRANSFER_CONFIRMS.remove(clan);
        }, 60*20);
    }

    @Override
    public void addDisbandConfirmation(Clan clan) {
        DISBAND_CONFIRMS.add(clan);
        Bukkit.getScheduler().runTaskLater(GuildPlus.instance(), () -> {
            DISBAND_CONFIRMS.remove(clan);
        }, 60*20L);
    }

    @Override
    public void destroyAllConfirmationsFor(UUID player) {
        OWNERSHIP_TRANSFER_CONFIRMS.entrySet().stream()
                .filter(entry -> entry.getValue().equals(player))
                .map(Map.Entry::getKey).collect(Collectors.toSet())
                .forEach(OWNERSHIP_TRANSFER_CONFIRMS::remove);
    }

    @Override
    public void destroyAllConfirmationsFor(Clan clan) {
        OWNERSHIP_TRANSFER_CONFIRMS.entrySet().stream()
                .filter(entry -> entry.getKey().equals(clan))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())
                .forEach(OWNERSHIP_TRANSFER_CONFIRMS::remove);
        DISBAND_CONFIRMS.stream()
                .filter(clan1 -> clan1.equals(clan))
                .collect(Collectors.toSet())
                .forEach(DISBAND_CONFIRMS::remove);
    }


}
