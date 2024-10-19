package io.karma.moreprotectables.init;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.blockentity.SimpleKeypadDoorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public final class ModBlockEntities {
    public static final HashMap<WoodType, RegistryObject<BlockEntityType<SimpleKeypadDoorBlockEntity>>> KEYPAD_WOOD_DOOR = new HashMap<>();

    // @formatter:off
    private ModBlockEntities() {}
    // @formatter:on

    public static void register() {
        for (final var woodType : MoreProtectables.WOOD_TYPES) {
            final var name = String.format("keypad_%s_door", woodType.name());
            KEYPAD_WOOD_DOOR.put(woodType,
                MoreProtectables.blockEntity(name,
                    Objects.requireNonNull(ModBlocks.KEYPAD_WOOD_DOOR.get(woodType)),
                    (pos, state) -> new SimpleKeypadDoorBlockEntity(KEYPAD_WOOD_DOOR.get(woodType).get(), pos, state)));
        }
    }
}
