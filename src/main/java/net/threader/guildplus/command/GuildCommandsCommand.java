package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildCommandsCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        new Message.Builder().fromConfig("guild_commands", GuildPlus.instance()).build().send(player);
        return CommandResult.builder().build();
    }
}
