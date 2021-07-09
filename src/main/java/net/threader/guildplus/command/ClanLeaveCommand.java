package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.post.PlayerPostLeaveClanEvent;
import net.threader.guildplus.api.event.pre.PlayerPreLeaveClanEvent;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClanLeaveCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player leaving = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_CLAN.test(leaving.getUniqueId())) {
            leaving.sendMessage(Message.Util.of("sem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(leaving);
            return CommandResult.builder().build();
        }

        Member leavingMember = Member.of(leaving.getUniqueId()).get();
        Clan clan = leavingMember.getClan();

        if(CommonVerifiers.HAS_OFFICE.test(leaving.getUniqueId(), Office.LEADER)) {
            new ClanDisbandCommand().execute(commandContext, strings);
            return CommandResult.builder().build();
        }

        PlayerPreLeaveClanEvent preEvent = new PlayerPreLeaveClanEvent(leaving, clan);
        Bukkit.getPluginManager().callEvent(preEvent);
        if(!preEvent.isCancelled()) {
            clan.broadcast(new Message.Builder().fromConfig("clan_leave", GuildPlus.instance()).addVariable("player", leaving.getName()).build());
            leavingMember.destroy();
            Bukkit.getPluginManager().callEvent(new PlayerPostLeaveClanEvent(clan, leaving));
        }

        return CommandResult.builder().build();
    }
}
