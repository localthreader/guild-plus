package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.api.event.ClanChatMessageEvent;
import com.armacraft.clans.controller.ConfirmationController;
import com.armacraft.clans.controller.instance.SingleConfirmationController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ClanChatCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        String message = commandContext.getArgumentAs("message", String.class);
        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();
        Clan clan = member.getClan();

        String office = "";
        switch(member.getOffice()) {
            case MEMBER:
                office = "&3MEMBRO";
                break;
            case SUBLEADER:
                office = "&eSUB-LÍDER";
                break;
            case LEADER:
                office = "&6LÍDER";
                break;
        }

        Message.Builder builder = new Message.Builder();

        if(member.getRank() != null && !member.getRank().equalsIgnoreCase("")) {
            builder.fromConfig("clan_chat_message_rank", ArmaClans.instance()).addVariable("rank", member.getRank());
        } else {
            builder.fromConfig("clan_chat_message", ArmaClans.instance());
        }

        builder.addVariable("office", office);
        builder.addVariable("nick", member.getName());
        builder.addVariable("message", message);

        ClanChatMessageEvent event = new ClanChatMessageEvent(builder.build(), member);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            clan.broadcast(builder.build());
        }

        return CommandResult.builder().build();
    }
}
