package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.i18n.I18n;

import java.util.List;

public abstract class AbstractCommand {
    private final String name;
    protected I18n i18n;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    public abstract boolean onCommand(CommandSenderI sender, String[] args);
    public abstract List<String> onTabComplete(CommandSenderI sender, String[] args);
}
