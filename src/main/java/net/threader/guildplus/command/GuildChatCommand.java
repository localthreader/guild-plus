package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.GuildChatMessageEvent;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildChatCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        String message = commandContext.getArgumentAs("message", String.class);
        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();
        Guild guild = member.getGuild();

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
            builder.fromConfig("guild_chat_message_rank", GuildPlus.instance()).addVariable("rank", member.getRank());
        } else {
            builder.fromConfig("guild_chat_message", GuildPlus.instance());
        }

        builder.addVariable("office", office);
        builder.addVariable("nick", member.getName());
        builder.addVariable("message", message);

        GuildChatMessageEvent event = new GuildChatMessageEvent(builder.build(), member);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            guild.broadcast(builder.build());
        }

        return CommandResult.builder().build();
    }
}
