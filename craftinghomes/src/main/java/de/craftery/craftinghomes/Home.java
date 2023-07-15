package de.craftery.craftinghomes;

import de.craftery.craftinghomes.annotation.annotations.DataModel;
import de.craftery.craftinghomes.annotation.annotations.Column;
import de.craftery.craftinghomes.common.api.CraftingLocation;
import de.craftery.craftinghomes.common.api.PlayerI;
import de.craftery.craftinghomes.common.storage.AbstractDataModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@DataModel
public class Home extends AbstractDataModel {
    @Column
    private String name;
    @Column
    private String uuid;

    @Column
    private String world;
    @Column
    private Double x;
    @Column
    private Double y;
    @Column
    private Double z;
    @Column
    private Float yaw;
    @Column
    private Float pitch;

    public CraftingLocation getLocation() {
        return new CraftingLocation(this.world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public void setPlayer(PlayerI player) {
        this.uuid = player.getUniqueId();
    }

    public void setLocation(CraftingLocation location) {
        this.world = location.getWorld();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public static List<Home> getByField(String field, Object value) {
        return AbstractDataModel.getByField(Home.class, field, value);
    }

    public static @Nullable Home getPlayerHome(String uuid, String name) {
        return Home.getByField("uuid", uuid).stream().filter(h -> h.getName().equals(name)).findFirst().orElse(null);
    }

    public static @Nullable Home getPlayerHome(PlayerI player, String name) {
        return Home.getPlayerHome(player.getUniqueId(), name);
    }

    public static List<Home> getByPlayer(PlayerI player) {
        return Home.getByField("uuid", player.getUniqueId());
    }
}
