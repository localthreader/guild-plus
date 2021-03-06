package net.threader.guildplus.utils;

import net.threader.guildplus.model.Guild;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;

import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class CommonVerifiers {

    public static final Predicate<UUID> HAS_GUILD = (uuid) -> Member.of(uuid).isPresent();

    public static final BiPredicate<UUID, Office> HAS_OFFICE = (uuid, office) -> {
        if(Member.of(uuid).isPresent()) {
            Member member = Member.of(uuid).get();
            return member.getOffice().getId() >= office.getId();
        }
        return false;
    };

    public static final BiPredicate<UUID, UUID> SAME_GUILD = (player1, player2) ->
        HAS_GUILD.test(player1) && HAS_GUILD.test(player2)
            && Guild.of(Member.of(player1).get()).equals(Guild.of(Member.of(player2).get()));
}
