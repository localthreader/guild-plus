package net.threader.guildplus.utils;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Guild;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {
    public enum Type {
        GUILD_CREATE, SETHOME, CHANGE_SOMETHING;
    }

    public static final Map<UUID, Type> GUILD_COOLDOWN = new HashMap<>();
    public static final Map<UUID, Type> PLAYER_COOLDOWN = new HashMap<>();

    public static void putInCooldown(Object object, Type type, int delay) {
        if (object instanceof Guild) {
            UUID uid = ((Guild) object).getUniqueId();
            GUILD_COOLDOWN.remove(uid);
            GUILD_COOLDOWN.put(uid, type);
            Bukkit.getScheduler().runTaskLater(GuildPlus.instance(), () -> {
                if(GUILD_COOLDOWN.containsKey(uid)) {
                    GUILD_COOLDOWN.remove(uid);
                }
            }, delay*20l);
        }
        if (object instanceof Player) {
            UUID uid = ((Player) object).getUniqueId();
            PLAYER_COOLDOWN.remove(uid);
            PLAYER_COOLDOWN.put(uid, type);
            Bukkit.getScheduler().runTaskLater(GuildPlus.instance(), () -> {
                if(PLAYER_COOLDOWN.containsKey(uid)) {
                    PLAYER_COOLDOWN.remove(uid);
                }
            }, delay*20l);
        }
    }
}