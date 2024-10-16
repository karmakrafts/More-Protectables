/*
 * Copyright 2024 Karma Krafts & associates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.karma.moreprotectables;

import io.karma.moreprotectables.client.render.ChestKeypadRenderer;
import io.karma.moreprotectables.compat.CompatibilityModule;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 09/10/2024
 */
@Mod(MoreProtectables.MODID)
public class MoreProtectables {
    public static final String MODID = "moreprotectables";
    public static final Logger LOGGER = LogManager.getLogger("More Protectables");
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,
        MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
        MODID);
    // @formatter:off
    private static final List<CompatibilityModule> COMPAT_MODULES = CompatibilityModule.loadAll()
        .filter(provider -> FMLLoader.getLoadingModList().getModFileById(CompatibilityModule.getModId(provider.type())) != null)
        .map(Provider::get)
        .toList();
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register(MODID, () -> CreativeModeTab.builder()
        .icon(() -> new ItemStack(SCContent.KEY_PANEL.get()))
        .title(Component.translatable(String.format("itemGroup.%s", MODID)))
        .displayItems((params, output) -> {
            for(final var module : COMPAT_MODULES) {
                module.addItemsToTab(output);
            }
        })
        .build());
    // @formatter:on

    public MoreProtectables() {
        COMPAT_MODULES.forEach(CompatibilityModule::init);
        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ChestKeypadRenderer.INSTANCE.setup();
            modBus.addListener(this::onClientSetup);
        });

        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        ITEMS.register(modBus);
        TABS.register(modBus);
    }

    public static <B extends Block> RegistryObject<B> block(final String name,
                                                            final Supplier<B> supplier,
                                                            final BiFunction<Block, Properties, ? extends BlockItem> itemFactory) {
        final var block = BLOCKS.register(name, supplier);
        ITEMS.register(name, () -> itemFactory.apply(block.get(), new Properties()));
        return block;
    }

    public static <E extends BlockEntity> RegistryObject<BlockEntityType<E>> blockEntity(final String name,
                                                                                         final Supplier<? extends Block> block,
                                                                                         final BlockEntitySupplier<E> supplier) {
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>(supplier, Set.of(block.get()), null));
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        for (final var module : COMPAT_MODULES) {
            event.enqueueWork(module::initClient);
        }
    }
}
