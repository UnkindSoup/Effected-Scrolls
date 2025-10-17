package com.uksoup.effected_scrolls.datagen;

import com.uksoup.effected_scrolls.EffectedScrollsMain;
import com.uksoup.effected_scrolls.items.ModItems;
import com.uksoup.effected_scrolls.loot.AddItemModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

import java.util.List;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, EffectedScrollsMain.MODID);
    }

    public static final List<String> LOOT_POOL = List.of(
            "abandoned_mineshaft",
            "ancient_city",
            "ancient_city_ice_box",
            "bastion_bridge",
            "bastion_hoglin_stable",
            "bastion_other",
            "bastion_treasure",
            "buried_treasure",
            "desert_pyramid",
            "end_city_treasure",
            "igloo_chest",
            "jungle_temple",
            "nether_bridge",
            "pillager_outpost",
            "ruined_portal",
            "shipwreck_treasure",
            "simple_dungeon",
            "stronghold_corridor",
            "stronghold_crossing",
            "stronghold_library",
            "woodland_mansion"
    );
    @Override
    protected void start() {
        for (String loot_pool : LOOT_POOL) {
            add("tomb_from_" + loot_pool, new AddItemModifier(new LootItemCondition[] {
                    new LootTableIdCondition.Builder(new ResourceLocation("chests/" + loot_pool)).build(),
                    LootItemRandomChanceCondition.randomChance(0.5f).build() },
                    ModItems.EXPERIENCE_TOMB.get()));
        }
    }
}