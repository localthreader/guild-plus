package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.EasyTeleportHandler;
import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;

public class ClanHomeCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(member.getClan().getHome() == null) {
            return CommandResult.builder().message("O seu clan não tem uma home definida").result(Result.FAILURE).build();
        }

        EasyTeleportHandler.enqueueTeleport(player, member.getClan().getHome(), 5,true, false);

        return CommandResult.builder().message("Home do clan atualizada.").result(Result.SUCCESS).build();
    }
}
