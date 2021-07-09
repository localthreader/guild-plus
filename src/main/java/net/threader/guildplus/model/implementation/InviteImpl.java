package net.threader.guildplus.model.implementation;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.instance.SingleInviteController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Invite;
import net.threader.guildplus.utils.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class InviteImpl implements Invite {
    private BukkitTask task;
    private int seconds;
    private UUID invited;
    private Guild inviter;

    public InviteImpl(UUID invited, Guild inviter) {
        this.invited = invited;
        this.inviter = inviter;
        this.seconds = SingleInviteController.INVITE_TIMEOUT_SECONDS;
    }

    @Override
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(GuildPlus.instance(), () -> {
            seconds--;
            if(seconds <= 0) {
                task.cancel();
                SingleInviteController.INSTANCE.getInvites().remove(InviteImpl.this);
            }
            if(seconds > 0 && seconds % 15 == 0) {
                Player player = Bukkit.getPlayer(invited);
                if(player != null && player.isOnline()) {
                    new Message.Builder().fromConfig("guild_invite", GuildPlus.instance())
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
    public Guild getInviter() {
        return inviter;
    }
}
