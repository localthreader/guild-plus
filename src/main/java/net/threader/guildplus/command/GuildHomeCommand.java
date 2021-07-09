package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threader.guildplus.utils.EasyTeleportHandler;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildHomeCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(member.getGuild().getHome() == null) {
            return CommandResult.builder().message("A sua guilda não tem uma home definida").result(Result.FAILURE).build();
        }

        EasyTeleportHandler.enqueueTeleport(player, member.getGuild().getHome(), 5,true, false);

        return CommandResult.builder().message("Home da guilda atualizada.").result(Result.SUCCESS).build();
    }
}
