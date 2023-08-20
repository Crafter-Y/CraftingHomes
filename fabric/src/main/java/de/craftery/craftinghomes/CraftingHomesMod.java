package de.craftery.craftinghomes;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.craftery.craftinghomes.common.AbstractCommand;
import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.ConfigurationI;
import de.craftery.craftinghomes.common.api.OfflinePlayerI;
import de.craftery.craftinghomes.common.api.PlayerI;
import de.craftery.craftinghomes.common.gui.GuiBuilder;
import de.craftery.craftinghomes.impl.FabricCommandSenderImpl;
import de.craftery.craftinghomes.impl.FabricConfigurationImpl;
import de.craftery.craftinghomes.impl.FabricPlayerImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.craftery.craftinghomes.common.ServerEntry;
import static net.minecraft.server.command.CommandManager.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class CraftingHomesMod implements ModInitializer, ServerEntry {
    public static final Logger LOGGER = LoggerFactory.getLogger("craftinghomes");

    private Path workingDir;

    public void createWorkingDir () {
        workingDir = FabricLoader.getInstance().getConfigDir().resolve("craftinghomes");
        if (!Files.exists(workingDir)) {
            try {
                Files.createDirectory(workingDir);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    public void onInitialize() {
        createWorkingDir();
        Platform.onEnable(this);
    }

    @Override
    public void log(String message) {
        LOGGER.info(message);
    }

    @Override
    public void registerCommand(AbstractCommand command) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<ServerCommandSource> builder = literal(command.getName());

            builder.executes(context -> callCommand(command, context.getSource(), new String[0]));

            if (command.getArguments().size() > 0) {
                dispatcher.register(builder.then(buildLiteral(0, builder, command)));
            } else {
                dispatcher.register(builder);
            }
        });
    }

    private int callCommand (AbstractCommand command, ServerCommandSource source, String[] args) {
        if (source.isExecutedByPlayer()) {
            boolean succeed = command.onCommand(new FabricPlayerImpl(source), args);
            return succeed ? 1 : 0;
        } else {
            boolean succeed = command.onCommand(new FabricCommandSenderImpl(source), args);
            return succeed ? 1 : 0;
        }
    }

    private List<String> callTabCompleter (AbstractCommand command, ServerCommandSource source, String[] args) {
        if (source.isExecutedByPlayer()) {
            return command.onTabComplete(new FabricPlayerImpl(source), args);
        } else {
            return command.onTabComplete(new FabricCommandSenderImpl(source), args);
        }
    }

    private <T extends ArgumentBuilder<ServerCommandSource, ?>> T buildLiteral (int index, T builder, AbstractCommand command) {
        RequiredArgumentBuilder<ServerCommandSource, String> arg = argument(Integer.toString(index), StringArgumentType.word())
                .executes(context -> {
                    String[] args = new String[index + 1];
                    for (int i = 0; i <= index; i++) {
                        args[i] = context.getArgument(Integer.toString(i), String.class);
                    }
                    return callCommand(command, context.getSource(), args);
                }).suggests((context, builder1) -> {
                    String[] args = new String[index + 1];
                    for (int i = 0; i < index; i++) {
                        args[i] = context.getArgument(Integer.toString(i), String.class);
                    }

                    System.out.println("args: " + Arrays.toString(args));
                    // TODO: fix tab completion

                    List<String> suggestions = callTabCompleter(command, context.getSource(), args);
                    if (suggestions != null) suggestions.forEach(builder1::suggest);
                    return builder1.buildFuture();
                });
        if (index < command.getArguments().size() - 1) {
            arg.then(buildLiteral(index + 1, builder.then(arg), command));
        }
        return (T) arg;
    }

    @Override
    public ConfigurationI getConfiguration() {
        return new FabricConfigurationImpl(workingDir);
    }

    @Override
    public ConfigurationI getConfiguration(String configFileName) {
        return new FabricConfigurationImpl(workingDir, configFileName);
    }

    @Override
    public @Nullable OfflinePlayerI getOfflinePlayer(String name) {
        // https://www.curseforge.com/minecraft/mc-mods/namefabric/files/4651876
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void openGui(PlayerI player, GuiBuilder builder) {
        throw new UnsupportedOperationException("Not implemented");
    }
}