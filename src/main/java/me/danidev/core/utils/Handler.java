package me.danidev.core.utils;

import me.danidev.core.Main;

public class Handler
{
    private final Main instance;

    public Handler(Main instance) {
        this.instance = instance;
    }

    public void enable() {
    }

    public void disable() {
    }

    public Main getInstance() {
        return this.instance;
    }
}
