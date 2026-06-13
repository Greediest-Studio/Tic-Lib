package com.smd.ticlib.integration.crafttweaker;

import com.smd.ticlib.util.TicArmorTraitCache;
import com.smd.ticlib.util.TicToolStacks;
import com.smd.ticlib.util.TicToolStats;
import com.smd.ticlib.util.TicToolTraits;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityEquipmentSlot;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.ticlib.TicTool")
@ZenRegister
public final class TicTool {

    private TicTool() {
    }

    @ZenMethod
    public static boolean isTool(IItemStack stack) {
        return TicToolStacks.isTicTool(toStackCopy(stack));
    }

    @ZenMethod
    public static IItemStack[] getAllItems() {
        return CraftTweakerMC.getIItemStacks(TicToolStacks.getAllKnownTicItems());
    }

    @ZenMethod
    public static boolean isArmor(IItemStack stack) {
        return TicToolStacks.isTicArmor(toStackCopy(stack));
    }

    @ZenMethod
    public static String getArmorType(IItemStack stack) {
        return TicToolStacks.getArmorType(toStackCopy(stack));
    }

    @ZenMethod
    public static IEntityEquipmentSlot getArmorSlot(IItemStack stack) {
        EntityEquipmentSlot slot = TicToolStacks.getArmorSlot(toStackCopy(stack));
        return slot == null ? null : CraftTweakerMC.getIEntityEquipmentSlot(slot);
    }

    @ZenMethod
    public static String[] getMaterials(IItemStack stack) {
        return TicToolStacks.getMaterials(toStackCopy(stack));
    }

    @ZenMethod
    public static String[] getTraits(IItemStack stack) {
        return TicToolTraits.getTraits(toStackCopy(stack));
    }

    @ZenMethod
    public static boolean hasTrait(IItemStack stack, String traitId) {
        return TicToolTraits.hasTrait(toStackCopy(stack), traitId);
    }

    @ZenMethod
    public static int getTraitColor(IItemStack stack, String traitId) {
        return TicToolTraits.getTraitColor(toStackCopy(stack), traitId);
    }

    @ZenMethod
    public static int getTraitLevel(IItemStack stack, String traitId) {
        return TicToolTraits.getTraitLevel(toStackCopy(stack), traitId);
    }

    @ZenMethod
    public static boolean addTrait(IItemStack stack, String traitId, int color, int level) {
        return applyRegisteredTrait(stack, traitId, color, level);
    }

    @ZenMethod
    public static boolean applyRegisteredTrait(IItemStack stack, String traitId, int color, int level) {
        return TicToolTraits.applyRegisteredTrait(toMutableStack(stack), traitId, color, level);
    }

    @ZenMethod
    public static boolean removeTrait(IItemStack stack, String traitId) {
        return removeRegisteredTrait(stack, traitId);
    }

    @ZenMethod
    public static boolean removeRegisteredTrait(IItemStack stack, String traitId) {
        return TicToolTraits.removeRegisteredTrait(toMutableStack(stack), traitId);
    }

    @ZenMethod
    public static IItemStack withTrait(IItemStack stack, String traitId, int color, int level) {
        return withRegisteredTrait(stack, traitId, color, level);
    }

    @ZenMethod
    public static IItemStack withRegisteredTrait(IItemStack stack, String traitId, int color, int level) {
        return CraftTweakerMC.getIItemStack(TicToolTraits.withRegisteredTrait(toStackCopy(stack), traitId, color, level));
    }

    @ZenMethod
    public static IItemStack withoutTrait(IItemStack stack, String traitId) {
        return withoutRegisteredTrait(stack, traitId);
    }

    @ZenMethod
    public static IItemStack withoutRegisteredTrait(IItemStack stack, String traitId) {
        return CraftTweakerMC.getIItemStack(TicToolTraits.withoutRegisteredTrait(toStackCopy(stack), traitId));
    }

    @ZenMethod
    public static boolean setBroken(IItemStack stack, boolean broken) {
        return TicToolStats.setBroken(toMutableStack(stack), broken);
    }

    @ZenMethod
    public static boolean addMiningSpeed(IItemStack stack, float amount, String token) {
        return patchMiningSpeed(stack, amount, token);
    }

    @ZenMethod
    public static boolean patchMiningSpeed(IItemStack stack, float amount, String token) {
        return TicToolStats.patchMiningSpeed(toMutableStack(stack), amount, token);
    }

    @ZenMethod
    public static boolean addAttack(IItemStack stack, float amount, String token) {
        return patchAttack(stack, amount, token);
    }

    @ZenMethod
    public static boolean patchAttack(IItemStack stack, float amount, String token) {
        return TicToolStats.patchAttack(toMutableStack(stack), amount, token);
    }

    @ZenMethod
    public static boolean addFreeModifiers(IItemStack stack, int amount, String token) {
        return patchFreeModifiers(stack, amount, token);
    }

    @ZenMethod
    public static boolean patchFreeModifiers(IItemStack stack, int amount, String token) {
        return TicToolStats.patchFreeModifiers(toMutableStack(stack), amount, token);
    }

    @ZenMethod
    public static boolean addDefense(IItemStack stack, float amount, String token) {
        return patchDefense(stack, amount, token);
    }

    @ZenMethod
    public static boolean patchDefense(IItemStack stack, float amount, String token) {
        return TicToolStats.patchDefense(toMutableStack(stack), amount, token);
    }

    @ZenMethod
    public static boolean addToughness(IItemStack stack, float amount, String token) {
        return patchToughness(stack, amount, token);
    }

    @ZenMethod
    public static boolean patchToughness(IItemStack stack, float amount, String token) {
        return TicToolStats.patchToughness(toMutableStack(stack), amount, token);
    }

    @ZenMethod
    public static boolean addHarvestLevel(IItemStack stack, int amount, String token) {
        return patchHarvestLevel(stack, amount, token);
    }

    @ZenMethod
    public static boolean patchHarvestLevel(IItemStack stack, int amount, String token) {
        return TicToolStats.patchHarvestLevel(toMutableStack(stack), amount, token);
    }

    @ZenMethod
    public static boolean addDrawSpeed(IItemStack stack, float amount, String token) {
        return patchDrawSpeed(stack, amount, token);
    }

    @ZenMethod
    public static boolean patchDrawSpeed(IItemStack stack, float amount, String token) {
        return TicToolStats.patchDrawSpeed(toMutableStack(stack), amount, token);
    }

    @ZenMethod
    public static boolean addAttackSpeedMultiplier(IItemStack stack, float amount, String token) {
        return patchAttackSpeedMultiplier(stack, amount, token);
    }

    @ZenMethod
    public static boolean patchAttackSpeedMultiplier(IItemStack stack, float amount, String token) {
        return TicToolStats.patchAttackSpeedMultiplier(toMutableStack(stack), amount, token);
    }

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

    private static ItemStack toStackCopy(IItemStack stack) {
        return stack == null ? ItemStack.EMPTY : CraftTweakerMC.getItemStack(stack);
    }

    private static ItemStack toMutableStack(IItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return CraftTweakerMC.getItemStack(stack.mutable());
    }

    private static EntityPlayer toPlayer(IPlayer player) {
        return CraftTweakerMC.getPlayer(player);
    }
}
