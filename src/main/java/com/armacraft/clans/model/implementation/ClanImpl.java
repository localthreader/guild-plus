package com.armacraft.clans.model.implementation;

import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.instance.SingleClanController;
import com.armacraft.clans.controller.instance.SingleClanRankController;
import com.armacraft.clans.controller.instance.SingleMemberController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.ClanStat;
import com.armacraft.clans.model.enums.Office;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClanImpl implements Clan {
    private Map<ClanStat, Integer> stats = new HashMap<>();
    private Set<Clan> allies = new HashSet<>();
    private UUID uniqueId;
    private String name;
    private String tag;
    private int points;
    private Location home;

    public ClanImpl(UUID uniqueId, String name, String tag, int points, int exp, int kills, int deaths) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.tag = tag;
        this.points = points;
        this.stats.put(ClanStat.POINTS, points);
        this.stats.put(ClanStat.EXP, exp);
        this.stats.put(ClanStat.DEATH, deaths);
        this.stats.put(ClanStat.KILL, kills);
    }

    @Override
    public Set<Clan> getAlliances() {
        return allies;
    }

    @Override
    public void registerMember(Player player, Office office) {
        SingleMemberController.INSTANCE.registerMember(player, this, office);
    }

    @Override
    public void registerAlly(Clan ally) {
        this.allies.add(ally);
        Bukkit.getScheduler().runTaskAsynchronously(ArmaClans.instance(), () -> {
           try(PreparedStatement st = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(
                   "INSERT INTO ClanAllies VALUES (?,?)"
           )) {
               st.setString(1, this.getUniqueId().toString());
               st.setString(2, ally.getUniqueId().toString());
               st.executeUpdate();
           } catch (SQLException ex) {
               ex.printStackTrace();
           }
        });
    }

    @Override
    public Member getLeader() {
        return this.getMembers().stream().filter(member -> member.getOffice() == Office.LEADER).findFirst().get();
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public float getKDR() {
        int deaths = getStat(ClanStat.DEATH);
        int kills = getStat(ClanStat.KILL);
        if(deaths == 0) {
            return kills;
        }
        return (float) kills/deaths;
    }

    @Override
    public Location getHome() {
        return home;
    }

    @Override
    public void setHome(Location location) {
        this.home = location;
    }

    @Override
    public int getRankPosition() {
        return SingleClanRankController.INSTANCE.getCachedRank().get(this.uniqueId) != null
                ? SingleClanRankController.INSTANCE.getCachedRank().get(this.uniqueId)
                : 0;
    }

    @Override
    public int getStat(ClanStat stat) {
        return stats.get(stat);
    }

    @Override
    public void broadcast(Message message) {
        this.getMembers().stream()
                .filter(member -> Bukkit.getPlayer(member.getUniqueId()) != null && Bukkit.getPlayer(member.getUniqueId()).isOnline())
                .forEach(member -> message.send(Bukkit.getPlayer(member.getUniqueId())));
    }

    @Override
    public void increaseStat(ClanStat stat, int quantity) {
        int currentValue = stats.get(stat);
        stats.remove(stat);
        stats.put(stat, currentValue + quantity);
        Bukkit.getScheduler().runTaskAsynchronously(ArmaClans.instance(), () -> {
            String query = "UPDATE Clans SET " + stat.getColumn() + "=" + stat.getColumn() + "+? WHERE UniqueID=?";
            try (PreparedStatement st = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(query)) {
                st.setInt(1, quantity);
                st.setString(2, this.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void decreaseStat(ClanStat stat, int quantity) {
        int currentValue = stats.get(stat);
        int finalValue = Math.max(currentValue - quantity, 0);
        stats.remove(stat);
        stats.put(stat, finalValue);
        Bukkit.getScheduler().runTaskAsynchronously(ArmaClans.instance(), () -> {
            String query = "UPDATE FROM Clans SET " + stat.getColumn() + "=? WHERE UniqueID=?";
            try (PreparedStatement st = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(query)) {
                st.setInt(1, finalValue);
                st.setString(2, this.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void updateHome(Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(ArmaClans.instance(), () -> {
            try (PreparedStatement deleteStatement = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(
                    "DELETE FROM ClanHomes WHERE ClanID=?");
                 PreparedStatement insertStatement = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(
                            "INSERT INTO ClanHomes VALUES (?,?,?,?,?)")) {
                deleteStatement.setString(1, this.uniqueId.toString());
                insertStatement.setString(1, this.uniqueId.toString());
                insertStatement.setString(2, location.getWorld().getUID().toString());
                insertStatement.setInt(3, location.getBlockX());
                insertStatement.setInt(4, location.getBlockY());
                insertStatement.setInt(5, location.getBlockZ());
                deleteStatement.executeUpdate();
                insertStatement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void destroy() {
        SingleClanController.INSTANCE.destroy(this);
    }

    @Override
    public Set<Member> getMembers() {
        return SingleMemberController.INSTANCE.getMembers().values().stream()
                .filter(member -> member.getClan().equals(this))
                .collect(Collectors.toSet());
    }
}
