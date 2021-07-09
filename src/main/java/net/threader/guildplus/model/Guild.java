package net.threader.guildplus.model;

import net.threader.guildplus.controller.instance.SingleGuildController;
import net.threader.guildplus.model.enums.GuildStat;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.message.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Guild {

    static Guild of(Member member) {
        return member.getGuild();
    }

    static Optional<Guild> of(UUID uniqueId) {
        return Optional.ofNullable(SingleGuildController.INSTANCE.getGuilds().get(uniqueId));
    }

    Set<Guild> getAlliances();

    void registerMember(Player player, Office office);

    void registerAlly(Guild ally);

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

    int getStat(GuildStat stat);

    void broadcast(Message message);

    void increaseStat(GuildStat stat, int quantity);

    void decreaseStat(GuildStat stat, int quantity);

    void updateHome(Location location);

    void destroy();

    Set<Member> getMembers();
}
