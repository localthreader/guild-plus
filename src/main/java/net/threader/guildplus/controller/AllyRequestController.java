package net.threader.guildplus.controller;

import net.threader.guildplus.model.AllyRequest;
import net.threader.guildplus.model.Clan;

import java.util.Optional;
import java.util.Set;

public interface AllyRequestController {
    Set<AllyRequest> getInvites();
    Set<AllyRequest> getInvitesOf(Clan clan);
    Optional<Clan> getInviter(Clan invited);
    void removeInvitesOf(Clan clan);
    void addInvite(Clan inviter, Clan invited);
}
