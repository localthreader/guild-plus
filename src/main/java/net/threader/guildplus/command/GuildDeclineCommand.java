package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.InviteController;
import net.threader.guildplus.controller.instance.SingleInviteController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildDeclineCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        InviteController inviteController = SingleInviteController.INSTANCE;

        if(CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("ja_tem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        if(!inviteController.getInviter(player.getUniqueId()).isPresent()) {
            return CommandResult.builder().message(Message.Util.of("no_invites", GuildPlus.instance())).result(Result.FAILURE).build();
        }

        Guild guild = inviteController.getInviter(player.getUniqueId()).get();

        inviteController.removeInvitesOf(player.getUniqueId());

        guild.broadcast(new Message.Builder().fromConfig("recusou_guild", GuildPlus.instance()).addVariable("membro",  player.getName()).build());

        return CommandResult.builder().build();
    }
}
