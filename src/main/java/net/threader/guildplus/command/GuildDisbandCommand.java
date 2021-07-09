package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.instance.SingleConfirmationController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildDisbandCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player leaving = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_GUILD.test(leaving.getUniqueId())) {
            leaving.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(leaving);
            return CommandResult.builder().build();
        }

        Member leavingMember = Member.of(leaving.getUniqueId()).get();
        Guild guild = leavingMember.getGuild();
        SingleConfirmationController controller = SingleConfirmationController.INSTANCE;

        if(CommonVerifiers.HAS_OFFICE.test(leaving.getUniqueId(), Office.LEADER)) {
            if(!controller.getDisbandConfirmations().contains(guild)) {
                new Message.Builder().fromConfig("guild_disband_confirm", GuildPlus.instance()).build().send(leaving);
                controller.addDisbandConfirmation(guild);
                return CommandResult.builder().build();
            }
        }
        new Message.Builder().fromConfig("cargo_insuficiente", GuildPlus.instance()).build().send(leaving);
        return CommandResult.builder().build();
    }
}
