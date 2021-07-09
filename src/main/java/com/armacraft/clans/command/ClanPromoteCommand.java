package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.api.event.ClanPromoteEvent;
import com.armacraft.clans.controller.ConfirmationController;
import com.armacraft.clans.controller.instance.SingleConfirmationController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.utils.CommonVerifiers;
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
            player.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(member.getOffice() != Office.LEADER) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("cargo_insuficiente", ArmaClans.instance())).build();
        }

        Clan clan = member.getClan();

        int maxSubLeaders = ArmaClans.instance().getConfig().getInt("clan.config.max_subleaders");
        if(maxSubLeaders > 0) {
            if(clan.getMembers().stream().filter(x -> x.getOffice() == Office.SUBLEADER).count() >= maxSubLeaders) {
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("max_sub_liders", ArmaClans.instance())).build();
            }
        }

        Member promotedMember = Member.of(targetPlayer.getUniqueId()).get();

        switch(promotedMember.getOffice()) {
            case MEMBER:
                ClanPromoteEvent event = new ClanPromoteEvent(member, promotedMember);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    promotedMember.updateOffice(Office.SUBLEADER);
                    clan.broadcast(new Message.Builder().fromConfig("promoveu", ArmaClans.instance())
                            .addVariable("player", player.getName())
                            .addVariable("membro", promotedMember.getName())
                            .addVariable("cargo", "sub-líder").build());
                    return CommandResult.builder().result(Result.SUCCESS).build();
                }
                break;
            case SUBLEADER:
                ClanPromoteEvent eventSub = new ClanPromoteEvent(member, promotedMember);
                Bukkit.getPluginManager().callEvent(eventSub);
                if(!eventSub.isCancelled()) {
                    if (confirmationController.getOwnershipTransferConfirmations().containsKey(clan)
                            || confirmationController.getDisbandConfirmations().contains(clan)) {
                        return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("ja_confirmacao", ArmaClans.instance())).build();
                    }

                    confirmationController.addOwnershipTransferConfirmation(clan, promotedMember.getUniqueId());

                    new Message.Builder().fromConfig("owner_ship_confirm", ArmaClans.instance()).build().send(player);
                }
                break;
            case LEADER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_promover_vc_mesmo", ArmaClans.instance())).build();
        }
        return CommandResult.builder().build();
    }
}
