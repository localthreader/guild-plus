package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.instance.SingleAllyRequestController;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;

public class ClanAllyAcceptCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String passedArg = commandContext.getArgumentAs("key", String.class);
        SingleAllyRequestController controller = SingleAllyRequestController.INSTANCE;

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(!CommonVerifiers.HAS_OFFICE.test(player.getUniqueId(), Office.LEADER)) {
            player.sendMessage(Message.Util.of("cargo_insuficiente", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        if(!controller.getInviter(member.getClan()).isPresent()) {
            return CommandResult.builder().message("Nenhum clan te enviou pedido de aliança.").result(Result.FAILURE).build();
        }

        Clan inviter = controller.getInviter(member.getClan()).get();

        inviter.registerAlly(member.getClan());
        member.getClan().registerAlly(inviter);

        inviter.broadcast(new Message.Builder().fromConfig("alianca_estabelecida", GuildPlus.instance())
                .addVariable("clan_tag", member.getClan().getTag()).build());

        member.getClan().broadcast(new Message.Builder().fromConfig("alianca_estabelecida", GuildPlus.instance())
                .addVariable("clan_tag", inviter.getTag()).build());

        return CommandResult.builder().result(Result.SUCCESS).build();
    }
}
