package net.threader.guildplus.command;

import net.armacraft.armachest.ArmaChestPlugin;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.GuildChestEvent;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildChestCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {

        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Guild guild = Member.of(player.getUniqueId()).get().getGuild();

        GuildChestEvent event = new GuildChestEvent(Member.of(player.getUniqueId()).get(), false);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            Inventory chest = ArmaChestPlugin.instance().getVirtualChestManager().getChest(guild.getUniqueId().toString(), 1);
            player.openInventory(chest);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);

            guild.broadcast(new Message.Builder().fromConfig("abriu_bau", GuildPlus.instance()).addVariable("player", player.getName()).build());
        }

        return CommandResult.builder().build();
    }
}
