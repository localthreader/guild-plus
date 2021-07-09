package com.armacraft.clans.model.implementation;

import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.controller.instance.SingleMemberController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MemberImpl implements Member {
    private UUID uniqueId;
    private Clan clan;
    private Office office;
    private String rank;

    public MemberImpl(UUID uniqueId, Clan clan, Office office, String rank) {
        this.uniqueId = uniqueId;
        this.clan = clan;
        this.office = office;
        this.rank = rank;
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uniqueId);
    }

    @Override
    public String getName() {
        return getOfflinePlayer().getName();
    }

    @Override
    public String getRank() {
        return rank;
    }

    @Override
    public Clan getClan() {
        return clan;
    }

    @Override
    public Office getOffice() {
        return office;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public void destroy() {
        SingleMemberController.INSTANCE.destroy(this);
    }

    @Override
    public void updateOffice(Office newOffice) {
        if(newOffice != this.office) {
            this.office = newOffice;
            Bukkit.getScheduler().runTaskAsynchronously(ArmaClans.instance(), () -> {
               try (PreparedStatement st = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(
                       "UPDATE Members SET Office=? WHERE UniqueID=?")) {
                   st.setInt(1, newOffice.getId());
                   st.setString(2, this.getUniqueId().toString());
                   st.executeUpdate();
               } catch (SQLException throwables) {
                   throwables.printStackTrace();
               }
            });
        }
    }

    @Override
    public void updateRank(String rank) {
        this.rank = rank;
        Bukkit.getScheduler().runTaskAsynchronously(ArmaClans.instance(), () -> {
            try (PreparedStatement st = ArmaClans.DATABASE_ACESSOR.getConnection().prepareStatement(
                    "UPDATE Members SET ChatRank=? WHERE UniqueID=?")) {
                st.setString(1, rank);
                st.setString(2, this.getUniqueId().toString());
                st.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
}
