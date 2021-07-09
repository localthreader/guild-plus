package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.api.event.ClanChestEvent;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.utils.CommonVerifiers;
import net.armacraft.armachest.ArmaChestPlugin;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ClanLeaderChestCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {

        Player player = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        if(!CommonVerifiers.HAS_OFFICE.test(player.getUniqueId(), Office.SUBLEADER)) {
            player.sendMessage(Message.Util.of("cargo_insuficiente", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Clan clan = Member.of(player.getUniqueId()).get().getClan();

        ClanChestEvent event = new ClanChestEvent(Member.of(player.getUniqueId()).get(), true);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            Inventory chest = ArmaChestPlugin.instance().getVirtualChestManager().getChest(clan.getUniqueId().toString(), 2);
            player.openInventory(chest);
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);

            clan.broadcast(new Message.Builder().fromConfig("abriu_bau_lider", ArmaClans.instance()).addVariable("player", player.getName()).build());
        }

        return CommandResult.builder().build();
    }
}