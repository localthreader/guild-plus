package com.armacraft.clans.controller.instance;

import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.ClanRankController;
import com.armacraft.clans.model.Clan;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public enum SingleClanRankController implements ClanRankController {
    INSTANCE;

    private Map<UUID, Integer> cachedRank = new HashMap<>();

    @Override
    public void startUpdateTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ArmaClans.instance(), () -> {
            try (ResultSet rs = ArmaClans.DATABASE_ACESSOR.getConnection().createStatement()
                    .executeQuery("SELECT UniqueID FROM Clans ORDER BY Exp DESC")) {
                int index = 1;
                while(rs.next()) {
                    cachedRank.put(UUID.fromString(rs.getString("UniqueID")), index);
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
