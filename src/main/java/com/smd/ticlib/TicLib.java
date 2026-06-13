package com.smd.ticlib;

import com.smd.ticlib.stats.TicArmorStatModifier;
import com.smd.ticlib.stats.TicToolStatModifier;
import com.smd.ticlib.integration.crafttweaker.event.TicBuildEventForwarder;
import com.smd.ticlib.util.TicArmorTraitCache;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(   modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        dependencies = "required-after:tconstruct;required-after:conarm")
public class TicLib {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        TicToolStatModifier.INSTANCE.getIdentifier();
        TicArmorStatModifier.INSTANCE.getIdentifier();
        MinecraftForge.EVENT_BUS.register(TicBuildEventForwarder.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TicArmorTraitCache.INSTANCE);
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }

}
