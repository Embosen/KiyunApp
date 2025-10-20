package com.example.cchen.test1.model;

/**
 * Created by cchen on 2016/6/18.
 */
public class ToggleItem {
    private ToggleKey toggleKey;
    private LightKey lightKey;
    public boolean isOn;
    public String name;


    public enum LightKey {
        TOP,
        LEFT,
        RIGHT,
        SHOW,
        WIDTH,
        FAR,
        NEAR,
        BREAK,
        ANTI_AIR_BREAK,
        ANTI_AIR_SHOW
    }

    public enum ToggleKey {
        IGN,
        LEFT,
        RIGHT,
        WARNING,
        WIDTH,
        FAR,
        NEAR,
        BREAK,
        ANTI_AIR
    }

    public ToggleItem(ToggleKey key, String name) {
        this.name = name;
        isOn = false;
        this.toggleKey = key;
    }

    public ToggleItem(LightKey key, String name) {
        this.name = name;
        isOn = false;
        this.lightKey = key;
    }
}
