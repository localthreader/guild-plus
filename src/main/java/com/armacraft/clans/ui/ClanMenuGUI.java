package com.armacraft.clans.ui;

import com.armacraft.armalib.api.util.ItemStackBuilder;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.instance.SingleClanRankController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.enums.ClanStat;
import net.arzio.simplegui.GUIItem;
import net.arzio.simplegui.SimpleGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanMenuGUI {
    public static SimpleGUI createFor(Clan clan, Player holder) {
        GUIItem[] items = new GUIItem[3];
        items[0] = new GUIItem(ItemStackBuilder.factory()
                .title("&7[" + clan.getTag()  + "&7] " + clan.getName())
                .lore("&eLíder: &6" + clan.getLeader().getName())
                .lore("&eRank: &6" + (clan.getRankPosition() != 0 ? "#" + clan.getRankPosition() : "Indefinido"))
                .lore("&ePontos: &6" + clan.getStat(ClanStat.POINTS))
                .lore("&eExperiência: &6" + clan.getStat(ClanStat.EXP) + "EXP")
                .lore("&eKDR: &6" + String.format("%.2f", clan.getKDR()))
                .lore("&eKills/Mortes: &6" + clan.getStat(ClanStat.KILL) + "K&e/&6" + clan.getStat(ClanStat.DEATH) + "D")
                .lore("&eMembros: &6" + clan.getMembers().size() + " membros")
                .lore("")
                .lore("&ePara mais informações, utilize /clan comandos")
                .type(Material.PAINTING)
                .build(), 13, (p, e) -> {});

        items[1] = new GUIItem(ItemStackBuilder.factory()
                .title("&7Membros")
                .lore("&fVisualize os jogadores do seu clan!")
                .type(Material.PLAYER_HEAD)
                .build(), 14, (p,e) -> {
            MembersShowcaseUI.createFor(clan, holder).openInventory(holder);
        });

        items[2] = new GUIItem(ItemStackBuilder.factory()
                .title("&7Loja do Clan")
                .lore("")
                .lore("&fAcessar loja do clan!")
                .lore("&f(Em breve...)")
                .type(Material.DIAMOND)
                .build(), 12, (p,e) -> {});
        return new SimpleGUI(ArmaClans.instance(), holder, "§7[" + clan.getTag().replace("&", "§") + "§7] §8" + clan.getName(), SimpleGUI.Rows.THREE, items);
    }
}
