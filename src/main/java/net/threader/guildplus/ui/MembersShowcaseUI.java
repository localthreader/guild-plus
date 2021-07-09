package net.threader.guildplus.ui;

import net.arzio.simplegui.GUIItem;
import net.arzio.simplegui.SimpleGUI;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MembersShowcaseUI {
    public static SimpleGUI createFor(Guild guild, Player holder) {
        GUIItem[] items = new GUIItem[guild.getMembers().size()];
        AtomicInteger arrayIndex = new AtomicInteger(0);
        AtomicInteger invIndex = new AtomicInteger(0);
        items[arrayIndex.getAndIncrement()] = new GUIItem(getSkull(guild.getLeader()), invIndex.getAndIncrement(), (p, e) -> {});
        guild.getMembers().stream().filter(x -> x.getOffice() == Office.SUBLEADER).forEach(member ->
            items[arrayIndex.getAndIncrement()] = new GUIItem(getSkull(member), invIndex.getAndIncrement(), (p,e) -> {})
        );
        guild.getMembers().stream().filter(x -> x.getOffice() == Office.MEMBER).forEach(member ->
            items[arrayIndex.getAndIncrement()] = new GUIItem(getSkull(member), invIndex.getAndIncrement(), (p,e) -> {})
        );
        return new SimpleGUI(GuildPlus.instance(), holder,"§7[" + guild.getTag().replace("&", "§") + "§7] §8" + guild.getName(),getRows(guild), items);
    }

    private static SimpleGUI.Rows getRows(Guild guild) {
        if(guild.getMembers().size() <= 9) {
            return SimpleGUI.Rows.ONE;
        }
        if(guild.getMembers().size() <= 18) {
            return SimpleGUI.Rows.TWO;
        }
        if(guild.getMembers().size() <= 27) {
            return SimpleGUI.Rows.THREE;
        }
        if(guild.getMembers().size() <= 36) {
            return SimpleGUI.Rows.FOUR;
        }
        if(guild.getMembers().size() <= 45) {
            return SimpleGUI.Rows.FIVE;
        }
        return SimpleGUI.Rows.SIX;
    }

    private static ItemStack getSkull(Member p) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(p.getUniqueId());
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = skull.getItemMeta();
        List<String> lore = new ArrayList<>();
        switch(p.getOffice()) {
            case MEMBER:
                lore.add("§7Cargo: §fMEMBRO");
                break;
            case LEADER:
                lore.add("§7Cargo: §bLÍDER");
                break;
            case SUBLEADER:
                lore.add("§7Cargo: §eSUB-LÍDER");
                break;
        }
        if(player.isOnline()) {
            meta.setDisplayName("§a" + p.getName());
        } else {
            meta.setDisplayName("§c" + p.getName());
        }
        meta.setLore(lore);
        skull.setItemMeta(meta);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
