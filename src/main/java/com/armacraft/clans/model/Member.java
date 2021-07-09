package com.armacraft.clans.model;

import com.armacraft.clans.controller.instance.SingleMemberController;
import com.armacraft.clans.model.enums.Office;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;

public interface Member {
    static Optional<Member> of(UUID uniqueId) {
        return Optional.ofNullable(SingleMemberController.INSTANCE.getMembers().get(uniqueId));
    }

    OfflinePlayer getOfflinePlayer();

    String getName();

    String getRank();

    Clan getClan();

    Office getOffice();

    UUID getUniqueId();

    void destroy();

    void updateOffice(Office newOffice);

    void updateRank(String rank);
}
