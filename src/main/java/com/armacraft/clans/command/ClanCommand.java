package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.ClanStat;
import com.armacraft.clans.ui.ClanMenuGUI;
import com.armacraft.clans.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.entity.Player;

public class ClanCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        if(CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            Clan clan = Member.of(player.getUniqueId()).get().getClan();
            /*new Message.Builder().fromConfig("clan_lookup")
                    .addVariable("tag", clan.getTag())
                    .addVariable("nome", clan.getName())
                    .addVariable("lider", clan.getLeader().getName())
                    .addVariable("pontos", clan.getStat(ClanStat.POINTS))
                    .addVariable("exp", clan.getStat(ClanStat.EXP))
                    .addVariable("membros_size", clan.getMembers().size())
                    .addVariable("kills", clan.getStat(ClanStat.KILL))
                    .addVariable("mortes", clan.getStat(ClanStat.DEATH))
                    .addVariable("kdr", String.format("%.2f", clan.getKDR()))
                    .build().send(player);*/

            ClanMenuGUI.createFor(clan, player).openInventory(player);
            return CommandResult.builder().build();
        }

        new Message.Builder().fromConfig("no_clan_lookup", ArmaClans.instance()).build().send(player);

        return CommandResult.builder().build();
    }
}
