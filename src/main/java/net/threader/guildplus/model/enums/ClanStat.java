package net.threader.guildplus.model.enums;

public enum ClanStat {
    KILL("Kills"), DEATH("Deaths"), EXP("Exp"), POINTS("Points");

    private String column;

    ClanStat(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }
}
