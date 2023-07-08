package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.common.api.CommandSenderI;

import java.util.List;

public abstract class AbstractCommand {
    private final String name;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract boolean onCommand(CommandSenderI sender, String[] args);
    public abstract List<String> onTabComplete(CommandSenderI sender, String[] args);
}
