package com.armacraft.clans.utils;

import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.Member;
import com.armacraft.clans.model.enums.Office;

import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class CommonVerifiers {

    public static final Predicate<UUID> HAS_CLAN = (uuid) -> Member.of(uuid).isPresent();

    public static final BiPredicate<UUID, Office> HAS_OFFICE = (uuid, office) -> {
        if(Member.of(uuid).isPresent()) {
            Member member = Member.of(uuid).get();
            return member.getOffice().getId() >= office.getId();
        }
        return false;
    };

    public static final BiPredicate<UUID, UUID> SAME_CLAN = (player1, player2) ->
        HAS_CLAN.test(player1) && HAS_CLAN.test(player2)
            && Clan.of(Member.of(player1).get()).equals(Clan.of(Member.of(player2).get()));
}
