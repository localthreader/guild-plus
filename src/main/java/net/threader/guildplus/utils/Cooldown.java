package net.threader.guildplus.utils;

import com.armacraft.armalib.ArmaLib;
import net.threader.guildplus.model.Clan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {
    public enum Type {
        CLAN_CREATE, SETHOME, CHANGE_SOMETHING;
    }

    public static final Map<UUID, Type> CLAN_COOLDOWN = new HashMap<>();
    public static final Map<UUID, Type> PLAYER_COOLDOWN = new HashMap<>();

    public static void putInCooldown(Object object, Type type, int delay) {
        if (object instanceof Clan) {
            UUID uid = ((Clan) object).getUniqueId();
            CLAN_COOLDOWN.remove(uid);
            CLAN_COOLDOWN.put(uid, type);
            Bukkit.getScheduler().runTaskLater(ArmaLib.instance(), () -> {
                if(CLAN_COOLDOWN.containsKey(uid)) {
                    CLAN_COOLDOWN.remove(uid);
                }
            }, delay*20l);
        }
        if (object instanceof Player) {
            UUID uid = ((Player) object).getUniqueId();
            PLAYER_COOLDOWN.remove(uid);
            PLAYER_COOLDOWN.put(uid, type);
            Bukkit.getScheduler().runTaskLater(ArmaLib.instance(), () -> {
                if(PLAYER_COOLDOWN.containsKey(uid)) {
                    PLAYER_COOLDOWN.remove(uid);
                }
            }, delay*20l);
        }
    }
}