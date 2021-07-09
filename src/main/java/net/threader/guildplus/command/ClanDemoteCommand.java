package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.ClanPromoteEvent;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ClanDemoteCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(commandContext.getArgumentAs("player", String.class));

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(member.getOffice() != Office.LEADER) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("cargo_insuficiente", GuildPlus.instance())).build();
        }

        Clan clan = member.getClan();

        Member demotedMember = Member.of(targetPlayer.getUniqueId()).get();

        switch(demotedMember.getOffice()) {
            case Office.MEMBER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_ser_rebaixado", GuildPlus.instance())).result(Result.SUCCESS).build();
            case Office.SUBLEADER:
                ClanPromoteEvent event = new ClanPromoteEvent(member, demotedMember);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    demotedMember.updateOffice(Office.MEMBER);
                    clan.broadcast(new Message.Builder().fromConfig("foi_rebaixado", GuildPlus.instance()).addVariable("player", demotedMember.getName()).build());
                }
                break;
            case Office.LEADER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_rebaixar_vc_mesmo", GuildPlus.instance())).build();
        }
        return CommandResult.builder().build();
    }
}
