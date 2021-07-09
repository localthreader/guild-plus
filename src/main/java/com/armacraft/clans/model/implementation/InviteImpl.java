package com.armacraft.clans.model.implementation;

import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.instance.SingleInviteController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Invite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class InviteImpl implements Invite {
    private BukkitTask task;
    private int seconds;
    private UUID invited;
    private Clan inviter;

    public InviteImpl(UUID invited, Clan inviter) {
        this.invited = invited;
        this.inviter = inviter;
        this.seconds = SingleInviteController.INVITE_TIMEOUT_SECONDS;
    }

    @Override
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(ArmaClans.instance(), () -> {
            seconds--;
            if(seconds <= 0) {
                task.cancel();
                SingleInviteController.INSTANCE.getInvites().remove(InviteImpl.this);
            }
            if(seconds > 0 && seconds % 15 == 0) {
                Player player = Bukkit.getPlayer(invited);
                if(player != null && player.isOnline()) {
                    new Message.Builder().fromConfig("clan_invite", ArmaClans.instance())
                            .addVariable("tag", inviter.getTag())
                            .build().send(player);
                }
            }
        }, 20, 20);
    }

    @Override
    public BukkitTask getTask() {
        return task;
    }

    @Override
    public UUID getInvited() {
        return invited;
    }

    @Override
    public Clan getInviter() {
        return inviter;
    }
}
