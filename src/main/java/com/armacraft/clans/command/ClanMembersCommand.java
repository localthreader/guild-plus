package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class ClanMembersCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Clan clan = Member.of(player.getUniqueId()).get().getClan();
        StringBuilder membros = new StringBuilder();

        Iterator<Member> iterator = clan.getMembers().iterator();
        if(iterator.hasNext()) {
            Member member = iterator.next();
            String color = member.getOfflinePlayer().isOnline() ? "&a" : "&7";
            membros.append(color + member.getOfflinePlayer().getName());
        }

        iterator.forEachRemaining(member -> {
            String color = member.getOfflinePlayer().isOnline() ? "&a" : "&7";
            membros.append("&f, " + color + member.getOfflinePlayer().getName());
        });

        new Message.Builder().fromConfig("clan_membros", ArmaClans.instance())
                .addVariable("membros", membros.toString())
                .build().send(player);

        return CommandResult.builder().build();
    }
}
