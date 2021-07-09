package net.threader.guildplus.controller.instance;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.ClanController;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.ClanStat;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.model.implementation.ClanImpl;
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

public enum SingleClanController implements ClanController {
    INSTANCE;

    private final Map<UUID, Clan> CLANS = new HashMap<>();

    public Map<UUID, Clan> getClans() {
        return CLANS;
    }

    @Override
    public Clan registerClan(Player leader, String tag, String name) {
        UUID uid = UUID.randomUUID();
        Clan clan = new ClanImpl(uid, name, tag, 100, 0, 0, 0);
        CLANS.put(uid, clan);
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () ->
            Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
                try (PreparedStatement statement = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                        "INSERT INTO Clans VALUES(?, ?, ?, ?, ?, ?, ?)")) {

                    statement.setString(1, clan.getUniqueId().toString());
                    statement.setString(2, name);
                    statement.setString(3, tag);
                    statement.setInt(4, clan.getStat(ClanStat.POINTS));
                    statement.setInt(5, clan.getStat(ClanStat.EXP));
                    statement.setInt(6, clan.getStat(ClanStat.DEATH));
                    statement.setInt(7, clan.getStat(ClanStat.KILL));
                    statement.executeUpdate();

                    clan.registerMember(leader, Office.LEADER);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }));
        return clan;
    }

    @Override
    public void destroy(Clan clan) {
        clan.getMembers().forEach(Member::destroy);
        SingleInviteController.INSTANCE.removeInvitesOf(clan);
        SingleConfirmationController.INSTANCE.destroyAllConfirmationsFor(clan);
        this.CLANS.remove(clan.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
           try {
               PreparedStatement st1 = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                       "DELETE FROM ClanHomes WHERE ClanID=?");
               PreparedStatement st2 = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                       "DELETE FROM Clans WHERE UniqueID=?");
               st1.setString(1, clan.getUniqueId().toString());
               st2.setString(1, clan.getUniqueId().toString());
               st1.executeUpdate();
               st2.executeUpdate();
           } catch (SQLException ex) {
               ex.printStackTrace();
           }
        });
    }

    @Override
    public void downloadClans() {
        try (Statement st = GuildPlus.DATABASE_ACESSOR.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM Clans");
            while(rs.next()) {
                UUID uid = UUID.fromString(rs.getString("UniqueID"));
                String name = rs.getString("Name");
                String tag = rs.getString("Tag");
                int points = rs.getInt("Points");
                int exp = rs.getInt("Exp");
                int kills = rs.getInt("Kills");
                int deaths = rs.getInt("Deaths");
                this.CLANS.put(uid, new ClanImpl(uid, name, tag, points, exp, kills, deaths));
            }
            ResultSet rs2 = st.executeQuery("SELECT * FROM ClanHomes");
            while(rs2.next()) {
                UUID clanId = UUID.fromString(rs2.getString("ClanID"));
                UUID world = UUID.fromString(rs2.getString("World"));
                int x = rs2.getInt("X");
                int y = rs2.getInt("Y");
                int z = rs2.getInt("Z");
                Clan.of(clanId).ifPresent(clan -> clan.setHome(new Location(Bukkit.getWorld(world), x, y, z)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static class Finder {
        public static Optional<Clan> byLeaderName(String name) {
            return SingleClanController.INSTANCE.CLANS.values().stream()
                    .filter(clan -> ChatColor.stripColor(clan.getLeader().getName()).equalsIgnoreCase(name)).findFirst();
        }

        public static Optional<Clan> byPlainTag(String tag) {
            return SingleClanController.INSTANCE.CLANS.values().stream()
                    .filter(clan -> ChatColor.stripColor(clan.getTag()).equalsIgnoreCase(tag)).findFirst();
        }
    }
}
