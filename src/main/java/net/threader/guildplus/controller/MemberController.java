package net.threader.guildplus.controller;

import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface MemberController {
    Map<UUID, Member> getMembers();
    Member registerMember(Player player, Guild guild, Office office);
    void destroy(Member member);
    void downloadMembers();
}
