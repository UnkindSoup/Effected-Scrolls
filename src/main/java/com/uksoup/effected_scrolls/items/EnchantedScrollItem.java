package com.uksoup.effected_scrolls.items;

import com.uksoup.effected_scrolls.sounds.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnchantedScrollItem extends Item {
    private static final int USE_DURATION = 32; // About 1.6 seconds
    private static final int COOLDOWN_TICKS = 40; // 1 second cooldown

    public EnchantedScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                ModSounds.SCROLL_USE.get(), SoundSource.PLAYERS, 0.4F, 1.0F);

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            // Play sound when scroll usage is completed (client side)
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.SCROLL_COMPLETE.get(), SoundSource.PLAYERS, 0.4F, 1.0F);

            if (!level.isClientSide) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("Effect")) {
                    String effectId = tag.getString("Effect");
                    int duration = tag.getInt("Duration");
                    int amplifier = tag.getInt("Amplifier");

                    // Get the effect from registry
                    MobEffect effect = net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(
                            new net.minecraft.resources.ResourceLocation(effectId)
                    );

                    if (effect != null) {
                        player.addEffect(new MobEffectInstance(effect, duration, amplifier));

                        // Add cooldown (like ender pearl or chorus fruit)
                        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

                        // Only consume in survival/adventure mode
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        // This is called when the player stops using the item early (releases right-click)
        if (entity instanceof Player player) {

            if (level.isClientSide) {
                player.stopUsingItem();
                net.minecraft.client.Minecraft.getInstance().getSoundManager()
                        .stop(ModSounds.SCROLL_USE.get().getLocation(), SoundSource.PLAYERS);
            }

            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.SCROLL_STOP.get(), SoundSource.PLAYERS, 0.4F, 1.0F);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW; // Could also use DRINK or EAT
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Effect")) {
            String effectId = tag.getString("Effect");
            int amplifier = tag.getInt("Amplifier");

            MobEffect effect = net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(
                    new net.minecraft.resources.ResourceLocation(effectId)
            );

            if (effect != null) {
                String effectName = Component.translatable(effect.getDescriptionId()).getString();
                String romanNumeral = amplifier > 0 ? " " + toRomanNumeral(amplifier + 1) : "";

                return Component.literal("Enchanted Scroll of " + effectName + romanNumeral)
                        .withStyle(ChatFormatting.YELLOW);
            }
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.literal("Hold right-click to use")
                .withStyle(ChatFormatting.BLUE)
                .withStyle(ChatFormatting.ITALIC)
        );

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Effect")) {
            int duration = tag.getInt("Duration");

            int durationSeconds = duration / 20;
            int minutes = durationSeconds / 60;
            int seconds = durationSeconds % 60;
            String durationStr = String.format("%d:%02d", minutes, seconds);

            tooltip.add(Component.literal("(" + durationStr + ")")
                    .withStyle(ChatFormatting.BLUE)
                    .withStyle(ChatFormatting.ITALIC)
            );
        }
    }

    private String toRomanNumeral(int number) {
        if (number <= 0) return "";
        if (number >= 10) return String.valueOf(number); // Fallback for very high numbers

        String[] romanNumerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return romanNumerals[number];
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted glint effect
    }

    /**
     * Returns the color for the potion effect overlay on the scroll.
     * This works similarly to how potions display their effect color.
     */
    public static int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Effect")) {
            String effectId = tag.getString("Effect");

            MobEffect effect = net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(
                    new net.minecraft.resources.ResourceLocation(effectId)
            );

            if (effect != null) {
                return effect.getColor();
            }
        }

        // Default purple color if no effect
        return 0x8B00FF;
    }
}