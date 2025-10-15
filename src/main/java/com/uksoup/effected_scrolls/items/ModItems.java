package com.uksoup.effected_scrolls.items;

import com.uksoup.effected_scrolls.EffectedScrollsMain;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EffectedScrollsMain.MODID);

    public static final RegistryObject<EnchantedScrollItem> ENCHANTED_SCROLL = ITEMS.register("enchanted_scroll",
            () -> new EnchantedScrollItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<ExperienceTombItem> EXPERIENCE_TOMB = ITEMS.register("experience_tomb",
            () -> new ExperienceTombItem(new Item.Properties().stacksTo(1)));


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
