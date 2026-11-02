package com.thealleysmp.pvpbetting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class BettingMenu {
    public static final String GUI_NAME = ChatColor.DARK_AQUA + "Place Your Wagers";

    public static void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_NAME);

        ItemStack blueTeam = new ItemStack(Material.BLUE_WOOL);
        ItemMeta blueMeta = blueTeam.getItemMeta();
        blueMeta.setDisplayName(ChatColor.BLUE + "Bet on Blue Team");
        blueMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Click to open prompt", 
            ChatColor.GRAY + "to type your bet amount."
        ));
        blueTeam.setItemMeta(blueMeta);

        ItemStack redTeam = new ItemStack(Material.RED_WOOL);
        ItemMeta redMeta = redTeam.getItemMeta();
        redMeta.setDisplayName(ChatColor.RED + "Bet on Red Team");
        redMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Click to open prompt", 
            ChatColor.GRAY + "to type your bet amount."
        ));
        redTeam.setItemMeta(redMeta);

        inv.setItem(11, blueTeam);
        inv.setItem(15, redTeam);

        player.openInventory(inv);
    }
}
