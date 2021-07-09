package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.instance.SingleAllyRequestController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildAllyAcceptCommand implements CommandRunner {
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

        if(!controller.getInviter(member.getGuild()).isPresent()) {
            return CommandResult.builder().message("Nenhuma guilda te enviou pedido de aliança.").result(Result.FAILURE).build();
        }

        Guild inviter = controller.getInviter(member.getGuild()).get();

        inviter.registerAlly(member.getGuild());
        member.getGuild().registerAlly(inviter);

        inviter.broadcast(new Message.Builder().fromConfig("alianca_estabelecida", GuildPlus.instance())
                .addVariable("guild_tag", member.getGuild().getTag()).build());

        member.getGuild().broadcast(new Message.Builder().fromConfig("alianca_estabelecida", GuildPlus.instance())
                .addVariable("guild_tag", inviter.getTag()).build());

        return CommandResult.builder().result(Result.SUCCESS).build();
    }
}
