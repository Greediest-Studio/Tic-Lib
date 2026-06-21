package com.smd.ticlib.integration.crafttweaker;

import com.smd.ticlib.util.TicArmorTraitCache;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenExpansion("crafttweaker.player.IPlayer")
@ZenRegister
public final class PlayerExpansion {

    @ZenMethod
    public static String[] getArmorTraits(IPlayer player) {
        return TicArmorTraitCache.INSTANCE.getArmorTraits(toPlayer(player));
    }

    @ZenMethod
    public static String[] getArmorSlotTraits(IPlayer player, String slotName) {
        return TicArmorTraitCache.INSTANCE.getArmorSlotTraits(toPlayer(player), slotName);
    }

    @ZenMethod
    public static boolean hasArmorTrait(IPlayer player, String traitId) {
        return TicArmorTraitCache.INSTANCE.hasArmorTrait(toPlayer(player), traitId);
    }

    @ZenMethod
    public static boolean hasArmorSlotTrait(IPlayer player, String slotName, String traitId) {
        return TicArmorTraitCache.INSTANCE.hasArmorSlotTrait(toPlayer(player), slotName, traitId);
    }

    @ZenMethod
    public static boolean refreshArmorCache(IPlayer player) {
        return TicArmorTraitCache.INSTANCE.refresh(toPlayer(player));
    }

    private static EntityPlayer toPlayer(IPlayer player) {
        return CraftTweakerMC.getPlayer(player);
    }
}