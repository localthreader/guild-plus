package com.armacraft.clans.controller;

import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface MemberController {
    Map<UUID, Member> getMembers();
    Member registerMember(Player player, Clan clan, Office office);
    void destroy(Member member);
    void downloadMembers();
}
