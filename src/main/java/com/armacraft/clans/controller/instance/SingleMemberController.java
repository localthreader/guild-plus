package com.armacraft.clans.controller.instance;

import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.MemberController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.model.implementation.MemberImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public enum SingleMemberController implements MemberController {
    INSTANCE;

    private final Map<UUID, Member> MEMBERS = new HashMap<>();

    @Override
    public Map<UUID, Member> getMembers() {
        return MEMBERS;
    }

    @Override
    public Member registerMember(Player player, Clan clan, Office office) {
        Member member = new MemberImpl(player.getUniqueId(), clan, office, "");
        MEMBERS.put(player.getUniqueId(), member);
        Bukkit.getScheduler().runTaskAsynchronously(ArmaClans.instance(), () -> {
            try (PreparedStatement statement = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(
                    "INSERT INTO Members VALUES(?, ?, ?, ?)")){

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, clan.getUniqueId().toString());
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
        Bukkit.getScheduler().runTaskAsynchronously(ArmaClans.instance(), () -> {
            try (PreparedStatement st = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(
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
        try (Statement st = ArmaClans.DATABASE_ACESSOR.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM Members");
            while(rs.next()) {
                UUID uid = UUID.fromString(rs.getString("UniqueID"));
                Optional<Clan> clan = Clan.of(UUID.fromString(rs.getString("ClanID")));
                String rank = rs.getString("ChatRank");
                Office office = Office.of(rs.getInt("Office"));
                clan.ifPresent(clan1 -> this.MEMBERS.put(uid, new MemberImpl(uid, clan1, office, rank)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
