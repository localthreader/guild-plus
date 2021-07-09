package net.threader.guildplus.controller;

import net.threader.guildplus.model.AllyRequest;
import net.threader.guildplus.model.Guild;

import java.util.Optional;
import java.util.Set;

public interface AllyRequestController {
    Set<AllyRequest> getInvites();
    Set<AllyRequest> getInvitesOf(Guild guild);
    Optional<Guild> getInviter(Guild invited);
    void removeInvitesOf(Guild guild);
    void addInvite(Guild inviter, Guild invited);
}
