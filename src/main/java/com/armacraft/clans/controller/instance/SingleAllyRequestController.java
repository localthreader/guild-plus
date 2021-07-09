package com.armacraft.clans.controller.instance;

import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.AllyRequestController;
import com.armacraft.clans.model.AllyRequest;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Invite;
import com.armacraft.clans.model.implementation.AllyRequestImpl;
import com.armacraft.clans.model.implementation.InviteImpl;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public enum SingleAllyRequestController implements AllyRequestController {
    INSTANCE;

    public static final int INVITE_TIMEOUT_SECONDS = ArmaClans.instance().getConfig().getInt("clan.invite.timeout_seconds");
    private final Set<AllyRequest> INVITES = new HashSet<>();

    @Override
    public Set<AllyRequest> getInvites() {
        return INVITES;
    }

    @Override
    public Set<AllyRequest> getInvitesOf(Clan clan) {
        return INVITES.stream().filter(invite -> invite.getInviter().equals(clan)).collect(Collectors.toSet());
    }

    @Override
        public Optional<Clan> getInviter(Clan invited) {
        return INVITES.stream().filter(invite -> invite.getInvited().equals(invited)).map(AllyRequest::getInviter).findFirst();
    }

    @Override
    public void addInvite(Clan inviter, Clan invited) {
        AllyRequest invite = new AllyRequestImpl(inviter, invited);
        this.INVITES.add(invite);
        invite.start();
    }

    @Override
    public void removeInvitesOf(Clan invited) {
        AtomicReference<AllyRequest> inviteAtomicReference = new AtomicReference<>();
        INVITES.stream().filter(x -> x.getInvited().equals(invited)).findFirst().ifPresent(inviteAtomicReference::set);
        if(inviteAtomicReference.get() != null) {
            inviteAtomicReference.get().getTask().cancel();
            INVITES.remove(inviteAtomicReference.get());
        }
    }
}
