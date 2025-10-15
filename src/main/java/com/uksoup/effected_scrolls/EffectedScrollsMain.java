package com.uksoup.effected_scrolls;

import com.mojang.logging.LogUtils;
import com.uksoup.effected_scrolls.items.ModItems;
import com.uksoup.effected_scrolls.sounds.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EffectedScrollsMain.MODID)
public class EffectedScrollsMain {
    public static final String MODID = "effected_scrolls";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EffectedScrollsMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ENCHANTED_SCROLL);
            event.accept(ModItems.EXPERIENCE_TOMB);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    public void clientSetup(FMLClientSetupEvent event) {
    }
}