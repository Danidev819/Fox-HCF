package me.danidev.core.managers.classes.rogue;

import me.danidev.core.Main;
import me.danidev.core.managers.classes.PvPClass;
import org.bukkit.entity.Damageable;
import org.bukkit.inventory.PlayerInventory;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import gnu.trove.map.hash.TObjectLongHashMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.potion.PotionEffectType;
import java.util.UUID;

import gnu.trove.map.TObjectLongMap;

import org.bukkit.potion.PotionEffect;
import org.bukkit.event.Listener;

public class RogueClass extends PvPClass implements Listener {
	
    private static final PotionEffect ROGUE_SPEED_EFFECT;
    private static final PotionEffect ROGUE_JUMP_EFFECT;
    private static final long ROGUE_SPEED_COOLDOWN_DELAY;
    private static final long ROGUE_JUMP_COOLDOWN_DELAY;
    private final Main plugin;
    private final TObjectLongMap<UUID> rogueSpeedCooldowns;
    private final TObjectLongMap<UUID> rogueJumpCooldowns;
    private RogueRestorer rogueRestorer;
    
    static {
        ROGUE_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 275, 4);
        ROGUE_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 275, 5);
        ROGUE_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(30L);
        ROGUE_JUMP_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(30L);
    }
    
    public RogueClass(final Main plugin) {
        super("Rogue", TimeUnit.SECONDS.toMillis(5L));
        this.rogueSpeedCooldowns = new TObjectLongHashMap<UUID>();
        this.rogueJumpCooldowns = new TObjectLongHashMap<UUID>();
        this.plugin = plugin;
        this.rogueRestorer = new RogueRestorer(plugin);
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player damaged = (Player)event.getEntity();
            final Player damager = (Player)event.getDamager();
            if (damaged != damager && this.plugin.getPvpClassManager().getEquippedClass(damager) == this) {
                final ItemStack itemInHand = damager.getItemInHand();
                if (itemInHand != null && itemInHand.getType() == Material.GOLD_SWORD && itemInHand.getEnchantments().isEmpty()) {
                    boolean cancelled = false;
                    for (final PotionEffect activePotionEffects : damager.getActivePotionEffects()) {
                        if (activePotionEffects.getType().equals(PotionEffectType.SLOW)) {
                            cancelled = true;
                        }
                    }
                    if (!cancelled) {
                        final Vector damagerDirection = damager.getLocation().getDirection();
                        final Vector damagedDirection = damaged.getLocation().getDirection();
                        if (damagerDirection.dot(damagedDirection) > 0.0) {
                            damaged.setHealth(((Damageable)damaged).getHealth() - 9.0);
                            damager.setItemInHand(new ItemStack(Material.AIR));
                            damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                            damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 2));
                            damager.sendMessage("");
                            damaged.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou has been &6&lBACKSTABBED &c&lby " + damager.getName() + "."));
                            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have backstabbed &3" + damaged.getName() + "&e."));
                            damager.sendMessage("");
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.SUGAR) {
            if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            final Player player = event.getPlayer();
            final UUID uuid = player.getUniqueId();
            final long timestamp = this.rogueSpeedCooldowns.get(uuid);
            final long millis = System.currentTimeMillis();
            final long remaining = (timestamp == this.rogueSpeedCooldowns.getNoEntryValue()) ? -1L : (timestamp - millis);
            if (remaining > 0L) {
                player.sendMessage(ChatColor.RED + "Cannot use " + this.getName() + " Speed for another " + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
            }
            else {
                final ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }
                else {
                    stack.setAmount(stack.getAmount() - 1);
                }
                this.rogueRestorer.setRestoreEffect(player, RogueClass.ROGUE_SPEED_EFFECT);
                player.sendMessage(ChatColor.YELLOW + "You now have your " + ChatColor.GOLD + "Rogue Speed" + ChatColor.YELLOW + ".");
                this.rogueSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + RogueClass.ROGUE_SPEED_COOLDOWN_DELAY);
            }
        }
        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.FEATHER) {
            if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            final Player player = event.getPlayer();
            final UUID uuid = player.getUniqueId();
            final long timestamp = this.rogueJumpCooldowns.get(uuid);
            final long millis = System.currentTimeMillis();
            final long remaining = (timestamp == this.rogueJumpCooldowns.getNoEntryValue()) ? -1L : (timestamp - millis);
            if (remaining > 0L) {
                player.sendMessage(ChatColor.RED + "Cannot use " + this.getName() + " Jump for another " + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
            }
            else {
                final ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }
                else {
                    stack.setAmount(stack.getAmount() - 1);
                }
                this.rogueRestorer.setRestoreEffect(player, RogueClass.ROGUE_JUMP_EFFECT);
                player.sendMessage(ChatColor.YELLOW + "You now have your " + ChatColor.GOLD + "Rogue Jump" + ChatColor.YELLOW + ".");
                this.rogueJumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + RogueClass.ROGUE_JUMP_COOLDOWN_DELAY);
            }
        }
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.CHAINMAIL_BOOTS;
    }
}
