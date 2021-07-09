package net.threader.guildplus.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class SoundPlayer {
    public static final Consumer<Player> ERROR_SOUND =
            (player) -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);

    public static final Consumer<Player> SUCCESS_SOUND =
            (player) -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

    public static final Consumer<Player> LEVEL_UP_SOUND =
            (player) -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

    public static final Consumer<Player> TELEPORT_SOUND =
            (player) -> player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, 1.0f, 1.0f);
}
