/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.PrepareItemCraftEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.bait.plugins;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class dupe
extends JavaPlugin
implements Listener {
    HashMap<UUID, Integer> duping = new HashMap();
    Integer looped = 0;
    Integer looped2 = 0;
    Integer runcommand = 0;
    Integer duping2 = 0;
    Integer return1 = 0;

    public static dupe getPlugin() {
        return (dupe)dupe.getPlugin(dupe.class);
    }

    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getServer().getLogger().info("[BaitCorp] 1.12 Dupe Re-Enabled, thanks for chosing baitcorphost plugins!");
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)dupe.getPlugin(), new Runnable(){

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!dupe.this.duping.containsKey(player.getUniqueId())) continue;
                    Integer amt = dupe.this.duping.get(player.getUniqueId());
                    if (amt <= 0) {
                        dupe.this.duping.remove(player.getUniqueId());
                        continue;
                    }
                    amt = amt - 1;
                    dupe.this.duping.put(player.getUniqueId(), amt);
                }
            }
        }, 5L, 20L);
    }

    @EventHandler
    public void onClick(PrepareItemCraftEvent e) {
        for (HumanEntity entity : e.getViewers()) {
            if (!(entity instanceof Player)) continue;
            this.duping.put(entity.getUniqueId(), 4);
        }
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    @EventHandler
    public void onPickup(final PlayerPickupItemEvent e) {
        this.return1 = 0;
        String command = "clear " + e.getPlayer().getName() + " planks 64";
        if (this.duping.containsKey(e.getPlayer().getUniqueId()) && this.duping.get(e.getPlayer().getUniqueId()) != 0) {
            final Thread t = new Thread(new Runnable(){

                @Override
                public void run() {
                    try {
                        Player player = e.getPlayer();
                        dupe.this.looped = 0;
                        dupe.this.looped2 = 1;
                        dupe.this.duping2 = 0;
                        for (ItemStack item : player.getInventory().getContents()) {
                            if (item == null || item.getType().toString() != "PLANKS" && item.getType().toString() != "OAK_PLANKS" && item.getType().toString() != "WOOD") continue;
                            dupe.this.duping2 = 1;
                        }
                        for (ItemStack item : player.getInventory().getContents()) {
                            if (dupe.this.duping2 == 0) {
                                return;
                            }
                            if (dupe.this.looped == 1) {
                                return;
                            }
                            if (dupe.this.looped2 != 1 || !item.isSimilar(e.getItem().getItemStack())) continue;
                            item.setAmount(Integer.valueOf(dupe.getRandomNumberInRange(63, 75)).intValue());
                            dupe.this.looped = 1;
                            dupe.this.getServer().getLogger().info(String.valueOf(e.getPlayer().getName()) + " duped item: " + item.getType().toString());
                            dupe.this.runcommand = 1;
                        }
                        if (dupe.this.runcommand == 1) {
                            for (ItemStack item : e.getPlayer().getInventory().getContents()) {
                                if (dupe.this.return1 == 1) {
                                    return;
                                }
                                if (item == null) continue;
                                if (item.getType().toString() == "PLANKS" || item.getType().toString() == "OAK_PLANKS" || item.getType().toString() == "WOOD") {
                                    dupe.this.return1 = 1;
                                }
                                item.setAmount(0);
                            }
                            dupe.this.runcommand = 0;
                        }
                        dupe.this.duping.put(e.getPlayer().getUniqueId(), 0);
                    }
                    catch (Error e2) {
                        e2.printStackTrace();
                    }
                }
            });
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)dupe.getPlugin(), new Runnable(){

                @Override
                public void run() {
                    t.start();
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        this.duping.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        this.duping.put(e.getPlayer().getUniqueId(), 0);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        this.duping.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.duping.remove(e.getPlayer().getUniqueId());
    }

    public void onDisable() {
        this.duping.clear();
        Bukkit.getScheduler().cancelTasks((Plugin)this);
        this.getServer().getLogger().info("[BaitCorp] 1.12 Dupe Disabled, thanks for chosing baitcorphost plugins!");
    }
}
