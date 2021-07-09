package net.threader.guildplus.ui;

import net.arzio.simplegui.GUIItem;
import net.arzio.simplegui.SimpleGUI;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.enums.GuildStat;
import net.threader.guildplus.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GuildMenuGUI {
    public static SimpleGUI createFor(Guild guild, Player holder) {
        GUIItem[] items = new GUIItem[3];
        items[0] = new GUIItem(ItemStackBuilder.factory()
                .title("&7[" + guild.getTag()  + "&7] " + guild.getName())
                .lore("&eLíder: &6" + guild.getLeader().getName())
                .lore("&eRank: &6" + (guild.getRankPosition() != 0 ? "#" + guild.getRankPosition() : "Indefinido"))
                .lore("&ePontos: &6" + guild.getStat(GuildStat.POINTS))
                .lore("&eExperiência: &6" + guild.getStat(GuildStat.EXP) + "EXP")
                .lore("&eKDR: &6" + String.format("%.2f", guild.getKDR()))
                .lore("&eKills/Mortes: &6" + guild.getStat(GuildStat.KILL) + "K&e/&6" + guild.getStat(GuildStat.DEATH) + "D")
                .lore("&eMembros: &6" + guild.getMembers().size() + " membros")
                .lore("")
                .lore("&ePara mais informações, utilize /guild comandos")
                .type(Material.PAINTING)
                .build(), 13, (p, e) -> {});

        items[1] = new GUIItem(ItemStackBuilder.factory()
                .title("&7Membros")
                .lore("&fVisualize os jogadores da sua guilda!")
                .type(Material.PLAYER_HEAD)
                .build(), 14, (p,e) -> {
            MembersShowcaseUI.createFor(guild, holder).openInventory(holder);
        });

        items[2] = new GUIItem(ItemStackBuilder.factory()
                .title("&7Loja da Guilda")
                .lore("")
                .lore("&fAcessar loja da guilda!")
                .lore("&f(Em breve...)")
                .type(Material.DIAMOND)
                .build(), 12, (p,e) -> {});
        return new SimpleGUI(GuildPlus.instance(), holder, "§7[" + guild.getTag().replace("&", "§") + "§7] §8" + guild.getName(), SimpleGUI.Rows.THREE, items);
    }
}
