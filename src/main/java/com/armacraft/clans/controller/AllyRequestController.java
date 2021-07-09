package com.armacraft.clans.controller;

import com.armacraft.clans.model.AllyRequest;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Invite;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AllyRequestController {
    Set<AllyRequest> getInvites();
    Set<AllyRequest> getInvitesOf(Clan clan);
    Optional<Clan> getInviter(Clan invited);
    void removeInvitesOf(Clan clan);
    void addInvite(Clan inviter, Clan invited);
}
