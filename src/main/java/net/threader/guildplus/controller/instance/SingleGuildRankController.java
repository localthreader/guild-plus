package net.threader.guildplus.controller.instance;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.GuildRankController;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum SingleGuildRankController implements GuildRankController {
    INSTANCE;

    private Map<UUID, Integer> cachedRank = new HashMap<>();

    @Override
    public void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(GuildPlus.instance(), () -> {
            try (ResultSet rs = GuildPlus.DATABASE_ACESSOR.getConnection().createStatement()
                    .executeQuery("SELECT unique_id FROM guilds ORDER BY experience DESC")) {
                int index = 1;
                while(rs.next()) {
                    cachedRank.put(UUID.fromString(rs.getString("unique_id")), index);
                    index++;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }, 20, 20*60*5);
    }

    @Override
    public Map<UUID, Integer> getCachedRank() {
        return cachedRank;
    }
}
