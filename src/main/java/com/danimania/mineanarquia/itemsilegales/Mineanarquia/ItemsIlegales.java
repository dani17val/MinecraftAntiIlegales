package com.danimania.mineanarquia.itemsilegales.Mineanarquia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;


import java.util.Random;
import org.bukkit.plugin.Plugin;

public final class ItemsIlegales extends JavaPlugin implements Listener {

    private ProtocolManager protocolManager;

    private Random rng;


    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        this.rng = new Random();

    }

    @Override
    public void onEnable() {


        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        // antithunder
        this.protocolManager.addPacketListener((PacketListener)new PacketAdapter((Plugin)this, new PacketType[] { PacketType.Play.Server.NAMED_SOUND_EFFECT }) {
            public void onPacketSending(PacketEvent event) {

                try{
                    PacketContainer packet = event.getPacket();
                    Player p = event.getPlayer();
                    String soundName = (String)packet.getStrings().read(0);
                    //if (soundName.equals("ambient.weather.thunder")) {
                    if( true ){
                        int x = ((Integer)packet.getIntegers().read(0)).intValue() / 8;
                        int z = ((Integer)packet.getIntegers().read(2)).intValue() / 8;
                        int distance = ItemsIlegales.this.distanceBetweenPoints(x, p.getLocation().getBlockX(), z, p.getLocation().getBlockZ());
                        if (distance > 500) {
                            event.setCancelled(true);
                        }
                    }
                }
                catch (Exception ex) {
                    // Bukkit.getServer().getLogger().warning("excepcion al revisar un paquete");
                }

            }
        });


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void alEntrar(PlayerJoinEvent e){
        for(ItemStack i : e.getPlayer().getInventory().getContents()){
            if(verificarIlegal(i)){
                i.setAmount(0);
            }
        }
    }

    @EventHandler
    public void alPortalUsar(PlayerPortalEvent e) {
        if(e.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END)){
            Location l = e.getTo().getWorld().getSpawnLocation();
            l.setX(100.0);
            l.setY(51.0);
            l.setZ(0.0);
            e.getPlayer().teleport(e.getTo().getWorld().getSpawnLocation());
        }
    }


    @EventHandler
    public void romperEndPortal(BlockBreakEvent e){
        if(e.getBlock().getType() == Material.END_PORTAL){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void alAbrir(InventoryOpenEvent e){
        boolean ilegales = false;
        for(ItemStack i : e.getInventory().getContents()){
            if(verificarIlegal(i)){
                ilegales = true;
                i.setAmount(0);
            }
        }
        if(ilegales){
            Bukkit.getServer().getLogger().info("Items ilegales. Jugador: "+e.getPlayer().getName()+". X"+e.getPlayer().getLocation().getX()+", Y"+e.getPlayer().getLocation().getY()+", Z"+e.getPlayer().getLocation().getZ());
        }
    }

    public boolean verificarIlegal(ItemStack item){
        if(item != null){
            if(item.getType() == Material.FIREWORK_ROCKET){
                FireworkMeta fwm = (FireworkMeta) item.getItemMeta();
                if(fwm.getPower()>3){
                    return true;
                }
            }else if(item.getType() == Material.END_PORTAL_FRAME){
                return true;
            }else if(item.getType() == Material.SPLASH_POTION){
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                for(PotionEffect pe : meta.getCustomEffects()){
                    if(pe.getAmplifier()>2){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public int distanceBetweenPoints(int x1, int x2, int y1, int y2) {
        return (int)Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }
}
