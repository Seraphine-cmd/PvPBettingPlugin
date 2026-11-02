package com.thealleysmp.pvpbetting;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvPBetting extends JavaPlugin {
    private static Economy econ = null;
    public Map<UUID, Double> blueTeamBets = new HashMap<>();
    public Map<UUID, Double> redTeamBets = new HashMap<>();
    public double totalPool = 0.0;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new BettingListener(this), this);
        getLogger().info("PvPBetting enabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public void processPayout(String winningTeam) {
        Map<UUID, Double> winningBets = winningTeam.equalsIgnoreCase("blue") ? blueTeamBets : redTeamBets;
        
        if (winningBets.isEmpty()) {
            Bukkit.broadcastMessage("§c[PvP Bets] The event ended, but no one wagered on the winning team!");
            resetPool();
            return;
        }

        double winningTeamTotalBet = winningBets.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Map.Entry<UUID, Double> entry : winningBets.entrySet()) {
            UUID playerId = entry.getKey();
            double playerBet = entry.getValue();
            
            double payoutPercentage = playerBet / winningTeamTotalBet;
            double winnings = totalPool * payoutPercentage;
            
            OfflinePlayer winner = Bukkit.getOfflinePlayer(playerId);
            getEconomy().depositPlayer(winner, winnings);
            
            if (winner.isOnline()) {
                winner.getPlayer().sendMessage("§6§l[PvP Bets] §aYou won $" + String.format("%.2f", winnings) + " from your wager on the " + winningTeam.toUpperCase() + " team!");
            }
        }
        
        Bukkit.broadcastMessage("§6§l[PvP Bets] §fPayouts distributed to the " + winningTeam.toUpperCase() + " team bettors! Total pool: $" + String.format("%.2f", totalPool));
        resetPool();
    }

    private void resetPool() {
        blueTeamBets.clear();
        redTeamBets.clear();
        totalPool = 0.0;
    }

    public void processRefund() {
        if (blueTeamBets.isEmpty() && redTeamBets.isEmpty()) {
            Bukkit.broadcastMessage("§c[PvP Bets] There are no active wagers to refund.");
            return;
        }

        for (Map.Entry<UUID, Double> entry : blueTeamBets.entrySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            getEconomy().depositPlayer(player, entry.getValue());
            if (player.isOnline()) {
                player.getPlayer().sendMessage("§6§l[PvP Bets] §aYour $" + String.format("%.2f", entry.getValue()) + " wager has been refunded.");
            }
        }

        for (Map.Entry<UUID, Double> entry : redTeamBets.entrySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            getEconomy().depositPlayer(player, entry.getValue());
            if (player.isOnline()) {
                player.getPlayer().sendMessage("§6§l[PvP Bets] §aYour $" + String.format("%.2f", entry.getValue()) + " wager has been refunded.");
            }
        }

        Bukkit.broadcastMessage("§6§l[PvP Bets] §cThe event has been cancelled. All wagers have been refunded.");
        resetPool();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bet") && sender instanceof Player) {
            Player player = (Player) sender;
            BettingMenu.openGUI(player);
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("payout")) {
            if (!sender.hasPermission("pvpbetting.admin")) {
                sender.sendMessage("§cYou do not have permission to run this command.");
                return true;
            }
            if (args.length != 1 || (!args[0].equalsIgnoreCase("blue") && !args[0].equalsIgnoreCase("red"))) {
                sender.sendMessage("§cUsage: /payout <blue|red>");
                return true;
            }
            
            processPayout(args[0]);
            return true;
        }

        if (command.getName().equalsIgnoreCase("refund")) {
            if (!sender.hasPermission("pvpbetting.admin")) {
                sender.sendMessage("§cYou do not have permission to run this command.");
                return true;
            }
            
            processRefund();
            return true;
        }

        if (command.getName().equalsIgnoreCase("pool")) {
            double blueTotal = blueTeamBets.values().stream().mapToDouble(Double::doubleValue).sum();
            double redTotal = redTeamBets.values().stream().mapToDouble(Double::doubleValue).sum();
            
            sender.sendMessage("§8§m----------------------------------------");
            sender.sendMessage("§6§lPvP Betting Pool Status:");
            sender.sendMessage("§9Blue Team Bets: §f$" + String.format("%.2f", blueTotal));
            sender.sendMessage("§cRed Team Bets: §f$" + String.format("%.2f", redTotal));
            sender.sendMessage("§aTotal Global Pool: §f$" + String.format("%.2f", totalPool));
            sender.sendMessage("§8§m----------------------------------------");
            
            return true;
        }
        
        return false;
    }
}
