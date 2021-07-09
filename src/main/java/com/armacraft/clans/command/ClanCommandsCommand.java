package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;

public class ClanCommandsCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        new Message.Builder().fromConfig("clan_commands", ArmaClans.instance()).build().send(player);
        return CommandResult.builder().build();
    }
}
