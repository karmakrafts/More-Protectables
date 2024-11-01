package io.karma.moreprotectables.block;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.client.hook.BlockColorsHooks;
import io.karma.moreprotectables.client.hook.ItemColorsHooks;
import io.karma.moreprotectables.client.model.ModelPatcher;
import io.karma.moreprotectables.item.ReinforcedShadowBlockItem;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Alexander Hinze
 * @since 27/10/2024
 */
public final class ReinforcedBlockGenerator {
    public static final ReinforcedBlockGenerator INSTANCE = new ReinforcedBlockGenerator();

    @OnlyIn(Dist.CLIENT)
    private static final BlockColor DEFAULT_BLOCK_COLOR = (state, level, pos, index) -> -1;
    @OnlyIn(Dist.CLIENT)
    private static final ItemColor DEFAULT_ITEM_COLOR = (stack, index) -> -1;

    private final HashSet<String> namespaceBlacklist = new HashSet<>();
    private final ArrayList<Pattern> whitelistPatterns = new ArrayList<>();
    private final LinkedHashMap<ResourceLocation, ReinforcedShadowBlock> generatedBlocks = new LinkedHashMap<>();
    private final ArrayList<ReinforcedShadowBlockItem> generatedItems = new ArrayList<>();

    private ReinforcedBlockGenerator() {
        namespaceBlacklist.add("minecraft");
        namespaceBlacklist.add(MoreProtectables.MODID);
        namespaceBlacklist.add(SecurityCraft.MODID);
    }

    @OnlyIn(Dist.CLIENT)
    private static BlockColor handleShadowBlockColor(final @Nullable BlockColor color) {
        if (color == null) {
            return DEFAULT_BLOCK_COLOR;
        }
        return (state, level, pos, index) -> {
            if (!(state.getBlock() instanceof IReinforcedBlock reinforcedBlock)) {
                return -1;
            }
            return color.getColor(reinforcedBlock.getVanillaBlock().withPropertiesOf(state), level, pos, index);
        };
    }

    @OnlyIn(Dist.CLIENT)
    private static BlockColor handleShadowGrassBlockColor(final @Nullable BlockColor color) {
        return (state, level, pos, index) -> {
            // @formatter:off
            return index == 1 && !state.getValue(SnowyDirtBlock.SNOWY)
                ? BiomeColors.getAverageGrassColor(Objects.requireNonNull(level), Objects.requireNonNull(pos))
                : -1;
            // @formatter:on
        };
    }

    @OnlyIn(Dist.CLIENT)
    private static BlockColor handleShadowWaterCauldronBlockColor(final @Nullable BlockColor color) {
        return (state, level, pos, index) -> {
            // @formatter:off
            return index == 1
                ? BiomeColors.getAverageWaterColor(Objects.requireNonNull(level), Objects.requireNonNull(pos))
                : -1;
            // @formatter:on
        };
    }

    @OnlyIn(Dist.CLIENT)
    private static ItemColor handleShadowBlockItemColor(final @Nullable ItemColor color) {
        if (color == null) {
            return DEFAULT_ITEM_COLOR;
        }
        return (stack, index) -> {
            final var block = Block.byItem(stack.getItem());
            if (!(block instanceof IReinforcedBlock reinforcedBlock)) {
                return -1;
            }
            final var shadowedBlock = reinforcedBlock.getVanillaBlock();
            final var shadowedStack = new ItemStack(shadowedBlock);
            return color.getColor(shadowedStack, index);
        };
    }

    @OnlyIn(Dist.CLIENT)
    private static ItemColor handleShadowTintedBlockItemColor(final @Nullable ItemColor color) {
        if (color == null) {
            return DEFAULT_ITEM_COLOR;
        }
        return (stack, index) -> {
            final var block = Block.byItem(stack.getItem());
            if (!(block instanceof IReinforcedBlock reinforcedBlock)) {
                return -1;
            }
            final var shadowedBlock = reinforcedBlock.getVanillaBlock();
            final var shadowedStack = new ItemStack(shadowedBlock);
            return index == 1 ? color.getColor(shadowedStack, index) : -1;
        };
    }

    public void whitelist(final @Language("RegExp") String pattern) {
        whitelistPatterns.add(Pattern.compile(pattern));
    }

    @Internal
    public void init() {
        final var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegisterBlocks);
        bus.addListener(this::onRegisterItems);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::onClientSetup));
    }

    @SuppressWarnings("deprecation")
    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            final var game = Minecraft.getInstance();
            final var blockColors = game.getBlockColors();
            final var itemColors = game.getItemColors();
            // Copy render properties for generated blocks
            for (final var block : generatedBlocks.values()) {
                // Copy over render types
                final var shadowedBlock = block.getVanillaBlock();
                ItemBlockRenderTypes.setRenderLayer(block,
                    ItemBlockRenderTypes.getRenderLayers(shadowedBlock.defaultBlockState()));
                // Copy over block color handler(s)
                final var colorProvider = ((BlockColorsHooks) blockColors).moreprotectable$getBlockColor(shadowedBlock);
                if (colorProvider == null) {
                    continue;
                }
                blockColors.register(handleShadowBlockColor(colorProvider), block);
            }
            // Override color handlers from builtin reinforced blocks
            for (final var entry : ForgeRegistries.BLOCKS.getEntries()) {
                final var block = entry.getValue();
                if (!(block instanceof IReinforcedBlock reinforcedBlock)) {
                    continue;
                }
                if (!entry.getKey().location().getNamespace().equals(SecurityCraft.MODID)) {
                    continue;
                }
                final var shadowedBlock = reinforcedBlock.getVanillaBlock();
                // Handle block colors
                var blockColor = ((BlockColorsHooks) blockColors).moreprotectable$getBlockColor(block);
                if (blockColor != null) {
                    if (shadowedBlock == Blocks.GRASS_BLOCK) {
                        blockColors.register(handleShadowGrassBlockColor(((BlockColorsHooks) blockColors).moreprotectable$getBlockColor(
                            shadowedBlock)), block);
                    }
                    else if (shadowedBlock == Blocks.WATER_CAULDRON) {
                        blockColors.register(handleShadowWaterCauldronBlockColor(((BlockColorsHooks) blockColors).moreprotectable$getBlockColor(
                            shadowedBlock)), block);
                    }
                    else {
                        blockColors.register(handleShadowBlockColor(((BlockColorsHooks) blockColors).moreprotectable$getBlockColor(
                            shadowedBlock)), block);
                    }
                }
                // Handle item colors
                if (!ForgeRegistries.ITEMS.containsKey(ForgeRegistries.BLOCKS.getKey(block))) {
                    continue;
                }
                final var item = block.asItem();
                final var itemColor = ((ItemColorsHooks) itemColors).moreprotectables$getItemColor(item);
                if (itemColor == null) {
                    continue;
                }
                if (shadowedBlock == Blocks.GRASS_BLOCK || shadowedBlock == Blocks.WATER_CAULDRON) {
                    itemColors.register(handleShadowTintedBlockItemColor(((ItemColorsHooks) itemColors).moreprotectables$getItemColor(
                        shadowedBlock.asItem())), item);
                    continue;
                }
                itemColors.register(handleShadowBlockItemColor(((ItemColorsHooks) itemColors).moreprotectables$getItemColor(
                    shadowedBlock.asItem())), item);
            }
        });
    }

    private boolean shouldGenerate(final @Nullable Block block) {
        final var key = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
        // Handle blacklist
        if (namespaceBlacklist.contains(key.getNamespace())) {
            return false;
        }
        // Handle whitelist
        final var keyString = key.toString();
        for (final var pattern : whitelistPatterns) {
            if (!pattern.matcher(keyString).find()) {
                continue;
            }
            return true;
        }
        // @formatter:off
        return !(block == null || block == Blocks.AIR
            || block instanceof EntityBlock
            || block instanceof LiquidBlock
            || block instanceof IPlantable
            || block instanceof FlowerPotBlock
            || block instanceof DoorBlock
            || block instanceof TrapDoorBlock
            || block instanceof WallTorchBlock
            || block instanceof RedstoneWallTorchBlock
            || block.isRandomlyTicking);
        // @formatter:on
    }

    private void onRegisterBlocks(final RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.BLOCKS.getRegistryKey())) {
            return;
        }
        final IForgeRegistry<Block> registry = Objects.requireNonNull(event.getForgeRegistry());
        final var entries = new LinkedHashSet<>(registry.getEntries());
        for (final var entry : entries) {
            final var key = entry.getKey().location();
            final var block = entry.getValue();
            if (!shouldGenerate(block)) {
                continue;
            }
            final var location = new ResourceLocation(MoreProtectables.MODID,
                String.format("reinforced_%s_%s", key.getNamespace(), key.getPath()));
            final var reinforcedBlock = new ReinforcedShadowBlock(block);
            registry.register(location, reinforcedBlock);
            reinforcedBlock.initAfterRegister();
            generatedBlocks.put(location, reinforcedBlock);

            IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.put(block, reinforcedBlock);
            IReinforcedBlock.SECURITYCRAFT_TO_VANILLA.put(reinforcedBlock, block);

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ModelPatcher.INSTANCE.suppressBlockErrors(reinforcedBlock));
        }
    }

    private void onRegisterItems(final RegisterEvent event) {
        if (!event.getRegistryKey().equals(ForgeRegistries.ITEMS.getRegistryKey())) {
            return;
        }
        final IForgeRegistry<Item> registry = Objects.requireNonNull(event.getForgeRegistry());
        for (final var entry : generatedBlocks.entrySet()) {
            if (!registry.containsKey(ForgeRegistries.BLOCKS.getKey(entry.getValue().getVanillaBlock()))) {
                continue; // Skip any blocks that don't have an item
            }
            final var item = new ReinforcedShadowBlockItem(entry.getValue(), new Item.Properties());
            registry.register(entry.getKey(), item);
            generatedItems.add(item);
        }
    }

    public ArrayList<ReinforcedShadowBlockItem> getGeneratedItems() {
        return generatedItems;
    }

    public LinkedHashMap<ResourceLocation, ReinforcedShadowBlock> getGeneratedBlocks() {
        return generatedBlocks;
    }
}
