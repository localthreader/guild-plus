package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.api.event.post.ClanPostKickEvent;
import com.armacraft.clans.api.event.pre.ClanPreKickEvent;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ClanKickCommand implements CommandRunner {

    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        String kickedNick = commandContext.getArgumentAs("player", String.class);
        OfflinePlayer kicked = Bukkit.getOfflinePlayer(kickedNick);
        Player kicking = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_CLAN.test(kicking.getUniqueId())) {
            kicking.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(kicking);
            return CommandResult.builder().build();
        }

        Member kickingMember = Member.of(kicking.getUniqueId()).get();

        if(!CommonVerifiers.SAME_CLAN.test(kicking.getUniqueId(), kicked.getUniqueId())) {
            return CommandResult.builder().result(Result.FAILURE)
                    .message(Message.Util.of("nao_mesmo_clan", ArmaClans.instance())).build();
        }

        Member beingKicked = Member.of(kicked.getUniqueId()).get();

        if(!CommonVerifiers.HAS_OFFICE.test(kicking.getUniqueId(), Office.SUBLEADER)
                || beingKicked.getOffice().getId() >= kickingMember.getOffice().getId()) {
            return CommandResult.builder().result(Result.FAILURE)
                    .message(Message.Util.of("cargo_insuficiente", ArmaClans.instance())).build();
        }

        Clan clan = kickingMember.getClan();
        ClanPreKickEvent preKickEvent = new ClanPreKickEvent(clan, beingKicked, kickingMember);
        Bukkit.getPluginManager().callEvent(preKickEvent);
        if(!preKickEvent.isCancelled()) {
            OfflinePlayer kickedOfflinePlayer = Bukkit.getOfflinePlayer(beingKicked.getUniqueId());
            ClanPostKickEvent postKickEvent = new ClanPostKickEvent(beingKicked, kickedOfflinePlayer);
            Bukkit.getPluginManager().callEvent(postKickEvent);
            beingKicked.destroy();
            clan.broadcast(new Message.Builder().fromConfig("player_kickado", ArmaClans.instance())
                    .addVariable("membro", kickingMember.getName())
                    .addVariable("player", beingKicked.getName()).build());
            if(kickedOfflinePlayer.isOnline()) {
                new Message.Builder().fromConfig("foi_kickado", ArmaClans.instance()).build().send((Player)kickedOfflinePlayer);
            }
        }

        return CommandResult.builder().build();
    }
}
