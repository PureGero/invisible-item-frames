package com.github.puregero.invisibleitemframes;

import org.bukkit.plugin.java.JavaPlugin;

public class InvisibleItemFramesPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new InvisibleItemFramesCommand(this);
    }

}
