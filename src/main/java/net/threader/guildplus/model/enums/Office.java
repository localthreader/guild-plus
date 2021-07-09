package net.threader.guildplus.model.enums;

import java.util.Arrays;

public enum Office {
    MEMBER(1), SUBLEADER(2), LEADER(3);

    private int id;

    Office(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Office of(int id) {
        return Arrays.asList(values()).stream().filter(x -> x.getId() == id).findFirst().get();
    }
}
