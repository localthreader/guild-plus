package net.threader.guildplus.controller;

import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Invite;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface InviteController {
    Set<Invite> getInvites();
    Set<Invite> getInvitesOf(Guild guild);
    Optional<Guild> getInviter(UUID invited);
    void removeInvitesOf(Guild guild);
    void addInvite(Guild inviter, Player invited);
    void removeInvitesOf(UUID invited);
}
