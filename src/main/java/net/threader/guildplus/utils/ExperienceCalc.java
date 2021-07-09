package net.threader.guildplus.utils;

import net.threader.guildplus.model.Guild;

public class ExperienceCalc {
    public static int getExperienceFor(Guild guild, float baseMultiplier) {
        return (int) ((int) guild.getMembers().stream().filter(member -> member.getOfflinePlayer().isOnline())
                        .count()*baseMultiplier);
    }
}
