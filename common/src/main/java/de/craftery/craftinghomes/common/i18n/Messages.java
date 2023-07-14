package de.craftery.craftinghomes.common.i18n;

import de.craftery.craftinghomes.annotation.annotations.I18nDef;
import de.craftery.craftinghomes.annotation.annotations.I18nSource;

@I18nSource(name = "I18n")
public interface Messages {
    @I18nDef(def = "&cYou must be a player to use this command!")
    String senderNotPlayer();
    @I18nDef(def = "&cHome with the name &b${homeName} &cdoes not exist!")
    String homeNotExisting(String homeName);
    @I18nDef(def = "&aHome &b${homeName} &adeleted!")
    String homeDeleted(String homeName);
    @I18nDef(def = "&aYou have been teleported to your home &b${homeName}&a!")
    String teleportedToHome(String homeName);
    @I18nDef(def = "&cYou don't have any homes!")
    String noHomes();
    @I18nDef(def = "&cPlayer &b${player} &chas no homes!")
    String noHomesPlayer(String player);
    @I18nDef(def = "&aYour homes: &b${homeNames}")
    String yourHomes(String homeNames);
    @I18nDef(def = "&a${playerName}'s homes: &b${homeNames}")
    String playerHomes(String playerName, String homeNames);
    @I18nDef(def = "&cYou can only have ${maxHomes} homes!")
    String maxHomesReached(Integer maxHomes);
    @I18nDef(def = "&cYou must provide the argument ${argument}!")
    String argumentIsRequired(String argument);
    @I18nDef(def = "&cThe player ${player} was never online!")
    String playerNeverOnline(String player);
    @I18nDef(def = "&cYou dont have permission to use this command!")
    String noPermission();

}
