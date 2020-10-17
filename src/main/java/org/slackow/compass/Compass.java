package org.slackow.compass;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.Material.*;
import static org.bukkit.Material.COMPASS;
import static org.bukkit.Material.NETHERITE_SCRAP;

public final class Compass extends JavaPlugin implements Listener {

    private static final Set<Action> RIGHT_CLICK_ACTIONS = EnumSet.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);

    @Override
    public void onEnable() {
        Bukkit.addRecipe(new ShapedRecipe(new NamespacedKey(this, "bastion_compass"),
                modifyData(COMPASS, meta -> {
                    CompassMeta compassMeta = (CompassMeta) meta;
                    compassMeta.setCustomModelData(1);
                    compassMeta.setDisplayName(RESET + "Bastion Compass");
                }))
                .shape(" S ",
                        "SRS",
                        " S ")
                .setIngredient('S', NETHERITE_SCRAP)
                .setIngredient('R', REDSTONE));
        Bukkit.getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        if (RIGHT_CLICK_ACTIONS.contains(e.getAction())) {
            ItemStack item = e.getItem();
            if (item != null && item.getType() == COMPASS) {
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                if (meta.hasCustomModelData()) {
                    Location location = e.getPlayer().getLocation();
                    assert location.getWorld() != null;
                    Location bastion = location.getWorld().locateNearestStructure(location, StructureType.BASTION_REMNANT, 10, true);
                    e.getPlayer().sendMessage(bastion != null ? GREEN + "Nearest Bastion Tracked" : RED + "Could not find Bastion");
                    CompassMeta compassMeta = (CompassMeta) meta;
                    compassMeta.setLodestone(bastion);
                    compassMeta.setLodestoneTracked(false);
                    item.setItemMeta(compassMeta);

                }
            }
        }
    }


    private static ItemStack modifyData(Material material, Consumer<ItemMeta> consumer) {
        ItemStack result = new ItemStack(material);
        ItemMeta meta = result.getItemMeta();
        consumer.accept(meta);
        result.setItemMeta(meta);
        return result;
    }

    @Override
    public void onDisable() {

    }
}
