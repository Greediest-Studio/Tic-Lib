package com.smd.ticlib.stats;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

final class TicStatModifierBridge {

    private TicStatModifierBridge() {
    }

    static boolean addStat(ItemStack stack, String identifier, int color, String statName, double amount, String token) {
        if (stack == null || stack.isEmpty() || token == null || token.trim().isEmpty()) {
            return false;
        }

        NBTTagCompound root = TagUtil.getTagSafe(stack).copy();
        NBTTagCompound stats = TagUtil.getToolTag(root);
        if (!TicStatPatches.hasNumericStat(stats, statName)) {
            return false;
        }

        NBTTagList modifiers = TagUtil.getModifiersTagList(root);
        int index = TinkerUtil.getIndexInCompoundList(modifiers, identifier);
        NBTTagCompound modifierTag = index >= 0 ? modifiers.getCompoundTagAt(index).copy() : new NBTTagCompound();
        if (TicStatPatches.hasToken(modifierTag, token)) {
            return true;
        }
        if (!TicStatPatches.addBonus(modifierTag, identifier, color, statName, amount, token)) {
            return false;
        }
        if (index >= 0) {
            modifiers.set(index, modifierTag);
        } else {
            modifiers.appendTag(modifierTag);
        }
        TagUtil.setModifiersTagList(root, modifiers);
        addBaseModifier(root, identifier);

        NBTTagCompound updatedStats = TagUtil.getToolTag(root).copy();
        TicStatPatches.addNumericStat(updatedStats, statName, amount);
        TagUtil.setToolTag(root, updatedStats);
        stack.setTagCompound(root);
        return true;
    }

    private static void addBaseModifier(NBTTagCompound root, String identifier) {
        if (TinkerUtil.hasModifier(root, identifier)) {
            return;
        }
        NBTTagList baseModifiers = TagUtil.getBaseModifiersTagList(root);
        baseModifiers.appendTag(new NBTTagString(identifier));
        TagUtil.setBaseModifiersTagList(root, baseModifiers);
    }
}
