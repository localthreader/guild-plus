package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.instance.SingleAllyRequestController;
import net.threader.guildplus.controller.instance.SingleClanController;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ClanAllyRequestCommand implements CommandRunner {
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

        Optional<Clan> foundClan = SingleClanController.Finder.byLeaderName(passedArg);
        if(!foundClan.isPresent()) {
            foundClan = SingleClanController.Finder.byPlainTag(passedArg);
        }

        if(!foundClan.isPresent()) {
            return CommandResult.builder().result(Result.FAILURE).message("Nenhum clan encontrado.").build();
        }

        Clan clan = foundClan.get();

        if(member.getClan().getAlliances().contains(clan)) {
            return CommandResult.builder().result(Result.FAILURE).message("Você já é aliado desse clan.").build();
        }

        if(!clan.getLeader().getOfflinePlayer().isOnline()) {
            return CommandResult.builder().result(Result.FAILURE).message("O Líder do outro clan deve estar online.").build();
        }

        if(controller.getInviter(clan).isPresent()) {
            return CommandResult.builder().message("Esse clan já tem um pedido de aliança pendente.").result(Result.FAILURE).build();
        }

        controller.addInvite(member.getClan(), clan);

        return CommandResult.builder().result(Result.SUCCESS).message("Pedido de aliança enviado!").build();
    }
}
