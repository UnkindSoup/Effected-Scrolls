package com.uksoup.effected_scrolls.events;

import com.uksoup.effected_scrolls.EffectedScrollsMain;
import com.uksoup.effected_scrolls.items.EnchantedScrollItem;
import com.uksoup.effected_scrolls.items.ModItems;
import net.minecraft.client.color.item.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EffectedScrollsMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    /**
     * Registers the color handler for the enchanted scroll overlay.
     * Layer 0 (base texture) remains uncolored.
     * Layer 1 (overlay) gets tinted with the potion effect color.
     */
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColors itemColors = event.getItemColors();

        itemColors.register((stack, tintIndex) -> {
            // tintIndex 0 = layer0 (base scroll texture - no tint)
            // tintIndex 1 = layer1 (overlay - apply potion color)
            return tintIndex > 0 ? EnchantedScrollItem.getColor(stack) : 0xFFFFFF;
        }, ModItems.ENCHANTED_SCROLL.get());
    }
}
