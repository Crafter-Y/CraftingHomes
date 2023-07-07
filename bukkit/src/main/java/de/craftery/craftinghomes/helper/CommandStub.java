package de.craftery.craftinghomes.helper;

import de.craftery.craftinghomes.common.AbstractCommand;

public class CommandStub implements CommandExecutor, TabCompleter {
    private final AbstractCommand parent;
    public CommandStub(AbstractCommand parent) {
        this.parent = parent;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return this.parent.onCommand(new CommandSenderImpl(sender), args);      
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.parent.onTabComplete(new CommandSenderImpl(sender), args);
    }
}