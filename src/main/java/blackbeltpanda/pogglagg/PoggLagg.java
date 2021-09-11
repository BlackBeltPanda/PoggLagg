package blackbeltpanda.pogglagg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public final class PoggLagg extends JavaPlugin implements Listener {

    boolean running = false;
    int timer = 0;
    PoggLagg plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    @EventHandler
    public void onDiamondMine(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.DIAMOND_ORE) return;
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(Vector.getRandom().add(Vector.getRandom().add(Vector.getRandom()))), new ItemStack(Material.DIAMOND));
        Player player = event.getPlayer();
        if (!running) {
            running = true;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (timer == 0) {
                        player.sendMessage(ChatColor.RED + "Warning! Lagg™ detected!");
                        player.sendMessage(ChatColor.RED + "Ground entities will be cleared in 10 seconds to reduce Lagg™.");
                        player.getInventory().getItemInMainHand().addEnchantment(Enchantment.DIG_SPEED, 5);
                    } else if (timer >= 5) {
                        if (timer == 5) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 120, 2, false, false, false));
                        } else if (timer == 10) {
                            player.getWorld().getNearbyEntitiesByType(Item.class, player.getLocation(), 100.0).forEach(Entity::remove);
                            player.sendMessage(ChatColor.RED + "Ground entities have been cleared");
                            reallyGetRidOfThatLagg(player);
                            timer = 0;
                            this.cancel();
                            return;
                        }
                        player.sendMessage(ChatColor.YELLOW + String.valueOf(10 - timer));
                    }
                    timer++;
                }
            }.runTaskTimer(this, 60L, 20L);
        }
    }

    public void reallyGetRidOfThatLagg(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timer == 100 || timer == 140 || timer == 160 || timer == 170 || timer == 180 || timer > 180) {
                    boolean hasItem = false;
                    for (ItemStack item : player.getInventory().getStorageContents()) {
                        if (item != null && item.getType() != Material.AIR) {
                            player.getInventory().remove(item);
                            hasItem = true;
                            break;
                        }
                    }
                    if (!hasItem) {
                        player.getInventory().setArmorContents(null);
                        player.getInventory().setExtraContents(null);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.sendTitle(ChatColor.GREEN + "Lagg™ Cleared", ChatColor.YELLOW + "Next clear in 30 seconds", 0, 100, 40);
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                            }
                        }.runTaskLater(plugin, 40L);
                        timer = 0;
                        running = false;
                        this.cancel();
                        return;
                    }
                }
                timer++;
            }
        }.runTaskTimer(this, 0L, 1L);
    }
}
