package de.craftery.craftinghomes.commands;

import de.craftery.craftinghomes.CraftingHomes;
import de.craftery.craftinghomes.Home;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HomesCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!").color(NamedTextColor.RED));
            return true;
        }

        List<Home> homes = CraftingHomes.getHomes(player);

        if (homes.isEmpty()) {
            player.sendMessage(Component.text("You don't have any homes!").color(NamedTextColor.RED));
            return true;
        }
        String homeNames = String.join(", ", homes.stream().map(Home::getName).toList());
        player.sendMessage(Component.text("Your homes: ").color(NamedTextColor.GREEN).append(Component.text(homeNames).color(NamedTextColor.AQUA)));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
