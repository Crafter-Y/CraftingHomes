package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.common.i18n.I18n;
import de.craftery.craftinghomes.common.storage.AbstractDataModel;
import de.craftery.craftinghomes.common.storage.DataStorageProvider;
import de.craftery.craftinghomes.common.storage.YmlDataStorageProvider;

import java.util.ArrayList;
import java.util.List;

public class Platform {
    private static ServerEntry server;
    private static final I18n i18n = new I18n();
    private static DataStorageProvider dataStorageProvider = null;

    private static final List<String> registeredDataModels = new ArrayList<>();

    public static void onEnable(ServerEntry entry) {
        server = entry;
        server.log("Plugin is starting up!");

        server.log("Registering i18n!");
        i18n.register();

        server.log("Registering data storage provider");
        registerDataStorageProvider();

        for (AbstractDataModel model : ReflectionUtil.getDataModels()) {
            if (registeredDataModels.contains(model.getQualifiedName())) {
                throw new RuntimeException("Duplicate data model " + model.getQualifiedName());
            }
            server.log("Registering data model " + model.getQualifiedName());

            registeredDataModels.add(model.getQualifiedName());

            dataStorageProvider.register(model);
        }

        for (AbstractCommand command : ReflectionUtil.getCommands()) {
            server.log("Registering command " + command.getName());
            command.setI18n(i18n);
            server.registerCommand(command);
        }
    }

    private static void registerDataStorageProvider() {
        String provider = server.getConfiguration().getString("storage", "yml");

        // TODO: register other data providers
        if (provider.equals("yml")) {
            dataStorageProvider = new YmlDataStorageProvider();
        }

        server.log("Registered data storage provider " + dataStorageProvider.getClass().getSimpleName());
    }

    public static void shutdown() {
        server.log("Plugin is shutting down!");
    }

    public static ServerEntry getServer() {
        return server;
    }

    public static DataStorageProvider getDataStorageProvider() {
        return dataStorageProvider;
    }
}
