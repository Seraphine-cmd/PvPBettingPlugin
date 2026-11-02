package com.thealleysmp.pvpbetting;

import net.milkbowl.vault.economy.EconomyResponse;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class BettingAnvil {

    public static void openBetInput(Player player, String teamName, PvPBetting plugin) {
        new AnvilGUI.Builder()
            .onClose(stateSnapshot -> {
                player.sendMessage(ChatColor.GRAY + "Betting cancelled.");
            })
            .onClick((slot, stateSnapshot) -> { 
                if (slot != AnvilGUI.Slot.OUTPUT) {
                    return Collections.emptyList();
                }

                String inputText = stateSnapshot.getText();
                
                try {
                    double betAmount = Double.parseDouble(inputText);
                    if (betAmount <= 0) {
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Must be > 0!"));
                    }
                    
                    if (plugin.getEconomy().getBalance(player) >= betAmount) {
                        EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, betAmount);
                        
                        if (response.transactionSuccess()) {
                            if (teamName.equals("Blue Team")) {
                                plugin.blueTeamBets.put(player.getUniqueId(), plugin.blueTeamBets.getOrDefault(player.getUniqueId(), 0.0) + betAmount);
                            } else {
                                plugin.redTeamBets.put(player.getUniqueId(), plugin.redTeamBets.getOrDefault(player.getUniqueId(), 0.0) + betAmount);
                            }
                            plugin.totalPool += betAmount;
                            player.sendMessage(ChatColor.GREEN + "Successfully wagered $" + betAmount + " on the " + teamName + ".");
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        } else {
                            player.sendMessage(ChatColor.RED + "Transaction failed: " + response.errorMessage);
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        }
                    } else {
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Not enough money!"));
                    }

                } catch (NumberFormatException e) {
                    return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Enter a Number!"));
                }
            })
            .text("100")
            .itemLeft(new ItemStack(Material.PAPER))
            .title("Type Bet Amount")
            .plugin(plugin)
            .open(player);
    }
}
