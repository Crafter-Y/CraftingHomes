package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.annotation.annotations.Argument;
import de.craftery.craftinghomes.annotation.annotations.Command;
import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.i18n.I18n;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand {
    private final String name;
    protected I18n i18n;

    public AbstractCommand() {
        Command cmd = this.getClass().getAnnotation(Command.class);
        if (cmd == null) {
            throw new RuntimeException("Every command must be annotated with the @Command annotation.");
        }
        this.name = cmd.name();
    }

    public String getName() {
        return this.name;
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    private boolean setFields(CommandSenderI sender, String[] args) {
        int argIndex = 0;
        for (Field field : this.getClass().getDeclaredFields()) {
            Argument argument = field.getAnnotation(Argument.class);
            if (argument == null) continue;
            if (!field.getType().toString().equals("class java.lang.String")) {
                throw new RuntimeException("Every field annotated with @Argument must be of type String.");
            }
            String value = null;
            if (args.length > argIndex) {
                value = args[argIndex];
            } else if (!argument.defaultValue().isEmpty()) {
                value = argument.defaultValue();
            } else if (argument.required()) {
                sender.sendMessage(this.i18n.argumentIsRequired(field.getName()));
                return false;
            }

            try {
                boolean accessible = field.canAccess(this);
                field.setAccessible(true);
                field.set(this, value);
                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            argIndex++;
        }
        return true;
    }

    public boolean onCommand(CommandSenderI sender, String[] args) {
        boolean succeed = setFields(sender, args);
        if (!succeed) return true;

        return this.onCommand(sender);
    }

    public abstract boolean onCommand(CommandSenderI sender);
    public List<String> onTabComplete(CommandSenderI sender, String[] args) {
        boolean succeed = setFields(sender, args);
        if (!succeed) return new ArrayList<>();

        return this.onTabComplete(sender, args.length);
    }
    public abstract List<String> onTabComplete(CommandSenderI sender, int argLength);
}
