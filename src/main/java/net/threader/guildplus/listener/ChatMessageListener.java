package net.threader.guildplus.listener;

import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;
import net.threader.guildplus.model.Member;
import net.md_5.bungee.api.ChatColor;
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
                event.addTag("{clan-tag}", "§7<" + ChatColor.RESET + member.getClan().getTag() + "§7>");
                break;
            case SUBLEADER:
                event.addTag("{clan-tag}", "§6<" + ChatColor.RESET + member.getClan().getTag() + "§6>");
                break;
            case LEADER:
                event.addTag("{clan-tag}", "§b<" + ChatColor.RESET + member.getClan().getTag() + "§b>");
                break;
        }

    }
}
