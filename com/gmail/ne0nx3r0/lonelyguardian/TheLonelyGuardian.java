package com.gmail.ne0nx3r0.lonelyguardian;

import com.gmail.ne0nx3r0.lonelyguardian.listeners.TheLonelyGuardianListeners;
import com.gmail.ne0nx3r0.lonelyguardian.commands.TheLonelyGuardianCommands;
import com.gmail.ne0nx3r0.lonelyguardian.guardian.Guardian;
import org.bukkit.plugin.java.JavaPlugin;

public class TheLonelyGuardian extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        Guardian guardian = new Guardian(this);
        
        getServer().getPluginManager().registerEvents(new TheLonelyGuardianListeners(guardian), this);
        
        getCommand("guardian").setExecutor(new TheLonelyGuardianCommands(guardian));
    }
}
