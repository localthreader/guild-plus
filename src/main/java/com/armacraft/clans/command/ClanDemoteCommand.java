package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.api.event.ClanPromoteEvent;
import com.armacraft.clans.controller.ConfirmationController;
import com.armacraft.clans.controller.instance.SingleConfirmationController;
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

public class ClanDemoteCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(commandContext.getArgumentAs("player", String.class));

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(member.getOffice() != Office.LEADER) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("cargo_insuficiente", ArmaClans.instance())).build();
        }

        Clan clan = member.getClan();

        Member demotedMember = Member.of(targetPlayer.getUniqueId()).get();

        switch(demotedMember.getOffice()) {
            case MEMBER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_ser_rebaixado", ArmaClans.instance())).result(Result.SUCCESS).build();
            case SUBLEADER:
                ClanPromoteEvent event = new ClanPromoteEvent(member, demotedMember);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    demotedMember.updateOffice(Office.MEMBER);
                    clan.broadcast(new Message.Builder().fromConfig("foi_rebaixado", ArmaClans.instance()).addVariable("player", demotedMember.getName()).build());
                }
                break;
            case LEADER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_rebaixar_vc_mesmo", ArmaClans.instance())).build();
        }
        return CommandResult.builder().build();
    }
}
