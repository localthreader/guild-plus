package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.post.PlayerPostLeaveGuildEvent;
import net.threader.guildplus.api.event.pre.PlayerPreLeaveGuildEvent;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildLeaveCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player leaving = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_GUILD.test(leaving.getUniqueId())) {
            leaving.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(leaving);
            return CommandResult.builder().build();
        }

        Member leavingMember = Member.of(leaving.getUniqueId()).get();
        Guild guild = leavingMember.getGuild();

        if(CommonVerifiers.HAS_OFFICE.test(leaving.getUniqueId(), Office.LEADER)) {
            new GuildDisbandCommand().execute(commandContext, strings);
            return CommandResult.builder().build();
        }

        PlayerPreLeaveGuildEvent preEvent = new PlayerPreLeaveGuildEvent(leaving, guild);
        Bukkit.getPluginManager().callEvent(preEvent);
        if(!preEvent.isCancelled()) {
            guild.broadcast(new Message.Builder().fromConfig("guild_leave", GuildPlus.instance()).addVariable("player", leaving.getName()).build());
            leavingMember.destroy();
            Bukkit.getPluginManager().callEvent(new PlayerPostLeaveGuildEvent(guild, leaving));
        }

        return CommandResult.builder().build();
    }
}
