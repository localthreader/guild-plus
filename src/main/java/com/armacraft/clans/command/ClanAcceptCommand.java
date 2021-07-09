package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.api.event.post.PlayerPostJoinClanEvent;
import com.armacraft.clans.api.event.pre.PlayerPreJoinClanEvent;
import com.armacraft.clans.controller.InviteController;
import com.armacraft.clans.controller.instance.SingleInviteController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.utils.CommonVerifiers;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClanAcceptCommand implements CommandRunner {
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
        PlayerPreJoinClanEvent event = new PlayerPreJoinClanEvent(player, clan);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            clan.registerMember(player, Office.MEMBER);
            PlayerPostJoinClanEvent eventPost = new PlayerPostJoinClanEvent(clan, player);
            Bukkit.getPluginManager().callEvent(eventPost);
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b" + player.getName() + " ingressou no clan " + clan.getTag()));

        return CommandResult.builder().build();
    }
}
