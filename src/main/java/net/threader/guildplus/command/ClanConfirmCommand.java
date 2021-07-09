package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.post.ClanPostDisbandEvent;
import net.threader.guildplus.api.event.pre.ClanPreDisbandEvent;
import net.threader.guildplus.controller.ConfirmationController;
import net.threader.guildplus.controller.instance.SingleConfirmationController;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClanConfirmCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        ConfirmationController controller = SingleConfirmationController.INSTANCE;

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(member.getOffice() != Office.LEADER) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("cargo_insuficiente", GuildPlus.instance())).build();
        }

        Clan clan = member.getClan();

        if(!controller.getDisbandConfirmations().contains(clan)
            && !controller.getOwnershipTransferConfirmations().containsKey(clan)) {
            return CommandResult.builder().message(Message.Util.of("sem_confirmacao", GuildPlus.instance())).result(Result.FAILURE).build();
        }

        if(controller.getOwnershipTransferConfirmations().containsKey(clan)) {
            UUID passingTo = controller.getOwnershipTransferConfirmations().get(clan);
            Member passingToMember = Member.of(passingTo).get();
            passingToMember.updateOffice(Office.LEADER);
            member.updateOffice(Office.SUBLEADER);
            clan.broadcast(new Message.Builder().fromConfig("transferiu", GuildPlus.instance())
                .addVariable("player", member.getName())
                .addVariable("membro", passingToMember.getName()).build());
            return CommandResult.builder().message(Message.Util.of("lideranca_transferida", GuildPlus.instance())).result(Result.SUCCESS).build();
        }

        if(controller.getDisbandConfirmations().contains(clan)) {
            ClanPreDisbandEvent preEvent = new ClanPreDisbandEvent(player, clan);
            Bukkit.getPluginManager().callEvent(preEvent);
            if(!preEvent.isCancelled()) {
                Set<Member> affectedPlayers = new HashSet<>(clan.getMembers());
                clan.getAlliances().forEach(ally -> affectedPlayers.addAll(ally.getMembers()));
                ClanPostDisbandEvent postEvent = new ClanPostDisbandEvent(player, affectedPlayers.stream().filter(m -> m.getOfflinePlayer().isOnline()).map(m -> m.getOfflinePlayer().getPlayer()).collect(Collectors.toSet()));
                clan.broadcast(new Message.Builder().fromConfig("disband", GuildPlus.instance()).build());
                clan.destroy();
                Bukkit.getPluginManager().callEvent(postEvent);
                return CommandResult.builder().message("Clan desfeito com sucesso.").result(Result.SUCCESS).build();
            }
        }

        return CommandResult.builder().build();
    }
}
