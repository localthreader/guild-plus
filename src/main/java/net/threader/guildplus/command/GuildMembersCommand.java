package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;

import java.util.Iterator;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildMembersCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Guild guild = Member.of(player.getUniqueId()).get().getGuild();
        StringBuilder membros = new StringBuilder();

        Iterator<Member> iterator = guild.getMembers().iterator();
        if(iterator.hasNext()) {
            Member member = iterator.next();
            String color = member.getOfflinePlayer().isOnline() ? "&a" : "&7";
            membros.append(color + member.getOfflinePlayer().getName());
        }

        iterator.forEachRemaining(member -> {
            String color = member.getOfflinePlayer().isOnline() ? "&a" : "&7";
            membros.append("&f, " + color + member.getOfflinePlayer().getName());
        });

        new Message.Builder().fromConfig("guild_membros", GuildPlus.instance())
                .addVariable("membros", membros.toString())
                .build().send(player);

        return CommandResult.builder().build();
    }
}
