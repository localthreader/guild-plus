package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.InviteController;
import net.threader.guildplus.controller.instance.SingleInviteController;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;

public class ClanDeclineCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        InviteController inviteController = SingleInviteController.INSTANCE;

        if(CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("ja_tem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        if(!inviteController.getInviter(player.getUniqueId()).isPresent()) {
            return CommandResult.builder().message(Message.Util.of("no_invites", GuildPlus.instance())).result(Result.FAILURE).build();
        }

        Clan clan = inviteController.getInviter(player.getUniqueId()).get();

        inviteController.removeInvitesOf(player.getUniqueId());

        clan.broadcast(new Message.Builder().fromConfig("recusou_clan", GuildPlus.instance()).addVariable("membro",  player.getName()).build());

        return CommandResult.builder().build();
    }
}
