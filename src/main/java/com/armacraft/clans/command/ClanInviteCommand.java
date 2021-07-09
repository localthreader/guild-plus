package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.api.event.ClanInviteEvent;
import com.armacraft.clans.controller.InviteController;
import com.armacraft.clans.controller.instance.SingleInviteController;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ClanInviteCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String invitedNick = commandContext.getArgumentAs("player", String.class);
        Player invited = Bukkit.getPlayer(invitedNick);
        InviteController inviteController = SingleInviteController.INSTANCE;
        FileConfiguration config = ArmaClans.instance().getConfig();

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        if(!CommonVerifiers.HAS_OFFICE.test(player.getUniqueId(), Office.SUBLEADER)) {
            player.sendMessage(Message.Util.of("cargo_insuficiente", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(Member.of(invited.getUniqueId()).isPresent()) {
            return CommandResult.builder().message(Message.Util.of("player_ja_tem_clan", ArmaClans.instance())).result(Result.FAILURE).build();
        }

        if(inviteController.getInvitesOf(member.getClan()).size() >= config.getInt("clan.invite.max_sync_invites")) {
            return CommandResult.builder().message(Message.Util.of("max_convites", ArmaClans.instance())).result(Result.FAILURE).build();
        }

        int maxPlayers = config.getInt("clan.config.max_players");
        if(maxPlayers != 0 && member.getClan().getMembers().size() >= maxPlayers) {
            return CommandResult.builder().message(Message.Util.of("max_players", ArmaClans.instance())).result(Result.FAILURE).build();
        }

        if(inviteController.getInviter(member.getUniqueId()).isPresent()) {
            return CommandResult.builder().message(Message.Util.of("ja_convidado", ArmaClans.instance())).result(Result.FAILURE).build();
        }

        ClanInviteEvent inviteEvent = new ClanInviteEvent(member.getClan(), invited, member);
        Bukkit.getPluginManager().callEvent(inviteEvent);
        if(!inviteEvent.isCancelled()) {
            inviteController.addInvite(member.getClan(), invited);

            member.getClan().broadcast(new Message.Builder().fromConfig("convidou_player", ArmaClans.instance())
                    .addVariable("membro", member.getName())
                    .addVariable("player", invited.getName()).build());
        }

        return CommandResult.builder().build();
    }
}
