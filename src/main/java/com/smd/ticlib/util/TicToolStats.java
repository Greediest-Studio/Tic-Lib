package com.smd.ticlib.util;

import com.smd.ticlib.stats.TicArmorStatModifier;
import com.smd.ticlib.stats.TicStatPatches;
import com.smd.ticlib.stats.TicToolStatModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public final class TicToolStats {

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

    public static String[] getStats(ItemStack stack) {
        if (!TicToolStacks.isTicTarget(stack)) {
            return TicToolNbt.EMPTY_STRINGS;
        }
        return TicStatPatches.getNumericStatKeys(TagUtil.getToolTag(stack));
    }

    public static boolean hasStat(ItemStack stack, String statName) {
        return TicToolStacks.isTicTarget(stack) && TicStatPatches.hasNumericStat(TagUtil.getToolTag(stack), statName);
    }

    public static float getFloatStat(ItemStack stack, String statName) {
        if (!hasStat(stack, statName)) {
            return 0.0F;
        }
        return TagUtil.getToolTag(stack).getFloat(statName);
    }

    public static int getIntStat(ItemStack stack, String statName) {
        if (!hasStat(stack, statName)) {
            return 0;
        }
        return TagUtil.getToolTag(stack).getInteger(statName);
    }

    public static boolean addStat(ItemStack stack, String statName, float amount, String token) {
        if (!TicToolStacks.isTicTarget(stack)) {
            return false;
        }
        if (TicToolStacks.isTicArmor(stack)) {
            return TicArmorStatModifier.INSTANCE.addStat(stack, statName, amount, token);
        }
        return TicToolStatModifier.INSTANCE.addStat(stack, statName, amount, token);
    }

    public static boolean addIntStat(ItemStack stack, String statName, int amount, String token) {
        return addStat(stack, statName, amount, token);
    }
}
