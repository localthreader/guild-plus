package com.armacraft.clans.model.implementation;

import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.instance.SingleAllyRequestController;
import com.armacraft.clans.controller.instance.SingleInviteController;
import com.armacraft.clans.model.AllyRequest;
import com.armacraft.clans.model.Clan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class AllyRequestImpl implements AllyRequest {
    private Clan inviter;
    private Clan invited;
    private BukkitTask task;
    private int seconds;

    public AllyRequestImpl(Clan inviter, Clan invited) {
        this.inviter = inviter;
        this.invited = invited;
        this.seconds = SingleAllyRequestController.INVITE_TIMEOUT_SECONDS;
    }

    @Override
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(ArmaClans.instance(), () -> {
            seconds--;
            if(seconds <= 0) {
                task.cancel();
                SingleAllyRequestController.INSTANCE.getInvites().remove(AllyRequestImpl.this);
            }
            if(seconds > 0 && seconds % 15 == 0) {
                Player leader = Bukkit.getPlayer(invited.getLeader().getUniqueId());
                if(leader != null && leader.isOnline()) {
                    new Message.Builder().fromConfig("clan_invite", ArmaClans.instance())
                            .addVariable("tag", inviter.getTag())
                            .build().send(leader);
                }
            }
        }, 20, 20);
    }

    @Override
    public BukkitTask getTask() {
        return task;
    }

    @Override
    public Clan getInvited() {
        return invited;
    }

    @Override
    public Clan getInviter() {
        return inviter;
    }
}
