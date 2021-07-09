package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.InviteController;
import com.armacraft.clans.controller.instance.SingleInviteController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.utils.CommonVerifiers;
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
            player.sendMessage(Message.Util.of("ja_tem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        if(!inviteController.getInviter(player.getUniqueId()).isPresent()) {
            return CommandResult.builder().message(Message.Util.of("no_invites", ArmaClans.instance())).result(Result.FAILURE).build();
        }

        Clan clan = inviteController.getInviter(player.getUniqueId()).get();

        inviteController.removeInvitesOf(player.getUniqueId());

        clan.broadcast(new Message.Builder().fromConfig("recusou_clan", ArmaClans.instance()).addVariable("membro",  player.getName()).build());

        return CommandResult.builder().build();
    }
}