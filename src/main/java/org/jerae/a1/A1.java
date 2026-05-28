package org.jerae.a1;

import org.bukkit.plugin.java.JavaPlugin;

public final class A1 extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
