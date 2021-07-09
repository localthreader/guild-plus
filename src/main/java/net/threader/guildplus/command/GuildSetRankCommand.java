package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildSetRankCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String rank = commandContext.getArgumentAs("rank", String.class);
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(commandContext.getArgumentAs("player", String.class));

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(!Member.of(targetPlayer.getUniqueId()).isPresent()) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_esta_guild", GuildPlus.instance())).build();
        }

        Member target = Member.of(targetPlayer.getUniqueId()).get();

        if(member.getOffice() != Office.MEMBER && target.getOffice().getId() <= member.getOffice().getId()) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("cargo_insuficiente", GuildPlus.instance())).build();
        }

        target.updateRank(rank);

        return CommandResult.builder().message("Rank atualizado com sucesso.").result(Result.SUCCESS).build();
    }
}
