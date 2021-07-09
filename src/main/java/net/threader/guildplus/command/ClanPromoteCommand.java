package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.ClanPromoteEvent;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ClanPromoteCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        ConfirmationController confirmationController = SingleConfirmationController.INSTANCE;
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(commandContext.getArgumentAs("player", String.class));

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

        int maxSubLeaders = GuildPlus.instance().getConfig().getInt("clan.config.max_subleaders");
        if(maxSubLeaders > 0) {
            if(clan.getMembers().stream().filter(x -> x.getOffice() == Office.SUBLEADER).count() >= maxSubLeaders) {
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("max_sub_liders", GuildPlus.instance())).build();
            }
        }

        Member promotedMember = Member.of(targetPlayer.getUniqueId()).get();

        switch(promotedMember.getOffice()) {
            case Office.MEMBER:
                ClanPromoteEvent event = new ClanPromoteEvent(member, promotedMember);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    promotedMember.updateOffice(Office.SUBLEADER);
                    clan.broadcast(new Message.Builder().fromConfig("promoveu", GuildPlus.instance())
                            .addVariable("player", player.getName())
                            .addVariable("membro", promotedMember.getName())
                            .addVariable("cargo", "sub-líder").build());
                    return CommandResult.builder().result(Result.SUCCESS).build();
                }
                break;
            case Office.SUBLEADER:
                ClanPromoteEvent eventSub = new ClanPromoteEvent(member, promotedMember);
                Bukkit.getPluginManager().callEvent(eventSub);
                if(!eventSub.isCancelled()) {
                    if (confirmationController.getOwnershipTransferConfirmations().containsKey(clan)
                            || confirmationController.getDisbandConfirmations().contains(clan)) {
                        return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("ja_confirmacao", GuildPlus.instance())).build();
                    }

                    confirmationController.addOwnershipTransferConfirmation(clan, promotedMember.getUniqueId());

                    new Message.Builder().fromConfig("owner_ship_confirm", GuildPlus.instance()).build().send(player);
                }
                break;
            case Office.LEADER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_promover_vc_mesmo", GuildPlus.instance())).build();
        }
        return CommandResult.builder().build();
    }
}
