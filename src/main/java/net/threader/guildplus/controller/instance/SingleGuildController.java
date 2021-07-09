package net.threader.guildplus.controller.instance;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.GuildController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.GuildStat;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.model.implementation.GuildImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public enum SingleGuildController implements GuildController {
    INSTANCE;

    private final Map<UUID, Guild> GUILDS = new HashMap<>();

    public Map<UUID, Guild> getGuilds() {
        return GUILDS;
    }

    @Override
    public Guild registerGuild(Player leader, String tag, String name) {
        UUID uid = UUID.randomUUID();
        Guild guild = new GuildImpl(uid, name, tag, 100, 0, 0, 0);
        GUILDS.put(uid, guild);
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () ->
            Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
                try (PreparedStatement statement = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                        "INSERT INTO guilds VALUES(?, ?, ?, ?, ?, ?, ?)")) {

                    statement.setString(1, guild.getUniqueId().toString());
                    statement.setString(2, name);
                    statement.setString(3, tag);
                    statement.setInt(4, guild.getStat(GuildStat.POINTS));
                    statement.setInt(5, guild.getStat(GuildStat.EXP));
                    statement.setInt(6, guild.getStat(GuildStat.DEATH));
                    statement.setInt(7, guild.getStat(GuildStat.KILL));
                    statement.executeUpdate();

                    guild.registerMember(leader, Office.LEADER);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }));
        return guild;
    }

    @Override
    public void destroy(Guild guild) {
        guild.getMembers().forEach(Member::destroy);
        SingleInviteController.INSTANCE.removeInvitesOf(guild);
        SingleConfirmationController.INSTANCE.destroyAllConfirmationsFor(guild);
        this.GUILDS.remove(guild.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
           try {
               PreparedStatement st1 = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                       "DELETE FROM guild_homes WHERE guild_id=?");
               PreparedStatement st2 = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                       "DELETE FROM guilds WHERE unique_id=?");
               st1.setString(1, guild.getUniqueId().toString());
               st2.setString(1, guild.getUniqueId().toString());
               st1.executeUpdate();
               st2.executeUpdate();
           } catch (SQLException ex) {
               ex.printStackTrace();
           }
        });
    }

    @Override
    public void downloadGuilds() {
        try (Statement st = GuildPlus.DATABASE_ACESSOR.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM guilds");
            while(rs.next()) {
                UUID uid = UUID.fromString(rs.getString("unique_id"));
                String name = rs.getString("name");
                String tag = rs.getString("tag");
                int points = rs.getInt("points");
                int exp = rs.getInt("experience");
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");
                this.GUILDS.put(uid, new GuildImpl(uid, name, tag, points, exp, kills, deaths));
            }
            ResultSet rs2 = st.executeQuery("SELECT * FROM guild_homes");
            while(rs2.next()) {
                UUID guildId = UUID.fromString(rs2.getString("guild_id"));
                UUID world = UUID.fromString(rs2.getString("world"));
                int x = rs2.getInt("X");
                int y = rs2.getInt("Y");
                int z = rs2.getInt("Z");
                Guild.of(guildId).ifPresent(guild -> guild.setHome(new Location(Bukkit.getWorld(world), x, y, z)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static class Finder {
        public static Optional<Guild> byLeaderName(String name) {
            return SingleGuildController.INSTANCE.GUILDS.values().stream()
                    .filter(guild -> ChatColor.stripColor(guild.getLeader().getName()).equalsIgnoreCase(name)).findFirst();
        }

        public static Optional<Guild> byPlainTag(String tag) {
            return SingleGuildController.INSTANCE.GUILDS.values().stream()
                    .filter(guild -> ChatColor.stripColor(guild.getTag()).equalsIgnoreCase(tag)).findFirst();
        }
    }
}
