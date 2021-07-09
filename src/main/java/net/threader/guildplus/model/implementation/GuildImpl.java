package net.threader.guildplus.model.implementation;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.instance.SingleGuildController;
import net.threader.guildplus.controller.instance.SingleGuildRankController;
import net.threader.guildplus.controller.instance.SingleMemberController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.GuildStat;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuildImpl implements Guild {
    private Map<GuildStat, Integer> stats = new HashMap<>();
    private Set<Guild> allies = new HashSet<>();
    private UUID uniqueId;
    private String name;
    private String tag;
    private int points;
    private Location home;

    public GuildImpl(UUID uniqueId, String name, String tag, int points, int exp, int kills, int deaths) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.tag = tag;
        this.points = points;
        this.stats.put(GuildStat.POINTS, points);
        this.stats.put(GuildStat.EXP, exp);
        this.stats.put(GuildStat.DEATH, deaths);
        this.stats.put(GuildStat.KILL, kills);
    }

    @Override
    public Set<Guild> getAlliances() {
        return allies;
    }

    @Override
    public void registerMember(Player player, Office office) {
        SingleMemberController.INSTANCE.registerMember(player, this, office);
    }

    @Override
    public void registerAlly(Guild ally) {
        this.allies.add(ally);
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
           try(PreparedStatement st = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                   "INSERT INTO guild_allies VALUES (?,?)"
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
        int deaths = getStat(GuildStat.DEATH);
        int kills = getStat(GuildStat.KILL);
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
        return SingleGuildRankController.INSTANCE.getCachedRank().get(this.uniqueId) != null
                ? SingleGuildRankController.INSTANCE.getCachedRank().get(this.uniqueId)
                : 0;
    }

    @Override
    public int getStat(GuildStat stat) {
        return stats.get(stat);
    }

    @Override
    public void broadcast(Message message) {
        this.getMembers().stream()
                .filter(member -> Bukkit.getPlayer(member.getUniqueId()) != null && Bukkit.getPlayer(member.getUniqueId()).isOnline())
                .forEach(member -> message.send(Bukkit.getPlayer(member.getUniqueId())));
    }

    @Override
    public void increaseStat(GuildStat stat, int quantity) {
        int currentValue = stats.get(stat);
        stats.remove(stat);
        stats.put(stat, currentValue + quantity);
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
            String query = "UPDATE guilds SET " + stat.getColumn() + "=" + stat.getColumn() + "+? WHERE UniqueID=?";
            try (PreparedStatement st = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(query)) {
                st.setInt(1, quantity);
                st.setString(2, this.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void decreaseStat(GuildStat stat, int quantity) {
        int currentValue = stats.get(stat);
        int finalValue = Math.max(currentValue - quantity, 0);
        stats.remove(stat);
        stats.put(stat, finalValue);
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
            String query = "UPDATE FROM guilds SET " + stat.getColumn() + "=? WHERE UniqueID=?";
            try (PreparedStatement st = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(query)) {
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
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
            try (PreparedStatement deleteStatement = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                    "DELETE FROM guild_homes WHERE guild_id=?");
                 PreparedStatement insertStatement = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                            "INSERT INTO guild_homes VALUES (?,?,?,?,?)")) {
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
        SingleGuildController.INSTANCE.destroy(this);
    }

    @Override
    public Set<Member> getMembers() {
        return SingleMemberController.INSTANCE.getMembers().values().stream()
                .filter(member -> member.getGuild().equals(this))
                .collect(Collectors.toSet());
    }
}
