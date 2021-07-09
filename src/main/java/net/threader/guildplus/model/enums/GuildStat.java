package net.threader.guildplus.model.enums;

public enum GuildStat {
    KILL("Kills"), DEATH("Deaths"), EXP("Exp"), POINTS("Points");

    private String column;

    GuildStat(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}
