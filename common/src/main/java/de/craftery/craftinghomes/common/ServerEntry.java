package de.craftery.craftinghomes.common;

public interface ServerEntry {
    void log(String message);
    void registerCommand(AbstractCommand command);
}
