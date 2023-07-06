package de.craftery.craftinghomes.common;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
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

    public static Set<? extends AbstractCommand> getCommands() {
        if (commands == null) {
            try {
                commands = serviceLoad(AbstractCommand.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load features", e);
            }
        }
        return commands;
    }

    private static <T> Set<? extends T> serviceLoad(Class<T> clazz) throws IOException {
        ClassLoader cl = ReflectionUtil.class.getClassLoader();
        Enumeration<URL> serviceFiles = cl.getResources("META-INF/services/" + clazz.getName());
        Set<String> classNames = new HashSet<>();
        while (serviceFiles.hasMoreElements()) {
            URL serviceFile = serviceFiles.nextElement();
            try (var reader = serviceFile.openStream()) {
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

    @SuppressWarnings("unchecked")
    private static <T> @Nullable Class<? extends T> loadClass(String className) {
        try {
            return (Class<? extends T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class " + className, e);
        } catch (NoClassDefFoundError e) {
            Platform.getServer().log(String.format("Failed to load class {}, required {}", className, e.getMessage()));
        }
        return null;
    }

}