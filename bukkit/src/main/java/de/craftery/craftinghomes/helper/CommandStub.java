package de.craftery.craftinghomes.helper;

import de.craftery.craftinghomes.common.AbstractCommand;
import de.craftery.craftinghomes.impl.CommandSenderImpl;
import de.craftery.craftinghomes.impl.PlayerImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandStub implements CommandExecutor, TabCompleter {
    private final AbstractCommand parent;
    public CommandStub(AbstractCommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            return this.parent.onCommand(new PlayerImpl(sender), args);
        }
        return this.parent.onCommand(new CommandSenderImpl(sender), args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            return this.parent.onTabComplete(new PlayerImpl(sender), args);
        }
        return this.parent.onTabComplete(new CommandSenderImpl(sender), args);
    }
}