package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threader.guildplus.utils.Cooldown;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildSetHomeCommand implements CommandRunner {

    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(member.getOffice() != Office.LEADER) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("cargo_insuficiente", GuildPlus.instance())).build();
        }

        Guild guild = member.getGuild();
        if(Cooldown.GUILD_COOLDOWN.containsKey(guild.getUniqueId())) {
            return CommandResult.builder().message("Aguarde para realizar essa ação novamente.").result(Result.FAILURE).build();
        }

        if(!player.getWorld().getName().equalsIgnoreCase("plotme")) {
            return CommandResult.builder().message("Comando permitido apenas no mundo de plots.").result(Result.FAILURE).build();
        }

        guild.updateHome(player.getLocation());

        return CommandResult.builder().message("Home da guild atualizada.").result(Result.SUCCESS).build();
    }
}
