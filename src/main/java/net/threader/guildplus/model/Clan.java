package net.threader.guildplus.model;

import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.controller.instance.SingleClanController;
import net.threader.guildplus.model.enums.ClanStat;
import net.threader.guildplus.model.enums.Office;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Clan {

    static Clan of(Member member) {
        return member.getClan();
    }

    static Optional<Clan> of(UUID uniqueId) {
        return Optional.ofNullable(SingleClanController.INSTANCE.getClans().get(uniqueId));
    }

    Set<Clan> getAlliances();

    void registerMember(Player player, Office office);

    void registerAlly(Clan ally);

    Member getLeader();

    UUID getUniqueId();

    String getName();

    String getTag();

    void setTag(String tag);

    void setName(String name);

    float getKDR();

    Location getHome();

    void setHome(Location location);

    int getRankPosition();

    int getStat(ClanStat stat);

    void broadcast(Message message);

    void increaseStat(ClanStat stat, int quantity);

    void decreaseStat(ClanStat stat, int quantity);

    void updateHome(Location location);

    void destroy();

    Set<Member> getMembers();
}
