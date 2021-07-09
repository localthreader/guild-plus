package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.ui.GuildMenuGUI;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        if(CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            Guild guild = Member.of(player.getUniqueId()).get().getGuild();
            GuildMenuGUI.createFor(guild, player).openInventory(player);
            return CommandResult.builder().build();
        }

        new Message.Builder().fromConfig("no_guild_lookup", GuildPlus.instance()).build().send(player);

        return CommandResult.builder().build();
    }
}
