package com.uksoup.effected_scrolls.sounds;

import com.uksoup.effected_scrolls.EffectedScrollsMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EffectedScrollsMain.MODID);
    public static final RegistryObject<SoundEvent> SCROLL_USE = registerSoundEvent("scroll_use");
    public static final RegistryObject<SoundEvent> SCROLL_COMPLETE = registerSoundEvent("scroll_complete");
    public static final RegistryObject<SoundEvent> SCROLL_STOP = registerSoundEvent("scroll_stop");

    public static final RegistryObject<SoundEvent> TOMB_USE = registerSoundEvent("tomb_use");
    public static final RegistryObject<SoundEvent> TOMB_COMPLETE = registerSoundEvent("tomb_complete");
    public static final RegistryObject<SoundEvent> TOMB_STOP = registerSoundEvent("tomb_stop");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(EffectedScrollsMain.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}