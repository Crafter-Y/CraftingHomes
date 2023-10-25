package de.craftery.craftinghomes;

import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.CraftingLocation;
import de.craftery.craftinghomes.common.i18n.I18n;
import de.craftery.craftinghomes.impl.TestConsolePlayerImplementation;
import de.craftery.craftinghomes.impl.TestPlatform;
import de.craftery.craftinghomes.impl.TestPlayerImplementation;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class PlatformTest {
    private TestPlatform instance;

    @BeforeEach
    public void prepare() {
        instance = new TestPlatform();
        instance.onEnable();
    }

    @AfterEach
    public void cleanup() {
        instance.onDisable();
    }

    @Test
    public void homeModelTest() {
        Home home1 = new Home();
        assertThrows(RuntimeException.class, home1::save);

        Home home2 = new Home();

        home2.setName("home1");
        home2.setUuid("uuid-for-home-1");
        home2.setLocation(new CraftingLocation("world", 1.0, 2.0, 3.0, 4.0f, 5.0f));
        assertDoesNotThrow(home2::save);

        Home restoreHome2 = Home.getPlayerHome("uuid-for-home-1", "home1");
        assertNotNull(restoreHome2);
        assertEquals("home1", restoreHome2.getName());
        assertEquals("uuid-for-home-1", restoreHome2.getUuid());
        assertEquals("world", restoreHome2.getWorld());
        assertEquals(1.0, restoreHome2.getX());
        assertEquals(2.0, restoreHome2.getY());
        assertEquals(3.0, restoreHome2.getZ());
        assertEquals(4.0f, restoreHome2.getYaw());
        assertEquals(5.0f, restoreHome2.getPitch());

        restoreHome2.setWorld("world2");
        restoreHome2.setName("home2");
        assertThrows(RuntimeException.class, restoreHome2::save);
        assertDoesNotThrow(restoreHome2::saveOrUpdate);

        Home restoreHome3 = Home.getPlayerHome("uuid-for-home-1", "home2");
        assertNotNull(restoreHome3);
        assertEquals("home2", restoreHome3.getName());
        assertEquals("world2", restoreHome3.getWorld());

        restoreHome3.setName("home3");
        restoreHome3.setWorld("world3");
        assertDoesNotThrow(restoreHome3::update);

        Home restoreHome4 = Home.getPlayerHome("uuid-for-home-1", "home3");
        assertNotNull(restoreHome4);
        assertEquals("home3", restoreHome4.getName());
        assertEquals("world3", restoreHome4.getWorld());

        List<Home> allHomes = Home.getByField("uuid", "uuid-for-home-1");
        assertEquals(1, allHomes.size());

        restoreHome4.delete();
        assertNull(Home.getPlayerHome("uuid-for-home-1", "home3"));

        List<Home> allHomes2 = Home.getByField("uuid", "uuid-for-home-1");
        assertEquals(0, allHomes2.size());
    }

    @Test
    public void sethomeTest() {
        List<String> bypassPermission = new ArrayList<>();
        bypassPermission.add("craftinghomes.bypass");

        TestConsolePlayerImplementation console = new TestConsolePlayerImplementation(new ArrayList<>());

        TestPlayerImplementation player = new TestPlayerImplementation("SethomeTestPlayer", new ArrayList<>());
        player.teleport(new CraftingLocation("world", 1.0, 2.0, 3.0, 4.0f, 5.0f));

        TestPlayerImplementation bypassPlayer = new TestPlayerImplementation("SethomeTestPlayer2", bypassPermission);

        I18n i18n = Platform.getI18n();

        instance.executeCommand("sethome", console, new String[]{});
        assertEquals(i18n.senderNotPlayer(), console.popLastMessage());

        instance.executeCommand("sethome", console, new String[]{"home1"});
        assertEquals(i18n.senderNotPlayer(), console.popLastMessage());

        List<String> tabCompletion = instance.getCommandSuggestions("sethome", console, new String[]{""});
        assertEquals(0, tabCompletion.size());

        instance.executeCommand("sethome", player, new String[]{});
        assertEquals(i18n.homeSet("default"), player.popLastMessage());
        Home playerHome = Home.getPlayerHome(player, "default");
        assertNotNull(playerHome);

        instance.executeCommand("sethome", player, new String[]{"home2"});
        assertEquals(i18n.homeSet("home2"), player.popLastMessage());

        instance.executeCommand("sethome", player, new String[]{"home3"});
        assertEquals(i18n.homeSet("home3"), player.popLastMessage());

        instance.executeCommand("sethome", player, new String[]{"home4"});
        assertEquals(i18n.maxHomesReached(3), player.popLastMessage());

        instance.executeCommand("sethome", bypassPlayer, new String[]{"home1"});
        assertEquals(i18n.homeSet("home1"), bypassPlayer.popLastMessage());

        instance.executeCommand("sethome", bypassPlayer, new String[]{"home2"});
        assertEquals(i18n.homeSet("home2"), bypassPlayer.popLastMessage());

        instance.executeCommand("sethome", bypassPlayer, new String[]{"home3"});
        assertEquals(i18n.homeSet("home3"), bypassPlayer.popLastMessage());

        instance.executeCommand("sethome", bypassPlayer, new String[]{"home4"});
        assertEquals(i18n.homeSet("home4"), bypassPlayer.popLastMessage());

        List<String> tabCompletion2 = instance.getCommandSuggestions("sethome", player, new String[]{""});
        assertEquals(0, tabCompletion2.size());
    }

    @Test
    public void homeTest() {
        TestConsolePlayerImplementation console = new TestConsolePlayerImplementation(new ArrayList<>());
        TestPlayerImplementation player = new TestPlayerImplementation("HomeTestPlayer", new ArrayList<>());

        I18n i18n = Platform.getI18n();

        instance.executeCommand("sethome", console, new String[]{});
        assertEquals(i18n.senderNotPlayer(), console.popLastMessage());

        player.teleport(new CraftingLocation("world", 1.0, 2.0, 3.0, 4.0f, 5.0f));
        instance.executeCommand("sethome", player, new String[]{});

        player.teleport(new CraftingLocation("world", 2.0, 3.0, 4.0, 5.0f, 6.0f));
        instance.executeCommand("sethome", player, new String[]{"home2"});

        player.teleport(new CraftingLocation("world", 0, 0, 0, 0, 0));

        instance.executeCommand("home", player, new String[]{});
        assertEquals(i18n.teleportedToHome("default"), player.popLastMessage());
        assertEquals(1.0, player.getLocation().getX());

        instance.executeCommand("home", player, new String[]{"home2"});
        assertEquals(i18n.teleportedToHome("home2"), player.popLastMessage());
        assertEquals(2.0, player.getLocation().getX());

        instance.executeCommand("home", player, new String[]{"home3"});
        assertEquals(i18n.homeNotExisting("home3"), player.popLastMessage());

        List<String> tabCompletion = instance.getCommandSuggestions("home", player, new String[]{""});
        assertEquals(2, tabCompletion.size());

        List<String> tabCompletion2 = instance.getCommandSuggestions("home", player, new String[]{"home2", ""});
        assertEquals(0, tabCompletion2.size());
    }

    @Test
    public void delhomeTest() {
        TestPlayerImplementation player = new TestPlayerImplementation("DelhomeTestPlayer", new ArrayList<>());

        instance.executeCommand("delhome", player, new String[]{});
        assertEquals(Platform.getI18n().homeNotExisting("default"), player.popLastMessage());

        instance.executeCommand("sethome", player, new String[]{});
        assertEquals(Platform.getI18n().homeSet("default"), player.popLastMessage());

        instance.executeCommand("sethome", player, new String[]{"home2"});
        assertEquals(Platform.getI18n().homeSet("home2"), player.popLastMessage());

        List<String> tabCompletion = instance.getCommandSuggestions("delhome", player, new String[]{""});
        assertEquals(2, tabCompletion.size());

        instance.executeCommand("delhome", player, new String[]{"home2"});
        assertEquals(Platform.getI18n().homeDeleted("home2"), player.popLastMessage());
        Home deletedHome = Home.getPlayerHome(player, "home2");
        assertNull(deletedHome);

        instance.executeCommand("delhome", player, new String[]{});
        assertEquals(Platform.getI18n().homeDeleted("default"), player.popLastMessage());
        deletedHome = Home.getPlayerHome(player, "default");
        assertNull(deletedHome);

        List<String> tabCompletion2 = instance.getCommandSuggestions("delhome", player, new String[]{""});
        assertEquals(0, tabCompletion2.size());

        List<String> tabCompletion3 = instance.getCommandSuggestions("delhome", player, new String[]{"home2", ""});
        assertEquals(0, tabCompletion3.size());
    }

    @Test
    public void homesTest() {
        List<String> otherPermission = new ArrayList<>();
        otherPermission.add("craftinghomes.homes.other");
        List<String> bypassPermission = new ArrayList<>();
        bypassPermission.add("craftinghomes.bypass");

        TestPlayerImplementation player = new TestPlayerImplementation("HomesTestPlayer", new ArrayList<>());
        new TestPlayerImplementation("HomesNoHome", new ArrayList<>());
        TestPlayerImplementation manyHomePlayer = new TestPlayerImplementation("ManyHomePlayer", bypassPermission);
        TestPlayerImplementation privilegedPlayer = new TestPlayerImplementation("HomesTestPlayer2", otherPermission);
        TestConsolePlayerImplementation console = new TestConsolePlayerImplementation(otherPermission);

        Set<String> registeredHomes = new TreeSet<>();
        for (int i = 0; i < 100; i++) {
            registeredHomes.add("home" + i);
            manyHomePlayer.teleport(new CraftingLocation("world", i, i, i, 0, 0));
            instance.executeCommand("sethome", manyHomePlayer, new String[]{"home" + i});
        }

        instance.executeCommand("homes", console, new String[]{});
        assertEquals(Platform.getI18n().senderNotPlayer(), console.popLastMessage());

        instance.executeCommand("homes", player, new String[]{});
        assertEquals(Platform.getI18n().noHomes(), player.popLastMessage());

        player.teleport(new CraftingLocation("world", 1.0, 2.0, 3.0, 4.0f, 5.0f));
        instance.executeCommand("sethome", player, new String[]{});
        assertEquals(Platform.getI18n().homeSet("default"), player.popLastMessage());

        player.teleport(new CraftingLocation("world", 2.0, 3.0, 4.0, 5.0f, 6.0f));
        instance.executeCommand("sethome", player, new String[]{"home2"});
        assertEquals(Platform.getI18n().homeSet("home2"), player.popLastMessage());

        instance.executeCommand("homes", player, new String[]{});
        assertTrue(instance.hasOpenGui(player));
        assertEquals(2, instance.openGuiSlotCount(player));

        instance.clickGuiSlot(player, 0);
        assertEquals(Platform.getI18n().teleportedToHome("default"), player.popLastMessage());
        assertEquals(1.0, player.getLocation().getX());

        instance.executeCommand("homes", player, new String[]{"maybeExistingPlayer"});
        assertEquals(Platform.getI18n().noPermission(), player.popLastMessage());

        instance.executeCommand("homes", privilegedPlayer, new String[]{"notExistingPlayer"});
        assertEquals(Platform.getI18n().playerNeverOnline("notExistingPlayer"), privilegedPlayer.popLastMessage());

        instance.executeCommand("homes", privilegedPlayer, new String[]{"HomesTestPlayer"});
        assertTrue(instance.hasOpenGui(privilegedPlayer));
        assertEquals(2, instance.openGuiSlotCount(privilegedPlayer));

        instance.clickGuiSlot(privilegedPlayer, 0);
        assertEquals(Platform.getI18n().teleportedToOthersHome(player.getName(), "default"), privilegedPlayer.popLastMessage());
        assertEquals(1.0, privilegedPlayer.getLocation().getX());

        String innerHomesString = String.join(", ", registeredHomes);
        instance.executeCommand("homes", privilegedPlayer, new String[]{"ManyHomePlayer"});
        assertEquals(Platform.getI18n().playerHomes("ManyHomePlayer", innerHomesString), privilegedPlayer.popLastMessage());

        instance.executeCommand("homes", manyHomePlayer, new String[]{});
        assertEquals(Platform.getI18n().yourHomes(innerHomesString), manyHomePlayer.popLastMessage());

        instance.executeCommand("homes", console, new String[]{"HomesTestPlayer", "homename"});
        assertEquals(Platform.getI18n().senderNotPlayer(), console.popLastMessage());

        instance.executeCommand("homes", privilegedPlayer, new String[]{"HomesTestPlayer", "notExisting"});
        assertEquals(Platform.getI18n().homeNotExisting("notExisting"), privilegedPlayer.popLastMessage());

        instance.executeCommand("homes", privilegedPlayer, new String[]{"HomesTestPlayer", "home2"});
        assertEquals(Platform.getI18n().teleportedToOthersHome(player.getName(), "home2"), privilegedPlayer.popLastMessage());
        assertEquals(2.0, privilegedPlayer.getLocation().getX());

        instance.executeCommand("homes", privilegedPlayer, new String[]{"HomesNoHome"});
        assertEquals(Platform.getI18n().noHomesPlayer("HomesNoHome"), privilegedPlayer.popLastMessage());

        List<String> tabComplete1 = instance.getCommandSuggestions("homes", privilegedPlayer, new String[]{""});
        assertNull(tabComplete1);

        List<String> tabComplete2 = instance.getCommandSuggestions("homes", privilegedPlayer, new String[]{"HomesTestPlayer", ""});
        assertEquals(2, tabComplete2.size());

        List<String> tabComplete3 = instance.getCommandSuggestions("homes", player, new String[]{""});
        assertEquals(0, tabComplete3.size());
    }
}
