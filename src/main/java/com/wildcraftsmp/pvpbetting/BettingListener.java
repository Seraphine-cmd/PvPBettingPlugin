package com.thealleysmp.pvpbetting;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BettingListener implements Listener {
    private final PvPBetting plugin;
    
    public BettingListener(PvPBetting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(BettingMenu.GUI_NAME)) return;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (clickedItem.getType() == Material.BLUE_WOOL) {
            player.closeInventory();
            BettingAnvil.openBetInput(player, "Blue Team", plugin);
        } else if (clickedItem.getType() == Material.RED_WOOL) {
            player.closeInventory();
            BettingAnvil.openBetInput(player, "Red Team", plugin);
        }
    }
}
