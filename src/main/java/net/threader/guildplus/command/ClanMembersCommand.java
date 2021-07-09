package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.utils.CommonVerifiers;
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
            player.sendMessage(Message.Util.of("sem_clan", GuildPlus.instance()));
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

        new Message.Builder().fromConfig("clan_membros", GuildPlus.instance())
                .addVariable("membros", membros.toString())
                .build().send(player);

        return CommandResult.builder().build();
    }
}
