package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.instance.SingleConfirmationController;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;

public class ClanDisbandCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player leaving = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_CLAN.test(leaving.getUniqueId())) {
            leaving.sendMessage(Message.Util.of("sem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(leaving);
            return CommandResult.builder().build();
        }

        Member leavingMember = Member.of(leaving.getUniqueId()).get();
        Clan clan = leavingMember.getClan();
        SingleConfirmationController controller = SingleConfirmationController.INSTANCE;

        if(CommonVerifiers.HAS_OFFICE.test(leaving.getUniqueId(), Office.LEADER)) {
            if(!controller.getDisbandConfirmations().contains(clan)) {
                new Message.Builder().fromConfig("clan_disband_confirm", GuildPlus.instance()).build().send(leaving);
                controller.addDisbandConfirmation(clan);
                return CommandResult.builder().build();
            }
        }
        new Message.Builder().fromConfig("cargo_insuficiente", GuildPlus.instance()).build().send(leaving);
        return CommandResult.builder().build();
    }
}
