package com.uksoup.effected_scrolls.items;

import com.uksoup.effected_scrolls.sounds.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExperienceTombItem extends Item {
    private static final int USE_DURATION = 32; // About 1.6 seconds
    private static final int COOLDOWN_TICKS = 40; // 1 second cooldown
    public ExperienceTombItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                ModSounds.TOMB_USE.get(), SoundSource.PLAYERS, 0.5F, 1.0F);

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            // Play sound when tomb usage is completed (client side)
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TOMB_COMPLETE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

            if (!level.isClientSide) {
                CompoundTag tag = stack.getTag();
                if (tag != null && tag.contains("Experience")) {
                    int experience = tag.getInt("Experience");

                    // Give the player the stored experience
                    player.giveExperiencePoints(experience);

                    // Add cooldown
                    player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

                    // Only consume in survival/adventure mode
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
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
            // Stop the TOMB_USE sound that's currently playing
            if (level.isClientSide) {
                player.stopUsingItem();
                net.minecraft.client.Minecraft.getInstance().getSoundManager()
                        .stop(ModSounds.TOMB_USE.get().getLocation(), SoundSource.PLAYERS);
            }

            // Play sound when tomb usage is stopped early
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TOMB_STOP.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Experience")) {
            int experience = tag.getInt("Experience");

            // Convert experience points to approximate levels for display
            int levels = calculateLevelsFromExperience(experience);

            if (levels > 0) {
                return Component.literal("Experience Tomb (" + levels + " Level" + (levels > 1 ? "s" : "") + ")")
                        .withStyle(ChatFormatting.GREEN);
            } else {
                return Component.literal("Experience Tomb (" + experience + " Points)")
                        .withStyle(ChatFormatting.GREEN);
            }
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Experience")) {
            int experience = tag.getInt("Experience");
            int levels = calculateLevelsFromExperience(experience);

            if (levels > 0) {
                tooltip.add(Component.literal("Experience: " + experience + " points (~" + levels + " levels)")
                        .withStyle(ChatFormatting.AQUA));
            } else {
                tooltip.add(Component.literal("Experience: " + experience + " points")
                        .withStyle(ChatFormatting.AQUA));
            }
        }

        tooltip.add(Component.literal("Hold right-click to absorb").withStyle(ChatFormatting.GRAY));
    }

    /**
     * Calculates approximate levels from experience points.
     * Uses the same formula as Minecraft's experience system.
     */
    private int calculateLevelsFromExperience(int experience) {
        int level = 0;
        int remainingXp = experience;

        while (remainingXp > 0) {
            int xpForNextLevel = getExperienceForLevel(level);
            if (remainingXp >= xpForNextLevel) {
                remainingXp -= xpForNextLevel;
                level++;
            } else {
                break;
            }
        }

        return level;
    }

    /**
     * Gets the XP required to go from this level to the next level.
     */
    private int getExperienceForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted glint effect
    }

    /**
     * Returns the color for the tomb overlay.
     * Can be customized to use different colors based on experience amount.
     */
    public static int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Experience")) {
            int experience = tag.getInt("Experience");

            // Color based on experience amount (green gradient)
            // Low XP = darker green, High XP = brighter green
            if (experience < 100) {
                return 0x00AA00; // Dark green
            } else if (experience < 500) {
                return 0x00FF00; // Medium green
            } else if (experience < 2000) {
                return 0x55FF55; // Bright green
            } else {
                return 0xAAFFAA; // Very bright green/cyan
            }
        }

        // Default green color
        return 0x00FF00;
    }
}