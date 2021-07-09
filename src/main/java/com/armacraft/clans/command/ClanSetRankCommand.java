package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.ConfirmationController;
import com.armacraft.clans.controller.instance.SingleConfirmationController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ClanSetRankCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String rank = commandContext.getArgumentAs("rank", String.class);
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(commandContext.getArgumentAs("player", String.class));

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(!Member.of(targetPlayer.getUniqueId()).isPresent()) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_esta_clan", ArmaClans.instance())).build();
        }

        Member target = Member.of(targetPlayer.getUniqueId()).get();

        if(member.getOffice() != Office.MEMBER && target.getOffice().getId() <= member.getOffice().getId()) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("cargo_insuficiente", ArmaClans.instance())).build();
        }

        target.updateRank(rank);

        return CommandResult.builder().message("Rank atualizado com sucesso.").result(Result.SUCCESS).build();
    }
}
