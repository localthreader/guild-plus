package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.instance.SingleAllyRequestController;
import net.threader.guildplus.controller.instance.SingleGuildController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;

import java.util.Optional;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildAllyRequestCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String passedArg = commandContext.getArgumentAs("key", String.class);
        SingleAllyRequestController controller = SingleAllyRequestController.INSTANCE;

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(!CommonVerifiers.HAS_OFFICE.test(player.getUniqueId(), Office.LEADER)) {
            player.sendMessage(Message.Util.of("cargo_insuficiente", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Optional<Guild> foundGuild = SingleGuildController.Finder.byLeaderName(passedArg);
        if(!foundGuild.isPresent()) {
            foundGuild = SingleGuildController.Finder.byPlainTag(passedArg);
        }

        if(!foundGuild.isPresent()) {
            return CommandResult.builder().result(Result.FAILURE).message("Nenhuma guilda encontrado.").build();
        }

        Guild guild = foundGuild.get();

        if(member.getGuild().getAlliances().contains(guild)) {
            return CommandResult.builder().result(Result.FAILURE).message("Você já é aliado dessa guilda.").build();
        }

        if(!guild.getLeader().getOfflinePlayer().isOnline()) {
            return CommandResult.builder().result(Result.FAILURE).message("O Líder da outra guilda deve estar online.").build();
        }

        if(controller.getInviter(guild).isPresent()) {
            return CommandResult.builder().message("Essa guild já tem um pedido de aliança pendente.").result(Result.FAILURE).build();
        }

        controller.addInvite(member.getGuild(), guild);

        return CommandResult.builder().result(Result.SUCCESS).message("Pedido de aliança enviado!").build();
    }
}
