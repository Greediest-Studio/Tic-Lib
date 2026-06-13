package com.smd.ticlib.util;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.common.armor.utils.ArmorTagUtil;
import c4.conarm.lib.armor.ArmorNBT;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public final class TicToolStats {

    private static final float MIN_DRAW_SPEED = 0.01F;

    private TicToolStats() {
    }

    public static boolean setBroken(ItemStack stack, boolean broken) {
        if (!TicToolStacks.isTicTarget(stack)) {
            return false;
        }
        NBTTagCompound root = TicToolNbt.copyRoot(stack);
        NBTTagCompound stats = TagUtil.getToolTag(root).copy();
        stats.setBoolean(Tags.BROKEN, broken);
        TagUtil.setToolTag(root, stats);
        stack.setTagCompound(root);
        return true;
    }

    public static boolean addMiningSpeed(ItemStack stack, float amount, String token) {
        return addFloatToolStat(stack, Tags.MININGSPEED, amount, token);
    }

    public static boolean addAttack(ItemStack stack, float amount, String token) {
        return addFloatToolStat(stack, Tags.ATTACK, amount, token);
    }

    public static boolean addFreeModifiers(ItemStack stack, int amount, String token) {
        return addIntToolStat(stack, Tags.FREE_MODIFIERS, amount, token);
    }

    public static boolean addHarvestLevel(ItemStack stack, int amount, String token) {
        return addIntToolStat(stack, Tags.HARVESTLEVEL, amount, token);
    }

    public static boolean addAttackSpeedMultiplier(ItemStack stack, float amount, String token) {
        return addFloatToolStat(stack, Tags.ATTACKSPEEDMULTIPLIER, amount, token);
    }

    public static boolean addDrawSpeed(ItemStack stack, float amount, String token) {
        if (!canApply(stack, Tags.DRAWSPEED, token) || TicToolStacks.isTicArmor(stack)) {
            return false;
        }
        if (TicToolNbt.hasToken(stack, token)) {
            return true;
        }

        NBTTagCompound root = TicToolNbt.copyRoot(stack);
        NBTTagCompound stats = TagUtil.getToolTag(root).copy();
        NBTTagCompound original = getOriginalStats(root, stats);
        float current = stats.getFloat(Tags.DRAWSPEED);
        float value = Math.max(MIN_DRAW_SPEED, current - amount);
        stats.setFloat(Tags.DRAWSPEED, value);
        original.setFloat(Tags.DRAWSPEED, value);
        TagUtil.setToolTag(root, stats);
        root.setTag(Tags.TOOL_DATA_ORIG, original);
        TicToolNbt.addToken(stack, token);
        stack.setTagCompound(root);
        return true;
    }

    public static boolean addDefense(ItemStack stack, float amount, String token) {
        if (!canApply(stack, ArmorTagUtil.DEFENSE, token) || !TicToolStacks.isTicArmor(stack)) {
            return false;
        }
        if (TicToolNbt.hasToken(stack, token)) {
            return true;
        }

        EntityEquipmentSlot slot = TicToolStacks.getArmorSlot(stack);
        if (slot == null) {
            return false;
        }

        float internalAmount = amount / ArmorHelper.defenseMultipliers[slot.getIndex()];
        NBTTagCompound root = TicToolNbt.copyRoot(stack);
        NBTTagCompound statsTag = TagUtil.getToolTag(root).copy();
        NBTTagCompound originalTag = getOriginalStats(root, statsTag);
        ArmorNBT stats = new ArmorNBT(statsTag);
        ArmorNBT original = new ArmorNBT(originalTag);
        stats.defense += internalAmount;
        original.defense += internalAmount;
        stats.write(statsTag);
        original.write(originalTag);
        TagUtil.setToolTag(root, statsTag);
        root.setTag(Tags.TOOL_DATA_ORIG, originalTag);
        TicToolNbt.addToken(stack, token);
        stack.setTagCompound(root);
        return true;
    }

    public static boolean addToughness(ItemStack stack, float amount, String token) {
        if (TicToolStacks.isTicArmor(stack)) {
            if (!canApply(stack, ArmorTagUtil.TOUGHNESS, token)) {
                return false;
            }
            if (TicToolNbt.hasToken(stack, token)) {
                return true;
            }

            NBTTagCompound root = TicToolNbt.copyRoot(stack);
            NBTTagCompound statsTag = TagUtil.getToolTag(root).copy();
            NBTTagCompound originalTag = getOriginalStats(root, statsTag);
            ArmorNBT stats = new ArmorNBT(statsTag);
            ArmorNBT original = new ArmorNBT(originalTag);
            stats.toughness += amount;
            original.toughness += amount;
            stats.write(statsTag);
            original.write(originalTag);
            TagUtil.setToolTag(root, statsTag);
            root.setTag(Tags.TOOL_DATA_ORIG, originalTag);
            TicToolNbt.addToken(stack, token);
            stack.setTagCompound(root);
            return true;
        }
        return addFloatToolStat(stack, "Toughness", amount, token);
    }

    private static boolean addFloatToolStat(ItemStack stack, String statName, float amount, String token) {
        if (!canApply(stack, statName, token) || TicToolStacks.isTicArmor(stack)) {
            return false;
        }
        if (TicToolNbt.hasToken(stack, token)) {
            return true;
        }

        NBTTagCompound root = TicToolNbt.copyRoot(stack);
        NBTTagCompound statsTag = TagUtil.getToolTag(root).copy();
        NBTTagCompound originalTag = getOriginalStats(root, statsTag);
        ToolNBT stats = new ToolNBT(statsTag);
        ToolNBT original = new ToolNBT(originalTag);
        mutateFloat(stats, statName, amount);
        mutateFloat(original, statName, amount);
        stats.write(statsTag);
        original.write(originalTag);
        TagUtil.setToolTag(root, statsTag);
        root.setTag(Tags.TOOL_DATA_ORIG, originalTag);
        TicToolNbt.addToken(stack, token);
        stack.setTagCompound(root);
        return true;
    }

    private static boolean addIntToolStat(ItemStack stack, String statName, int amount, String token) {
        if (!canApply(stack, statName, token) || TicToolStacks.isTicArmor(stack)) {
            return false;
        }
        if (TicToolNbt.hasToken(stack, token)) {
            return true;
        }

        NBTTagCompound root = TicToolNbt.copyRoot(stack);
        NBTTagCompound statsTag = TagUtil.getToolTag(root).copy();
        NBTTagCompound originalTag = getOriginalStats(root, statsTag);
        ToolNBT stats = new ToolNBT(statsTag);
        ToolNBT original = new ToolNBT(originalTag);
        mutateInt(stats, statName, amount);
        mutateInt(original, statName, amount);
        stats.write(statsTag);
        original.write(originalTag);
        TagUtil.setToolTag(root, statsTag);
        root.setTag(Tags.TOOL_DATA_ORIG, originalTag);
        TicToolNbt.addToken(stack, token);
        stack.setTagCompound(root);
        return true;
    }

    private static NBTTagCompound getOriginalStats(NBTTagCompound root, NBTTagCompound currentStats) {
        if (root.hasKey(Tags.TOOL_DATA_ORIG, 10)) {
            return root.getCompoundTag(Tags.TOOL_DATA_ORIG).copy();
        }
        return currentStats.copy();
    }

    private static boolean canApply(ItemStack stack, String statName, String token) {
        return TicToolStacks.isTicTarget(stack)
                && statName != null
                && token != null
                && !token.trim().isEmpty()
                && TicToolNbt.hasStat(stack, statName);
    }

    private static void mutateFloat(ToolNBT stats, String statName, float amount) {
        switch (statName) {
            case Tags.MININGSPEED:
                stats.speed += amount;
                break;
            case Tags.ATTACK:
                stats.attack += amount;
                break;
            case Tags.ATTACKSPEEDMULTIPLIER:
                stats.attackSpeedMultiplier += amount;
                break;
            case "Toughness":
                break;
            default:
                break;
        }
    }

    private static void mutateInt(ToolNBT stats, String statName, int amount) {
        switch (statName) {
            case Tags.FREE_MODIFIERS:
                stats.modifiers += amount;
                break;
            case Tags.HARVESTLEVEL:
                stats.harvestLevel += amount;
                break;
            default:
                break;
        }
    }
}
