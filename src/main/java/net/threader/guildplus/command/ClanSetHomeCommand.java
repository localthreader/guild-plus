package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threader.guildplus.utils.Cooldown;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;

public class ClanSetHomeCommand implements CommandRunner {

    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();

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
        if(Cooldown.CLAN_COOLDOWN.containsKey(clan.getUniqueId())) {
            return CommandResult.builder().message("Aguarde para realizar essa ação novamente.").result(Result.FAILURE).build();
        }

        if(!player.getWorld().getName().equalsIgnoreCase("plotme")) {
            return CommandResult.builder().message("Comando permitido apenas no mundo de plots.").result(Result.FAILURE).build();
        }

        clan.updateHome(player.getLocation());

        return CommandResult.builder().message("Home do clan atualizada.").result(Result.SUCCESS).build();
    }
}
