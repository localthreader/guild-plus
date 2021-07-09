package net.threader.guildplus.model;

import net.threader.guildplus.controller.instance.SingleMemberController;
import net.threader.guildplus.model.enums.Office;
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
