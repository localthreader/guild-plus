package net.threader.guildplus.controller;

import net.threader.guildplus.model.Guild;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ConfirmationController {
    Map<Guild, UUID> getOwnershipTransferConfirmations();

    Set<Guild> getDisbandConfirmations();

    void addOwnershipTransferConfirmation(Guild guild, UUID uid);

    void addDisbandConfirmation(Guild uid);

    void destroyAllConfirmationsFor(UUID player);

    void destroyAllConfirmationsFor(Guild guild);
}
