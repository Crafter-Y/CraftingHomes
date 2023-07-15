package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.common.storage.AbstractDataModel;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// This class is highly inspired by the ReflectionUtil class from the redstonetools-mod
// (https://github.com/RedstoneTools/redstonetools-mod)
public class ReflectionUtil {
    private static Set<? extends AbstractCommand> commands;
    private static Set<? extends AbstractDataModel> dataModels;

    public static Set<? extends AbstractCommand> getCommands() {
        if (commands == null) {
            try {
                commands = discoverAndInstantiateClasses("commands.txt", AbstractCommand.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load features", e);
            }
        }
        return commands;
    }

    public static Set<? extends AbstractDataModel> getDataModels() {
        if (dataModels == null) {
            try {
                dataModels = discoverAndInstantiateClasses("datasources.txt", AbstractDataModel.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load features", e);
            }
        }
        return dataModels;
    }

    private static <T> Set<? extends T> discoverAndInstantiateClasses(String discoverFile, Class<T> clazz) throws IOException {
        ClassLoader cl = ReflectionUtil.class.getClassLoader();
        Enumeration<URL> commandsFile = cl.getResources(discoverFile);
        Set<String> classNames = new HashSet<>();
        if (commandsFile.hasMoreElements()) {
            URL serviceFile = commandsFile.nextElement();
            try (InputStream reader = serviceFile.openStream()) {
                classNames.addAll(IOUtils.readLines(reader, "UTF-8"));
            }
        }
        return classNames.stream()
                .filter(it -> !it.isEmpty() && !it.isBlank())
                .map(ReflectionUtil::loadClass)
                .filter(Objects::nonNull)
                .filter(clazz::isAssignableFrom)
                .map(it -> {
                    try {
                        return it.getDeclaredConstructor().newInstance();
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("Failed to instantiate " + it, e);
                    }
                })
                .map(clazz::cast)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings({"unchecked", "unused"})
    private static <T> @Nullable Class<? extends T> loadClass(String className) {
        try {
            return (Class<? extends T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class " + className, e);
        } catch (NoClassDefFoundError e) {
            Platform.getServer().log("Failed to load class "+ className +", required " + e.getMessage());
        }
        return null;
    }
}
