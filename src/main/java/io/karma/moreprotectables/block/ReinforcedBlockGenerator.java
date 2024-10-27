package io.karma.moreprotectables.block;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.item.ReinforcedShadowBlockItem;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * @author Alexander Hinze
 * @since 27/10/2024
 */
public final class ReinforcedBlockGenerator {
    public static final ReinforcedBlockGenerator INSTANCE = new ReinforcedBlockGenerator();
    private final HashSet<String> namespaceBlacklist = new HashSet<>();
    private final LinkedHashMap<ResourceLocation, ReinforcedShadowBlock> generatedBlocks = new LinkedHashMap<>();

    private ReinforcedBlockGenerator() {
        namespaceBlacklist.add("minecraft");
        namespaceBlacklist.add(MoreProtectables.MODID);
        namespaceBlacklist.add(SecurityCraft.MODID);
    }

    public void setup() {
        final var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegisterBlocks);
        bus.addListener(this::onRegisterItems);
    }

    private void onRegisterBlocks(final RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.BLOCKS.getRegistryKey())) {
            return;
        }
        final var entries = new LinkedHashSet<>(ForgeRegistries.BLOCKS.getEntries());
        for (final var entry : entries) {
            final var key = entry.getKey().location();
            if (namespaceBlacklist.contains(key.getNamespace())) {
                continue;
            }
            final var block = entry.getValue();
            if (block == null || block instanceof AirBlock || block instanceof EntityBlock || block.hasDynamicShape()) {
                continue;
            }
            MoreProtectables.LOGGER.debug("Generating reinforced variant for {}", key);
            final var location = new ResourceLocation(MoreProtectables.MODID,
                String.format("reinforced_%s_%s", key.getNamespace(), key.getPath()));
            final var reinforcedBlock = new ReinforcedShadowBlock(() -> ForgeRegistries.BLOCKS.getValue(key));
            ForgeRegistries.BLOCKS.register(location, reinforcedBlock);
            generatedBlocks.put(location, reinforcedBlock);
        }
    }

    private void onRegisterItems(final RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.ITEMS.getRegistryKey())) {
            return;
        }
        for (final var entry : generatedBlocks.entrySet()) {
            ForgeRegistries.ITEMS.register(entry.getKey(),
                new ReinforcedShadowBlockItem(entry.getValue(), new Item.Properties()));
        }
    }

    public LinkedHashMap<ResourceLocation, ReinforcedShadowBlock> getGeneratedBlocks() {
        return generatedBlocks;
    }
}
