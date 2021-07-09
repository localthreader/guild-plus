package com.armacraft.clans.controller;

import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Invite;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface InviteController {
    Set<Invite> getInvites();
    Set<Invite> getInvitesOf(Clan clan);
    Optional<Clan> getInviter(UUID invited);
    void removeInvitesOf(Clan clan);
    void addInvite(Clan inviter, Player invited);
    void removeInvitesOf(UUID invited);
}
