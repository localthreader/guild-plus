package net.threader.guildplus.utils;

import com.armacraft.anticombatlog.ACL;
import net.threader.guildplus.GuildPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EasyTeleportHandler implements Listener {
    private static final Map<UUID, TeleportRepresentation> ENQUEUED_TELEPORTS = new HashMap<>();

    public static void enqueueTeleport(Player player, Location to, int delay, boolean cancelOnPvp, boolean allowWhileInCombat) {
        if(ENQUEUED_TELEPORTS.containsKey(player.getUniqueId())) {
            player.sendMessage("§cVocê já tem um teleporte pendente. Aguarde.");
            return;
        }
        if(!allowWhileInCombat && ACL.isInCombat(player)) {
            player.sendMessage("§cVocê não pode teleportar durante combate.");
            return;
        }
        TeleportRepresentation representation = new TeleportRepresentation(player, to, delay, cancelOnPvp);
        ENQUEUED_TELEPORTS.put(player.getUniqueId(), representation);
        representation.start();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            if(ENQUEUED_TELEPORTS.containsKey(event.getEntity().getUniqueId())) {
                TeleportRepresentation tp = ENQUEUED_TELEPORTS.get(event.getEntity().getUniqueId());
                if(tp.cancelOnPvp) {
                    tp.player.sendMessage("§cVocê tomou dano e seu teleporte foi cancelado.");
                    ENQUEUED_TELEPORTS.remove(event.getEntity().getUniqueId());
                }
            }
        }
    }

    public static class TeleportRepresentation {
        private Player player;
        private Location destination;
        private int delay;
        private boolean cancelOnPvp;
        private BukkitTask task;

        public TeleportRepresentation(Player player, Location destination, int delay, boolean cancelOnPvp) {
            this.player = player;
            this.destination = destination;
            this.delay = delay;
        }

        public void start() {
            player.sendMessage("&eTeleportando em " + delay + " segundos");
            this.task = Bukkit.getScheduler().runTaskLater(GuildPlus.instance(), () -> {
                if(ENQUEUED_TELEPORTS.containsKey(player.getUniqueId())) {
                    ENQUEUED_TELEPORTS.remove(player.getUniqueId());
                    player.teleport(destination);
                    player.sendMessage("§aTeleportando...");
                    SoundPlayer.TELEPORT_SOUND.accept(player);
                }
            }, delay*20l);
        }

    }
}
