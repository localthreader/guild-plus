package net.threader.guildplus.controller;

import net.threader.guildplus.model.Clan;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ConfirmationController {
    Map<Clan, UUID> getOwnershipTransferConfirmations();

    Set<Clan> getDisbandConfirmations();

    void addOwnershipTransferConfirmation(Clan clan, UUID uid);

    void addDisbandConfirmation(Clan uid);

    void destroyAllConfirmationsFor(UUID player);

    void destroyAllConfirmationsFor(Clan clan);
}
