package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.common.i18n.I18n;

public class Platform {
    private static ServerEntry server;
    private static final I18n i18n = new I18n();

    public static void onEnable(ServerEntry entry) {
        server = entry;
        server.log("CraftingHomes is starting up!");

        server.log("Registering i18n!");
        i18n.register();

        for (AbstractCommand command : ReflectionUtil.getCommands()) {
            server.log("Registering command " + command.getName());
            command.setI18n(i18n);
            server.registerCommand(command);
        }
    }

    public static void shutdown() {
        server.log("CraftingHomes is shutting down!");
    }

    public static ServerEntry getServer() {
        return server;
    }
}
