package com.uksoup.effected_scrolls.loot;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.Random;

public class AddItemModifier extends LootModifier {
    private static final Random RANDOM = new Random();
    public static final Supplier<Codec<AddItemModifier>> CODEC = Suppliers.memoize(()
            -> RecordCodecBuilder.create(inst -> codecStart(inst).and(ForgeRegistries.ITEMS.getCodec()
            .fieldOf("item").forGetter(m -> m.item)).apply(inst, AddItemModifier::new)));
    private final Item item;

    public AddItemModifier(LootItemCondition[] conditionsIn, Item item) {
        super(conditionsIn);
        this.item = item;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for(LootItemCondition condition : this.conditions) {
            if(!condition.test(context)) {
                return generatedLoot;
            }
        }

        ItemStack stack = new ItemStack(this.item);

        // Add random experience to Experience Tomb items
        if (this.item instanceof com.uksoup.effected_scrolls.items.ExperienceTombItem) {
            CompoundTag tag = stack.getOrCreateTag();
            RandomSource random = context.getRandom();

            // Generate random experience between 10-1000 points (roughly 1-20 levels)
            int minExperience = 10;
            int maxExperience = 1000;
            int experience = getWeightedRandomExperience(minExperience, maxExperience);

            tag.putInt("Experience", experience);
            stack.setTag(tag);
        }

        generatedLoot.add(stack);

        return generatedLoot;
    }

    private static int getWeightedRandomExperience(int min, int max) {
        double roll = RANDOM.nextDouble();

        if (roll < 0.20) {
            int rangeMin = Math.max(min, 10);
            int rangeMax = Math.min(100, max);
            if (rangeMax <= rangeMin) return rangeMin;
            return rangeMin + RANDOM.nextInt(rangeMax - rangeMin);

        } else if (roll < 0.70) {
            int rangeMin = Math.max(min, 100);
            int rangeMax = Math.min(300, max);
            if (rangeMax <= rangeMin) return rangeMin;
            return rangeMin + RANDOM.nextInt(rangeMax - rangeMin);

        } else if (roll < 0.90) {
            int rangeMin = Math.max(min, 300);
            int rangeMax = Math.min(500, max);
            if (rangeMax <= rangeMin) return rangeMin;
            return rangeMin + RANDOM.nextInt(rangeMax - rangeMin);

        } else if (roll < 0.95) {
            int rangeMin = Math.max(min, 500);
            int rangeMax = Math.min(1500, max);
            if (rangeMax <= rangeMin) return rangeMin;
            return rangeMin + RANDOM.nextInt(rangeMax - rangeMin);

        } else {
            int rangeMin = Math.max(min, 1500);
            int rangeMax = Math.min(2000, max);
            if (rangeMax <= rangeMin) return rangeMin;
            return rangeMin + RANDOM.nextInt(rangeMax - rangeMin);
        }
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}