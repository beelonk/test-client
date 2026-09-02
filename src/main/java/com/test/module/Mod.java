package com.test.module;

import net.minecraft.client.MinecraftClient;

public class Mod {

    private String name;
    private String displayName;
    private String description;
    public Category category;
    private int key;
    private boolean enabled;

    protected MinecraftClient mc = MinecraftClient.getInstance();

    public Mod(String name, String description, Category category){
        this.name = name;
        this.displayName = name;
        this.description = description;
        this.category = category;
    }

    public void toggle(){
        setEnabled(!enabled);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void onTick(){

    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;

        if(enabled) onEnable();
        else onDisable();
    }

    public enum Category {
        COMBAT,
        MOVEMENT,
        RENDER,
        EXPLOIT,
        WORLD
    }
}
