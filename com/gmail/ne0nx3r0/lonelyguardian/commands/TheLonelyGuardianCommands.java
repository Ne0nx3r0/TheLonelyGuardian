package com.gmail.ne0nx3r0.lonelyguardian.commands;

import com.gmail.ne0nx3r0.lonelyguardian.guardian.Guardian;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TheLonelyGuardianCommands implements CommandExecutor
{
    private final Guardian guardian;

    public TheLonelyGuardianCommands(Guardian guardian)
    {
        this.guardian = guardian;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args)
    {
        //redundant, sort of.
        if(!cs.hasPermission("TheLonelyGuardian.guard"))
        {
            this.sendError(cs, "You do not have permission to use guardian, yo.");
            
            return true;
        }
        
        if(args.length == 0)
        {
            return this.usage(cs);
        }
        
        List<String> aArgs = new LinkedList<>(Arrays.asList(args));

        aArgs.remove(0);
                
        switch(args[0])
        {
            case "register":
                return this.register(cs, aArgs);
            case "remember":
                return this.remember(cs, aArgs);
            case "reset":
                return this.reset(cs, aArgs);
            default:
                //Uses the 0 arg as password
                return this.authenticate(cs,new LinkedList<>(Arrays.asList(args)));
        }
    }
    
    private boolean usage(CommandSender cs)
    {
        cs.sendMessage(ChatColor.DARK_GREEN+"=== "+ChatColor.GREEN+"The Lonely Guardian "+ChatColor.DARK_GREEN+"===");
        cs.sendMessage("Usage, yo:");
        cs.sendMessage("/guardian register <password> <passwordConfirm>");
        cs.sendMessage("/guardian <password>");
        cs.sendMessage("/guardian remember <password>");
        cs.sendMessage("/guardian reset <username> <password>");
        
        return true;
    }

    private boolean register(CommandSender cs, List<String> aArgs)
    {
        if(!cs.hasPermission("TheLonelyGuardian.guard"))
        {
            this.sendError(cs, "You do not have permission to use this command, yo.");
            
            return true;
        }
        
        if(aArgs.size() < 2)
        {
            this.sendError(cs,"Passwords did not match, yo.");
            
            return true;
        }
        
        String sPassword = aArgs.get(0);
        String sPasswordConfirm = aArgs.get(1);
        
        if(!sPassword.equals(sPasswordConfirm))
        {
            this.sendError(cs,"Passwords did not match, yo.");
            
            return true;
        }
        
        if(this.guardian.isRegistered((Player) cs))
        {
            this.sendError(cs, "You have registered already, yo.");
            
            return true;
        }
        
        if(this.guardian.registerPlayer((Player) cs,sPassword))
        {
            if(this.guardian.authenticatePlayer((Player) cs, sPassword))
            {
                this.sendMessage(cs,"You are now registered, yo.");
            }
            else
            {
                this.sendMessage(cs,"Registered you, but unable to log you in... Weird, yo.");
            }
            
            return true;
        }
        else
        {
            this.sendError(cs, "An unknown error occurred, yo.");
            
            return true;
        }
    }

    private boolean authenticate(CommandSender cs, List<String> aArgs)
    {
        if(!(cs instanceof Player))
        {
            this.sendError(cs, "Not from console, yo.");
            
            return true;
        }
        
        if(!cs.hasPermission("TheLonelyGuardian.guard"))
        {
            this.sendError(cs, "You do not have permission to use this command, yo.");
            
            return true;
        }
        
        if(!this.guardian.isRegistered((Player) cs))
        {
            this.sendError(cs, "You must register first, yo.");
            
            return true;
        }
        
        if(aArgs.isEmpty())
        {
            return this.usage(cs);
        }
        
        String sPassword = aArgs.get(0);
        
        if(this.guardian.authenticatePlayer((Player) cs, sPassword))
        {
            this.sendMessage(cs, "You are now authenticated for this session only, yo.");
        }
        else
        {
            this.sendError(cs, "Invalid password, yo.");
        }
        
        return true;
    }

    private boolean reset(CommandSender cs, List<String> aArgs)
    {
        if(cs instanceof Player)
        {
            this.sendError(cs, "Console only, yo.");
            
            return true;
        }
        
        if(aArgs.size() < 2)
        {
            return this.usage(cs);
        }
        
        String sUsername = aArgs.get(0);
        String sPassword = aArgs.get(1);
        
        if(this.guardian.resetPlayer(sUsername,sPassword))
        {
            this.sendMessage(cs, sUsername+ " was reset, yo.");
        }
        else
        {
            this.sendError(cs, "An unexpected error occurred, yo.");
        }
        
        return true;
    }

    private boolean remember(CommandSender cs, List<String> aArgs)
    {
   if(!(cs instanceof Player))
        {
            this.sendError(cs, "Not from console, yo.");
            
            return true;
        }
        
        if(!cs.hasPermission("TheLonelyGuardian.guard"))
        {
            this.sendError(cs, "You do not have permission to use this command, yo.");
            
            return true;
        }
        
        if(!this.guardian.isRegistered((Player) cs))
        {
            this.sendError(cs, "You must register first, yo.");
            
            return true;
        }
        
        if(aArgs.isEmpty())
        {
            return this.usage(cs);
        }
        
        String sPassword = aArgs.get(0);
        
        if(this.guardian.authenticatePlayer((Player) cs, sPassword))
        {
            if(this.guardian.addTrustedConnection((Player) cs, sPassword))
            {
                this.sendMessage(cs, "You are now authenticated "+ChatColor.DARK_RED+"permanently"+ChatColor.RESET+" for this IP, yo.");
            }
            else
            {
                this.sendError(cs, "You were authenticated, but I was unable to store this connection... Weird, yo.");
            }
        }
        else
        {
            this.sendError(cs, "Invalid password, yo.");
        }
        
        return true;
    }
    
    private void sendError(CommandSender cs, String message)
    {
        this.sendMessage(cs, ChatColor.DARK_RED+message);
        cs.sendMessage("");
    }
        
    private void sendMessage(CommandSender cs, String message)
    {
        cs.sendMessage(ChatColor.DARK_GREEN+"=== "+ChatColor.GREEN+"The Lonely Guardian "+ChatColor.DARK_GREEN+"===");
        cs.sendMessage(message);
        cs.sendMessage("");
    }
}
