package com.armacraft.clans.utils;

import com.armacraft.clans.model.Clan;

public class ExperienceCalc {
    public static int getExperienceFor(Clan clan, float baseMultiplier) {
        return (int) ((int) clan.getMembers().stream().filter(member -> member.getOfflinePlayer().isOnline())
                        .count()*baseMultiplier);
    }
}
