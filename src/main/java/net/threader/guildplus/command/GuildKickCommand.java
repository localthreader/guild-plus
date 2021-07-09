package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.post.GuildPostKickEvent;
import net.threader.guildplus.api.event.pre.GuildPreKickEvent;
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

public class GuildKickCommand implements CommandRunner {

    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        String kickedNick = commandContext.getArgumentAs("player", String.class);
        OfflinePlayer kicked = Bukkit.getOfflinePlayer(kickedNick);
        Player kicking = (Player) commandContext.getSender();

        if(!CommonVerifiers.HAS_GUILD.test(kicking.getUniqueId())) {
            kicking.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(kicking);
            return CommandResult.builder().build();
        }

        Member kickingMember = Member.of(kicking.getUniqueId()).get();

        if(!CommonVerifiers.SAME_GUILD.test(kicking.getUniqueId(), kicked.getUniqueId())) {
            return CommandResult.builder().result(Result.FAILURE)
                    .message(Message.Util.of("nao_mesmo_guild", GuildPlus.instance())).build();
        }

        Member beingKicked = Member.of(kicked.getUniqueId()).get();

        if(!CommonVerifiers.HAS_OFFICE.test(kicking.getUniqueId(), Office.SUBLEADER)
                || beingKicked.getOffice().getId() >= kickingMember.getOffice().getId()) {
            return CommandResult.builder().result(Result.FAILURE)
                    .message(Message.Util.of("cargo_insuficiente", GuildPlus.instance())).build();
        }

        Guild guild = kickingMember.getGuild();
        GuildPreKickEvent preKickEvent = new GuildPreKickEvent(guild, beingKicked, kickingMember);
        Bukkit.getPluginManager().callEvent(preKickEvent);
        if(!preKickEvent.isCancelled()) {
            OfflinePlayer kickedOfflinePlayer = Bukkit.getOfflinePlayer(beingKicked.getUniqueId());
            GuildPostKickEvent postKickEvent = new GuildPostKickEvent(beingKicked, kickedOfflinePlayer);
            Bukkit.getPluginManager().callEvent(postKickEvent);
            beingKicked.destroy();
            guild.broadcast(new Message.Builder().fromConfig("player_kickado", GuildPlus.instance())
                    .addVariable("membro", kickingMember.getName())
                    .addVariable("player", beingKicked.getName()).build());
            if(kickedOfflinePlayer.isOnline()) {
                new Message.Builder().fromConfig("foi_kickado", GuildPlus.instance()).build().send((Player)kickedOfflinePlayer);
            }
        }

        return CommandResult.builder().build();
    }
}
