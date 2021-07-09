package net.threader.guildplus.controller.instance;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.ConfirmationController;
import net.threader.guildplus.model.Guild;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public enum SingleConfirmationController implements ConfirmationController {
    INSTANCE;

    private final Map<Guild, UUID> OWNERSHIP_TRANSFER_CONFIRMS = new HashMap<>();
    private final Set<Guild> DISBAND_CONFIRMS = new HashSet<>();

    @Override
    public Map<Guild, UUID> getOwnershipTransferConfirmations() {
        return OWNERSHIP_TRANSFER_CONFIRMS;
    }

    @Override
    public Set<Guild> getDisbandConfirmations() {
        return DISBAND_CONFIRMS;
    }

    @Override
    public void addOwnershipTransferConfirmation(Guild guild, UUID uid) {
        OWNERSHIP_TRANSFER_CONFIRMS.put(guild, uid);
        Bukkit.getScheduler().runTaskLater(GuildPlus.instance(), () -> {
            OWNERSHIP_TRANSFER_CONFIRMS.remove(guild);
        }, 60*20);
    }

    @Override
    public void addDisbandConfirmation(Guild guild) {
        DISBAND_CONFIRMS.add(guild);
        Bukkit.getScheduler().runTaskLater(GuildPlus.instance(), () -> {
            DISBAND_CONFIRMS.remove(guild);
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
    public void destroyAllConfirmationsFor(Guild guild) {
        OWNERSHIP_TRANSFER_CONFIRMS.entrySet().stream()
                .filter(entry -> entry.getKey().equals(guild))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())
                .forEach(OWNERSHIP_TRANSFER_CONFIRMS::remove);
        DISBAND_CONFIRMS.stream()
                .filter(guild1 -> guild1.equals(guild))
                .collect(Collectors.toSet())
                .forEach(DISBAND_CONFIRMS::remove);
    }


}
