package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.GuildPromoteEvent;
import net.threader.guildplus.model.Guild;
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
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildDemoteCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(commandContext.getArgumentAs("player", String.class));

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(member.getOffice() != Office.LEADER) {
            return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("cargo_insuficiente", GuildPlus.instance())).build();
        }

        Guild guild = member.getGuild();

        Member demotedMember = Member.of(targetPlayer.getUniqueId()).get();

        switch(demotedMember.getOffice()) {
            case MEMBER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_ser_rebaixado", GuildPlus.instance())).result(Result.SUCCESS).build();
            case SUBLEADER:
                GuildPromoteEvent event = new GuildPromoteEvent(member, demotedMember);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    demotedMember.updateOffice(Office.MEMBER);
                    guild.broadcast(new Message.Builder().fromConfig("foi_rebaixado", GuildPlus.instance()).addVariable("player", demotedMember.getName()).build());
                }
                break;
            case LEADER:
                return CommandResult.builder().result(Result.FAILURE).message(Message.Util.of("nao_pode_rebaixar_vc_mesmo", GuildPlus.instance())).build();
        }
        return CommandResult.builder().build();
    }
}
