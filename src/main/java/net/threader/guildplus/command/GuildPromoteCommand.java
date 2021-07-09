package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.GuildPromoteEvent;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildPromoteCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        ConfirmationController confirmationController = SingleConfirmationController.INSTANCE;
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(commandContext.getArgumentAs("player", String.class));

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

        int maxSubLeaders = GuildPlus.instance().getConfig().getInt("guild.config.max_subleaders");
        if(maxSubLeaders > 0) {
            if(guild.getMembers().stream().filter(x -> x.getOffice() == Office.SUBLEADER).count() >= maxSubLeaders) {
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("max_sub_liders", GuildPlus.instance())).build();
            }
        }

        Member promotedMember = Member.of(targetPlayer.getUniqueId()).get();

        switch(promotedMember.getOffice()) {
            case MEMBER:
                GuildPromoteEvent event = new GuildPromoteEvent(member, promotedMember);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    promotedMember.updateOffice(Office.SUBLEADER);
                    guild.broadcast(new Message.Builder().fromConfig("promoveu", GuildPlus.instance())
                            .addVariable("player", player.getName())
                            .addVariable("membro", promotedMember.getName())
                            .addVariable("cargo", "sub-líder").build());
                    return CommandResult.builder().result(Result.SUCCESS).build();
                }
                break;
            case SUBLEADER:
                GuildPromoteEvent eventSub = new GuildPromoteEvent(member, promotedMember);
                Bukkit.getPluginManager().callEvent(eventSub);
                if(!eventSub.isCancelled()) {
                    if (confirmationController.getOwnershipTransferConfirmations().containsKey(guild)
                            || confirmationController.getDisbandConfirmations().contains(guild)) {
                        return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("ja_confirmacao", GuildPlus.instance())).build();
                    }

                    confirmationController.addOwnershipTransferConfirmation(guild, promotedMember.getUniqueId());

                    new Message.Builder().fromConfig("owner_ship_confirm", GuildPlus.instance()).build().send(player);
                }
                break;
            case LEADER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_promover_vc_mesmo", GuildPlus.instance())).build();
        }
        return CommandResult.builder().build();
    }
}
