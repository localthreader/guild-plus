package net.threader.guildplus.controller.instance;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.InviteController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Invite;
import net.threader.guildplus.model.implementation.InviteImpl;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public enum SingleInviteController implements InviteController {
    INSTANCE;

    public static final int INVITE_TIMEOUT_SECONDS = GuildPlus.instance().getConfig().getInt("guild.invite.timeout_seconds");
    private final Set<Invite> INVITES = new HashSet<>();

    @Override
    public Set<Invite> getInvites() {
        return INVITES;
    }

    @Override
    public Set<Invite> getInvitesOf(Guild guild) {
        return INVITES.stream().filter(invite -> invite.getInviter().equals(guild)).collect(Collectors.toSet());
    }

    @Override
    public Optional<Guild> getInviter(UUID invited) {
        return INVITES.stream().filter(invite -> invite.getInvited().equals(invited)).map(Invite::getInviter).findFirst();
    }

    @Override
    public void removeInvitesOf(Guild guild) {
        Set<Invite> invites = INVITES.stream().filter(x -> x.getInviter().equals(guild)).collect(Collectors.toSet());
        invites.forEach(INVITES::remove);
    }

    @Override
    public void addInvite(Guild inviter, Player invited) {
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
