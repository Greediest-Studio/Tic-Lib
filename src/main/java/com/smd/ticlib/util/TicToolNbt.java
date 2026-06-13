package com.smd.ticlib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

import java.util.ArrayList;
import java.util.List;

public final class TicToolNbt {

    public static final String[] EMPTY_STRINGS = new String[0];

    private static final String TICLIB_ROOT = "ticlib";
    private static final String APPLIED_TOKENS = "applied_tokens";

    private TicToolNbt() {
    }

    public static NBTTagCompound getRoot(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return null;
        }
        return stack.getTagCompound();
    }

    public static NBTTagCompound getOrCreateRoot(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return new NBTTagCompound();
        }
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            stack.setTagCompound(root);
        }
        return root;
    }

    public static NBTTagCompound copyRoot(ItemStack stack) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        return root == null ? new NBTTagCompound() : root.copy();
    }

    public static NBTTagCompound getStats(ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : TagUtil.getToolTag(stack);
    }

    public static NBTTagCompound getStatsOriginal(ItemStack stack) {
        NBTTagCompound root = getRoot(stack);
        return root == null || !root.hasKey(Tags.TOOL_DATA_ORIG, 10) ? null : root.getCompoundTag(Tags.TOOL_DATA_ORIG);
    }

    public static NBTTagCompound getOrCreateStats(ItemStack stack) {
        NBTTagCompound root = getOrCreateRoot(stack);
        if (!root.hasKey(Tags.TOOL_DATA, 10)) {
            root.setTag(Tags.TOOL_DATA, new NBTTagCompound());
        }
        return root.getCompoundTag(Tags.TOOL_DATA);
    }

    public static NBTTagCompound getOrCreateStatsOriginal(ItemStack stack) {
        NBTTagCompound root = getOrCreateRoot(stack);
        if (!root.hasKey(Tags.TOOL_DATA_ORIG, 10)) {
            root.setTag(Tags.TOOL_DATA_ORIG, new NBTTagCompound());
        }
        return root.getCompoundTag(Tags.TOOL_DATA_ORIG);
    }

    public static NBTTagCompound getBaseData(ItemStack stack) {
        NBTTagCompound root = getRoot(stack);
        return root == null || !root.hasKey(Tags.BASE_DATA, 10) ? null : root.getCompoundTag(Tags.BASE_DATA);
    }

    public static boolean hasStat(ItemStack stack, String statName) {
        NBTTagCompound stats = getStats(stack);
        return stats != null && statName != null && stats.hasKey(statName);
    }

    public static String[] readStringList(NBTTagList list) {
        if (list == null || list.tagCount() == 0) {
            return EMPTY_STRINGS;
        }
        String[] values = new String[list.tagCount()];
        for (int i = 0; i < list.tagCount(); i++) {
            values[i] = list.getStringTagAt(i);
        }
        return values;
    }

    public static List<String> toJavaList(NBTTagList list) {
        List<String> values = new ArrayList<>();
        if (list == null) {
            return values;
        }
        for (int i = 0; i < list.tagCount(); i++) {
            values.add(list.getStringTagAt(i));
        }
        return values;
    }

    public static NBTTagList copyStringListAppending(NBTTagList source, String value) {
        NBTTagList result = new NBTTagList();
        if (source != null) {
            for (int i = 0; i < source.tagCount(); i++) {
                result.appendTag(new NBTTagString(source.getStringTagAt(i)));
            }
        }
        result.appendTag(new NBTTagString(value));
        return result;
    }

    public static NBTTagList copyStringListWithout(NBTTagList source, String value) {
        NBTTagList result = new NBTTagList();
        if (source == null) {
            return result;
        }
        for (int i = 0; i < source.tagCount(); i++) {
            String current = source.getStringTagAt(i);
            if (!value.equals(current)) {
                result.appendTag(new NBTTagString(current));
            }
        }
        return result;
    }

    public static boolean stringListContains(NBTTagList list, String value) {
        if (list == null || value == null) {
            return false;
        }
        for (int i = 0; i < list.tagCount(); i++) {
            if (value.equals(list.getStringTagAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static NBTTagList copyModifierListWithout(NBTTagList source, String identifier) {
        NBTTagList result = new NBTTagList();
        if (source == null) {
            return result;
        }
        for (int i = 0; i < source.tagCount(); i++) {
            NBTTagCompound entry = source.getCompoundTagAt(i);
            if (!identifier.equals(entry.getString("identifier"))) {
                result.appendTag(entry.copy());
            }
        }
        return result;
    }

    public static NBTTagList copyModifierListAppending(NBTTagList source, NBTTagCompound modifier) {
        NBTTagList result = new NBTTagList();
        if (source != null) {
            for (int i = 0; i < source.tagCount(); i++) {
                result.appendTag(source.getCompoundTagAt(i).copy());
            }
        }
        result.appendTag(modifier.copy());
        return result;
    }

    public static boolean hasToken(ItemStack stack, String token) {
        if (stack == null || stack.isEmpty() || token == null || token.trim().isEmpty()) {
            return false;
        }
        NBTTagCompound root = getRoot(stack);
        if (root == null || !root.hasKey(TICLIB_ROOT, 10)) {
            return false;
        }
        NBTTagList tokens = root.getCompoundTag(TICLIB_ROOT).getTagList(APPLIED_TOKENS, 8);
        return stringListContains(tokens, token);
    }

    public static boolean addToken(ItemStack stack, String token) {
        if (stack == null || stack.isEmpty() || token == null || token.trim().isEmpty()) {
            return false;
        }
        if (hasToken(stack, token)) {
            return true;
        }
        NBTTagCompound root = getOrCreateRoot(stack);
        return addToken(root, token);
    }

    public static boolean addToken(NBTTagCompound root, String token) {
        if (root == null || token == null || token.trim().isEmpty()) {
            return false;
        }
        NBTTagCompound ticlibData = root.getCompoundTag(TICLIB_ROOT);
        NBTTagList tokens = ticlibData.getTagList(APPLIED_TOKENS, 8);
        if (stringListContains(tokens, token)) {
            return true;
        }
        tokens.appendTag(new NBTTagString(token));
        ticlibData.setTag(APPLIED_TOKENS, tokens);
        root.setTag(TICLIB_ROOT, ticlibData);
        return true;
    }
}
