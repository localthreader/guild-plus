package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.post.GuildPostDisbandEvent;
import net.threader.guildplus.api.event.pre.GuildPreDisbandEvent;
import net.threader.guildplus.controller.ConfirmationController;
import net.threader.guildplus.controller.instance.SingleConfirmationController;
import net.threader.guildplus.model.Guild;
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
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildConfirmCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        ConfirmationController controller = SingleConfirmationController.INSTANCE;

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

        if(!controller.getDisbandConfirmations().contains(guild)
            && !controller.getOwnershipTransferConfirmations().containsKey(guild)) {
            return CommandResult.builder().message(Message.Util.of("sem_confirmacao", GuildPlus.instance())).result(Result.FAILURE).build();
        }

        if(controller.getOwnershipTransferConfirmations().containsKey(guild)) {
            UUID passingTo = controller.getOwnershipTransferConfirmations().get(guild);
            Member passingToMember = Member.of(passingTo).get();
            passingToMember.updateOffice(Office.LEADER);
            member.updateOffice(Office.SUBLEADER);
            guild.broadcast(new Message.Builder().fromConfig("transferiu", GuildPlus.instance())
                .addVariable("player", member.getName())
                .addVariable("membro", passingToMember.getName()).build());
            return CommandResult.builder().message(Message.Util.of("lideranca_transferida", GuildPlus.instance())).result(Result.SUCCESS).build();
        }

        if(controller.getDisbandConfirmations().contains(guild)) {
            GuildPreDisbandEvent preEvent = new GuildPreDisbandEvent(player, guild);
            Bukkit.getPluginManager().callEvent(preEvent);
            if(!preEvent.isCancelled()) {
                Set<Member> affectedPlayers = new HashSet<>(guild.getMembers());
                guild.getAlliances().forEach(ally -> affectedPlayers.addAll(ally.getMembers()));
                GuildPostDisbandEvent postEvent = new GuildPostDisbandEvent(player, affectedPlayers.stream().filter(m -> m.getOfflinePlayer().isOnline()).map(m -> m.getOfflinePlayer().getPlayer()).collect(Collectors.toSet()));
                guild.broadcast(new Message.Builder().fromConfig("disband", GuildPlus.instance()).build());
                guild.destroy();
                Bukkit.getPluginManager().callEvent(postEvent);
                return CommandResult.builder().message("Guilda desfeita com sucesso.").result(Result.SUCCESS).build();
            }
        }

        return CommandResult.builder().build();
    }
}
