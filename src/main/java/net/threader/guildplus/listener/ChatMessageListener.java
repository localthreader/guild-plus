package net.threader.guildplus.listener;

import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;
import net.md_5.bungee.api.ChatColor;
import net.threader.guildplus.model.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatMessageListener implements Listener {
    @EventHandler
    public void onMessage(SendChannelMessageEvent event) {
        if(!(event.getSender() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getSender();
        if(!Member.of(player.getUniqueId()).isPresent()) {
            return;
        }

        Member member = Member.of(player.getUniqueId()).get();

        switch(member.getOffice()) {
            case MEMBER:
                event.addTag("{guild-tag}", "§7<" + ChatColor.RESET + member.getGuild().getTag() + "§7>");
                break;
            case SUBLEADER:
                event.addTag("{guild-tag}", "§6<" + ChatColor.RESET + member.getGuild().getTag() + "§6>");
                break;
            case LEADER:
                event.addTag("{guild-tag}", "§b<" + ChatColor.RESET + member.getGuild().getTag() + "§b>");
                break;
        }

    }
}
