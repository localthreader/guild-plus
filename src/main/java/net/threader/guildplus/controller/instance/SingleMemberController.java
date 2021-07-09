package net.threader.guildplus.controller.instance;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.controller.MemberController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.model.implementation.MemberImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public enum SingleMemberController implements MemberController {
    INSTANCE;

    private final Map<UUID, Member> MEMBERS = new HashMap<>();

    @Override
    public Map<UUID, Member> getMembers() {
        return MEMBERS;
    }

    @Override
    public Member registerMember(Player player, Guild guild, Office office) {
        Member member = new MemberImpl(player.getUniqueId(), guild, office, "");
        MEMBERS.put(player.getUniqueId(), member);
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
            try (PreparedStatement statement = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                    "INSERT INTO Members VALUES(?, ?, ?, ?)")){

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, guild.getUniqueId().toString());
                statement.setString(3, "");
                statement.setInt(4, office.getId());
                statement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return member;
    }

    @Override
    public void destroy(Member member) {
        SingleConfirmationController.INSTANCE.destroyAllConfirmationsFor(member.getUniqueId());
        this.MEMBERS.remove(member.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
            try (PreparedStatement st = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                    "DELETE FROM Members WHERE UniqueID = ?")) {

                st.setString(1, member.getUniqueId().toString());
                st.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void downloadMembers() {
        try (Statement st = GuildPlus.DATABASE_ACESSOR.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM members");
            while(rs.next()) {
                UUID uid = UUID.fromString(rs.getString("unique_id"));
                Optional<Guild> guild = Guild.of(UUID.fromString(rs.getString("guild_id")));
                String rank = rs.getString("chat_rank");
                Office office = Office.of(rs.getInt("office"));
                guild.ifPresent(guild1 -> this.MEMBERS.put(uid, new MemberImpl(uid, guild1, office, rank)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
