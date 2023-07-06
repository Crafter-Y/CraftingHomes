package de.craftery.craftinghomes.common;

public class Platform {
    private static ServerEntry server;

    public static void onEnable(ServerEntry entry) {
        server = entry;
        server.log("CraftingHomes is starting up!");

        for (AbstractCommand command : ReflectionUtil.getCommands()) {
            server.log(command.getClass().getName());
        }
    }

    public static void shutdown() {
        server.log("CraftingHomes is shutting down!");
    }

    public static ServerEntry getServer() {
        return server;
    }
}
