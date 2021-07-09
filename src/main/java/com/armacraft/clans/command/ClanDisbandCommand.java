package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.instance.SingleConfirmationController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;

public class ClanDisbandCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player leaving = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_CLAN.test(leaving.getUniqueId())) {
            leaving.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(leaving);
            return CommandResult.builder().build();
        }

        Member leavingMember = Member.of(leaving.getUniqueId()).get();
        Clan clan = leavingMember.getClan();
        SingleConfirmationController controller = SingleConfirmationController.INSTANCE;

        if(CommonVerifiers.HAS_OFFICE.test(leaving.getUniqueId(), Office.LEADER)) {
            if(!controller.getDisbandConfirmations().contains(clan)) {
                new Message.Builder().fromConfig("clan_disband_confirm", ArmaClans.instance()).build().send(leaving);
                controller.addDisbandConfirmation(clan);
                return CommandResult.builder().build();
            }
        }
        new Message.Builder().fromConfig("cargo_insuficiente", ArmaClans.instance()).build().send(leaving);
        return CommandResult.builder().build();
    }
}
