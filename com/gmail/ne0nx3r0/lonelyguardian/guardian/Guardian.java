package com.gmail.ne0nx3r0.lonelyguardian.guardian;

import com.gmail.ne0nx3r0.lonelyguardian.TheLonelyGuardian;
import com.gmail.ne0nx3r0.lonelyguardian.crypto.MD5Digest;
import com.gmail.ne0nx3r0.lonelyguardian.crypto.PasswordHash;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.SQLite;
import org.bukkit.entity.Player;

public class Guardian
{
    private SQLite sqlite;
    private final TheLonelyGuardian plugin;
    private ArrayList<String> distrustedPlayers;
    
    public Guardian(TheLonelyGuardian plugin)
    {
        this.plugin = plugin;
     
        this.distrustedPlayers = new ArrayList<>();
        
        if(!plugin.getDataFolder().exists())
        {
            plugin.getDataFolder().mkdirs();
        }
        
        // DB Setup
        sqlite = new SQLite(
            plugin.getLogger(),
            "TheLonelyGuardian",
            "guardian",
            plugin.getDataFolder().getAbsolutePath()
        );
        
        try 
        {
            sqlite.open();
        }
        catch(Exception e)
        {
            plugin.getLogger().info(e.getMessage());
            plugin.getPluginLoader().disablePlugin(plugin);
        }
        
        if(!sqlite.checkTable("player"))
        {
            sqlite.query("CREATE TABLE player("
                + "username VARCHAR(16) PRIMARY KEY,"
                + "password VARCHAR(102)"
            + ");");

            sqlite.query("CREATE TABLE player_connection("
                + "hash VARCHAR(32) PRIMARY KEY,"
                + "username VARCHAR(16)"
            + ");");
            
            plugin.getLogger().log(Level.INFO, "The Lonely Guardian tables created.");
        }
    }

    public boolean authenticateConnection(Player player)
    {
        // Only force authentication for players who require it
        if(!this.needsAuthentication(player))
        {
            return true;
        }
        
        // Create a hash
        String connectionHash;

        try
        {
            connectionHash = MD5Digest.getHash(player.getName().toLowerCase() + player.getAddress().getAddress().getHostAddress());
        }
        catch (Exception ex)
        {
            this.distrust(player);
            
            plugin.getLogger().log(Level.WARNING, "Unable to get hash for: {0}{1}", new Object[]{player.getName().toLowerCase(), player.getAddress().getAddress().getHostAddress()});
            plugin.getLogger().log(Level.WARNING, "Distrusting automatically.");
            
            Logger.getLogger(Guardian.class.getName()).log(Level.SEVERE, null, ex);
            
            return false;
        }
        
        try
        {
            PreparedStatement statement = sqlite.prepare("SELECT 1 FROM player_connection WHERE hash=? LIMIT 1;");
            
            statement.setString(1, connectionHash);
            
            ResultSet result = statement.executeQuery();
            
            boolean found = result.next();
            
            result.close();

            if(!found)
            {
                this.distrust(player);
            }
                        
            return found;
        }
        catch (Exception ex)
        {
            this.distrust(player);
            
            plugin.getLogger().log(Level.WARNING, "Unknown error occurred, distrusting player automatically.");
            
            Logger.getLogger(Guardian.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
    }

    public void trust(Player player)
    {
        this.distrustedPlayers.remove(player.getName().toLowerCase());
    }
    
    public void distrust(Player player)
    {
        this.distrustedPlayers.add(player.getName().toLowerCase());
    }
    
    public boolean isDistrusted(Player player)
    {
        return this.distrustedPlayers.contains(player.getName().toLowerCase());
    }

    public void forget(Player player)
    {
        this.distrustedPlayers.remove(player.getName().toLowerCase());
    }

    private boolean needsAuthentication(Player player)
    {
        return player.hasPermission("TheLonelyGuardian.guard");
    }
    
    
// Command listener methods

    public boolean registerPlayer(Player player, String sPassword)
    {            
        try
        {
            PreparedStatement statement = sqlite.prepare("INSERT INTO player(username,password) VALUES(?,?);");
            
            statement.setString(1, player.getName().toLowerCase());
            statement.setString(2, PasswordHash.createHash(sPassword));
            
            statement.execute();
            
            return true;
        }
        catch (Exception ex)
        {
            this.distrust(player);
            
            plugin.getLogger().log(Level.WARNING, "Unknown error occurred, distrusting player automatically.");
            
            Logger.getLogger(Guardian.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
    }

    public boolean isRegistered(Player player)
    {
        try
        {
            PreparedStatement statement = sqlite.prepare("SELECT 1 FROM player WHERE username=? LIMIT 1;");
            
            statement.setString(1, player.getName().toLowerCase());
            
            ResultSet result = statement.executeQuery();
            
            boolean found = result.next();
            
            result.close();
                        
            return found;
        }
        catch (Exception ex)
        {
            this.distrust(player);
            
            plugin.getLogger().log(Level.WARNING, "Unknown error occurred, distrusting player automatically.");
            
            Logger.getLogger(Guardian.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
    }

    public boolean authenticatePlayer(Player player, String sPassword)
    {
        try
        {
            PreparedStatement statement = sqlite.prepare("SELECT password FROM player WHERE username=? LIMIT 1;");
            
            statement.setString(1, player.getName().toLowerCase());
            
            ResultSet result = statement.executeQuery();
            
            if(result.next())
            {
                String serverHash = result.getString("password");
                
                result.close();
                
                if(PasswordHash.validatePassword(sPassword, serverHash))
                {
                    this.trust(player);
                    
                    return true;
                }
                
                return false;
            }
            else
            {
                result.close();
                
                return false;
            }
        }
        catch (Exception ex)
        {
            this.distrust(player);
            
            plugin.getLogger().log(Level.WARNING, "Unknown error occurred, distrusting player automatically.");
            
            Logger.getLogger(Guardian.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
    }

    public boolean resetPlayer(String sUsername, String sPassword)
    {
        try
        {
// DELETE player entry
            PreparedStatement statement = sqlite.prepare("DELETE FROM player WHERE username=?;");
            
            statement.setString(1, sUsername);
            
            statement.execute();
            
// DELETE player_connection entries        
            statement = sqlite.prepare("DELETE FROM player_connection WHERE username=?;");
            
            statement.setString(1, sUsername);
            
            statement.execute();

// re-register player
            statement = sqlite.prepare("INSERT INTO player(username,password) VALUES(?,?);");
            
            statement.setString(1, sUsername);
            statement.setString(2, PasswordHash.createHash(sPassword));
            
            statement.execute();
            
            return true;
        }
        catch (Exception ex)
        {            
            plugin.getLogger().log(Level.WARNING, "Unknown error occurred, distrusting player automatically.");
            
            Logger.getLogger(Guardian.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
    }

    public boolean addTrustedConnection(Player player, String sPassword)
    {
        try
        {

// register player connection
            PreparedStatement statement = sqlite.prepare("INSERT INTO player_connection(username,hash) VALUES(?,?);");
            
            statement.setString(1, player.getName().toLowerCase());
            statement.setString(2, MD5Digest.getHash(player.getName().toLowerCase() + player.getAddress().getAddress().getHostAddress()));
            
            statement.execute();
            
            return true;
        }
        catch (Exception ex)
        {            
            plugin.getLogger().log(Level.WARNING, "Unknown error occurred, distrusting player automatically.");
            
            Logger.getLogger(Guardian.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
    }
}
