package com.armacraft.clans.controller.instance;

import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.InviteController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Invite;
import com.armacraft.clans.model.implementation.InviteImpl;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public enum SingleInviteController implements InviteController {
    INSTANCE;

    public static final int INVITE_TIMEOUT_SECONDS = ArmaClans.instance().getConfig().getInt("clan.invite.timeout_seconds");
    private final Set<Invite> INVITES = new HashSet<>();

    @Override
    public Set<Invite> getInvites() {
        return INVITES;
    }

    @Override
    public Set<Invite> getInvitesOf(Clan clan) {
        return INVITES.stream().filter(invite -> invite.getInviter().equals(clan)).collect(Collectors.toSet());
    }

    @Override
    public Optional<Clan> getInviter(UUID invited) {
        return INVITES.stream().filter(invite -> invite.getInvited().equals(invited)).map(Invite::getInviter).findFirst();
    }

    @Override
    public void removeInvitesOf(Clan clan) {
        Set<Invite> invites = INVITES.stream().filter(x -> x.getInviter().equals(clan)).collect(Collectors.toSet());
        invites.forEach(INVITES::remove);
    }

    @Override
    public void addInvite(Clan inviter, Player invited) {
        InviteImpl invite = new InviteImpl(invited.getUniqueId(), inviter);
        this.INVITES.add(invite);
        invite.start();
    }

    @Override
    public void removeInvitesOf(UUID invited) {
        AtomicReference<Invite> inviteAtomicReference = new AtomicReference<>();
        INVITES.stream().filter(x -> x.getInvited().equals(invited)).findFirst().ifPresent(inviteAtomicReference::set);
        if(inviteAtomicReference.get() != null) {
            inviteAtomicReference.get().getTask().cancel();
            INVITES.remove(inviteAtomicReference.get());
        }
    }
}
