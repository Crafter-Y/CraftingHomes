package de.craftery.craftinghomes.commands;

import com.google.auto.service.AutoService;
import de.craftery.craftinghomes.CraftingHomes;
import de.craftery.craftinghomes.Home;
import de.craftery.craftinghomes.common.AbstractCommand;
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

@AutoService(AbstractCommand.class)
public class DelhomeCommand extends AbstractCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!").color(NamedTextColor.RED));
            return true;
        }

        String homeName;
        if (args.length == 0) {
            homeName = "default";
        } else {
            homeName = args[0];
        }

        Home home = CraftingHomes.getHome(player, homeName);
        if (home == null) {
            player.sendMessage(Component.text("Home with the name ").color(NamedTextColor.RED).append(Component.text(homeName).color(NamedTextColor.AQUA)).append(Component.text(" does not exist!").color(NamedTextColor.RED)));
            return true;
        }

        CraftingHomes.deleteHome(player, home);
        player.sendMessage(Component.text("Home ").color(NamedTextColor.GREEN).append(Component.text(homeName).color(NamedTextColor.AQUA)).append(Component.text(" deleted!").color(NamedTextColor.GREEN)));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }
        if (args.length == 1) {
            return CraftingHomes.getHomes(player).stream().map(Home::getName).toList();
        }

        return new ArrayList<>();
    }
}