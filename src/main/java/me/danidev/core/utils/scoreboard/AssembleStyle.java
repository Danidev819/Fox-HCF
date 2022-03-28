package me.danidev.core.utils.scoreboard;

import lombok.Getter;

@Getter
public enum AssembleStyle {

    FOX(true, 15),
    MODERNO(false, 1);

    private boolean decending;
    private int startNumber;

    AssembleStyle(boolean decending, int startNumber) {
        this.decending = decending;
        this.startNumber = startNumber;
    }
    public boolean isDecending() {
        return this.decending;
    }

    public int getStartNumber() {
        return this.startNumber;
    }
}