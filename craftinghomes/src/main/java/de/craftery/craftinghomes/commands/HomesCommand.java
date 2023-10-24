package de.craftery.craftinghomes.commands;

import de.craftery.craftinghomes.Home;
import de.craftery.craftinghomes.annotation.annotations.Argument;
import de.craftery.craftinghomes.annotation.annotations.Command;
import de.craftery.craftinghomes.common.AbstractCommand;
import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.api.OfflinePlayerI;
import de.craftery.craftinghomes.common.api.PlayerI;
import de.craftery.craftinghomes.common.gui.GuiBuilder;
import de.craftery.craftinghomes.common.gui.GuiItem;
import de.craftery.craftinghomes.common.gui.GuiItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "homes")
@SuppressWarnings("unused")
public class HomesCommand extends AbstractCommand {
    @Argument(required = false)
    private String targetPlayer;

    @Argument(required = false)
    private String targetHome;

    @Override
    public boolean onCommand(CommandSenderI sender) {
        List<Home> homes;

        if (targetPlayer == null) {
            if (!(sender instanceof PlayerI)) {
                sender.sendMessage(this.i18n.senderNotPlayer());
                return true;
            }
            PlayerI player = (PlayerI) sender;

            homes = Home.getByPlayer(player);

            if (homes.isEmpty()) {
                sender.sendMessage(this.i18n.noHomes());
                return true;
            }

            if (homes.size() > 9*6) {
                String homeNames = homes.stream().map(Home::getName).collect(Collectors.joining(", "));
                sender.sendMessage(this.i18n.yourHomes(homeNames));
                return true;
            }

            GuiBuilder gui = new GuiBuilder(this.i18n.homeTitle(), 6);
            for (Home home : homes) {
                GuiItemType type = GuiItemType.OVERWORLD;
                if (home.getWorld().endsWith("_nether")) type = GuiItemType.NETHER;
                if (home.getWorld().endsWith("_the_end")) type = GuiItemType.END;

                List<String> lores = new ArrayList<>();
                lores.add(this.i18n.positionLore(home.getX().intValue(), home.getY().intValue(), home.getZ().intValue()));
                lores.add(this.i18n.worldLore(home.getWorld()));

                gui.addSlot(new GuiItem(type, "&a" + home.getName(), lores, player.getUniqueId() + home.getName(), () -> {
                    player.teleport(home.getLocation());
                    player.sendMessage(this.i18n.teleportedToHome(home.getName()));
                }));
            }
            gui.open(player);

        } else {
            if (!sender.hasPermission("craftinghomes.homes.other")) {
                sender.sendMessage(this.i18n.noPermission());
                return true;
            }
            OfflinePlayerI player = Platform.getServer().getOfflinePlayer(targetPlayer);
            if (player == null) {
                sender.sendMessage(this.i18n.playerNeverOnline(targetPlayer));
                return true;
            }

            if (targetHome != null) {
                if (!(sender instanceof PlayerI)) {
                    sender.sendMessage(this.i18n.senderNotPlayer());
                    return true;
                }
                PlayerI origin = (PlayerI) sender;

                Home home = Home.getPlayerHome(player.getUniqueId(), targetHome);
                if (home == null) {
                    origin.sendMessage(this.i18n.homeNotExisting(targetHome));
                    return true;
                }

                origin.teleport(home.getLocation());
                origin.sendMessage(this.i18n.teleportedToOthersHome(player.getName(), targetHome));
                return true;
            }

            homes = Home.getByField("uuid", player.getUniqueId());

            if (homes.isEmpty()) {
                sender.sendMessage(this.i18n.noHomesPlayer(player.getName()));
                return true;
            }

            if (homes.size() > 9*6 || !(sender instanceof PlayerI)) {
                String homeNames = homes.stream().map(Home::getName).collect(Collectors.joining(", "));
                sender.sendMessage(this.i18n.playerHomes(player.getName(), homeNames));
                return true;
            }
            PlayerI origin = (PlayerI) sender;

            GuiBuilder gui = new GuiBuilder(this.i18n.otherPlayersHome(player.getName()), 6);
            for (Home home : homes) {
                GuiItemType type = GuiItemType.OVERWORLD;
                if (home.getWorld().endsWith("_nether")) type = GuiItemType.NETHER;
                if (home.getWorld().endsWith("_the_end")) type = GuiItemType.END;

                List<String> lores = new ArrayList<>();
                lores.add(this.i18n.positionLore(home.getX().intValue(), home.getY().intValue(), home.getZ().intValue()));
                lores.add(this.i18n.worldLore(home.getWorld()));

                gui.addSlot(new GuiItem(type, "&a" + home.getName(), lores, player.getUniqueId() + home.getName(), () -> {
                    origin.teleport(home.getLocation());
                    origin.sendMessage(this.i18n.teleportedToOthersHome(player.getName(), home.getName()));
                }));
            }
            gui.open(origin);

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSenderI sender, int argLength) {
        if (argLength == 1 && sender.hasPermission("craftinghomes.homes.other")) {
            return null;
        }
        if (argLength == 2 && sender.hasPermission("craftinghomes.homes.other")) {
            OfflinePlayerI player = Platform.getServer().getOfflinePlayer(targetPlayer);
            if (player == null) return new ArrayList<>();
            return Home.getByField("uuid", player.getUniqueId()).stream().map(Home::getName).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
