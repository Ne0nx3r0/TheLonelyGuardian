package com.gmail.ne0nx3r0.lonelyguardian.listeners;

import com.gmail.ne0nx3r0.lonelyguardian.guardian.Guardian;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TheLonelyGuardianListeners implements Listener
{
    private final Guardian guardian;
    
    public TheLonelyGuardianListeners(Guardian guardian)
    {
        this.guardian = guardian;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {        
        if(!this.guardian.authenticateConnection(e.getPlayer()))
        {
            this.sendMessage(e.getPlayer(),"This is the first time you've connected with this IP. Please authenticate yourself, yo.");
        }
    }
    
    @EventHandler
    public void onPlayerquit(PlayerQuitEvent e)
    {        
        this.guardian.forget(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent e)
    {        
        this.guardian.forget(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e)
    {
        if(this.guardian.isDistrusted(e.getPlayer()))
        {
            if(!e.getMessage().startsWith("/guardian"))
            {
                e.setCancelled(true);
            
                this.sendMustAuthenticateMessage(e.getPlayer());
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        if(this.guardian.isDistrusted(e.getPlayer()))
        {
            e.setCancelled(true);
            
            this.sendMustAuthenticateMessage(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e)
    {
        if(this.guardian.isDistrusted(e.getPlayer()))
        {
            e.setCancelled(true);
            
            this.sendMustAuthenticateMessage(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerPickupItemEvent e)
    {
        if(this.guardian.isDistrusted(e.getPlayer()))
        {
            e.setCancelled(true);
            
            this.sendMustAuthenticateMessage(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        if(e.getDamager() instanceof Player
        && this.guardian.isDistrusted((Player) e.getDamager()))
        {
            e.setCancelled(true);

            this.sendMustAuthenticateMessage((Player) e.getDamager());
        }
    }

    private void sendMustAuthenticateMessage(Player player)
    {
        this.sendMessage(player,"You must authenticate with me first, yo.");
    }

    private void sendMessage(Player p, String message)
    {
        p.sendMessage(ChatColor.DARK_GREEN+"=== "+ChatColor.GREEN+"The Lonely Guardian "+ChatColor.DARK_GREEN+"===");
        p.sendMessage(message);
        p.sendMessage("");
    }
}
