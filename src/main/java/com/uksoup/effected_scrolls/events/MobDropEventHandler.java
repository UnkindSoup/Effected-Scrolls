package com.uksoup.effected_scrolls.events;

import com.uksoup.effected_scrolls.EffectedScrollsMain;
import com.uksoup.effected_scrolls.items.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = EffectedScrollsMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobDropEventHandler {
    private static final Random RANDOM = new Random();

    // 5% chance for a scroll to drop when player kills a mob
    private static final double DROP_CHANCE = 0.05;

    // Duration range in ticks (20 ticks = 1 second)
    private static final int MIN_DURATION = 10 * 20; // 10 seconds
    private static final int MAX_DURATION = 1800 * 20; // 30 minutes

    // Amplifier range (0 = level I, 1 = level II, etc.)
    private static final int MIN_AMPLIFIER = 0;
    private static final int MAX_AMPLIFIER = 4; // Up to level V

    /**
     * This method is called whenever any entity dies in the game.
     * We check if it was killed by a player, and if so, we have a chance to drop a scroll.
     */
    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {
        // Check if the entity was killed by a player
        if (event.getSource().getEntity() instanceof Player player) {
            // Don't drop scrolls when a player dies
            if (event.getEntity() instanceof Player) {
                return;
            }

            // Random chance to drop a scroll
            if (RANDOM.nextDouble() < DROP_CHANCE) {
                ItemStack scroll = createRandomEnchantedScroll();
                if (!scroll.isEmpty()) {
                    // Drop the scroll at the mob's location
                    event.getEntity().spawnAtLocation(scroll);
                }
            }
        }
    }

    /**
     * Creates an enchanted scroll with a random potion effect, duration, and amplifier.
     * This automatically includes ALL effects in the game, including modded ones!
     */
    private static ItemStack createRandomEnchantedScroll() {
        // Get all registered potion effects (vanilla + modded)
        List<MobEffect> allEffects = new ArrayList<>(ForgeRegistries.MOB_EFFECTS.getValues());

        if (allEffects.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Select a random effect from the list
        MobEffect randomEffect = allEffects.get(RANDOM.nextInt(allEffects.size()));
        ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(randomEffect);

        if (effectId == null) {
            return ItemStack.EMPTY;
        }

        // Generate random duration (in ticks)
        int duration = MIN_DURATION + RANDOM.nextInt(MAX_DURATION - MIN_DURATION);

        // Generate random amplifier (0 = Level I, 1 = Level II, etc.)
        int amplifier = MIN_AMPLIFIER + RANDOM.nextInt(MAX_AMPLIFIER - MIN_AMPLIFIER + 1);

        // Create the scroll item
        ItemStack scroll = new ItemStack(ModItems.ENCHANTED_SCROLL.get());

        // Add NBT data to store the effect information
        CompoundTag tag = scroll.getOrCreateTag();
        tag.putString("Effect", effectId.toString());
        tag.putInt("Duration", duration);
        tag.putInt("Amplifier", amplifier);

        return scroll;
    }
}
