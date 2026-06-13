package com.smd.ticlib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;

import java.util.ArrayList;
import java.util.List;

public final class TicToolTraits {

    private static final int DEFAULT_COLOR = 0xffffff;
    private static final int DEFAULT_LEVEL = 1;

    private TicToolTraits() {
    }

    public static String[] getTraits(ItemStack stack) {
        if (!TicToolStacks.isTicTarget(stack)) {
            return TicToolNbt.EMPTY_STRINGS;
        }
        return TicToolNbt.readStringList(TagUtil.getTraitsTagList(stack));
    }

    public static boolean hasTrait(ItemStack stack, String traitId) {
        if (!isValidTraitId(traitId)) {
            return false;
        }
        for (String trait : getTraits(stack)) {
            if (traitId.equals(trait)) {
                return true;
            }
        }
        return false;
    }

    public static int getTraitColor(ItemStack stack, String traitId) {
        NBTTagCompound modifier = getModifierTag(stack, traitId);
        return modifier == null ? DEFAULT_COLOR : (modifier.hasKey("color") ? modifier.getInteger("color") : DEFAULT_COLOR);
    }

    public static int getTraitLevel(ItemStack stack, String traitId) {
        NBTTagCompound modifier = getModifierTag(stack, traitId);
        return modifier == null ? DEFAULT_LEVEL : Math.max(DEFAULT_LEVEL, modifier.hasKey("level") ? modifier.getInteger("level") : DEFAULT_LEVEL);
    }

    public static boolean addTrait(ItemStack stack, String traitId, int color, int level) {
        TraitHandle handle = resolveTrait(traitId);
        if (!TicToolStacks.isTicTarget(stack) || handle == null || hasTrait(stack, traitId)) {
            return false;
        }

        NBTTagCompound root = TicToolNbt.getOrCreateRoot(stack);
        if (!root.hasKey(Tags.TOOL_TRAITS, 9) || !root.hasKey(Tags.TOOL_MODIFIERS, 9)) {
            return false;
        }

        ToolBuilder.addTrait(root, handle.trait, color);
        if (level > DEFAULT_LEVEL) {
            rebuildModifierEntry(root, handle.modifierIdentifier, collectTraitsByModifier(root, handle.modifierIdentifier), color);
        }
        syncBaseModifier(root, handle.modifierIdentifier, true);
        stack.setTagCompound(root);
        return true;
    }

    public static boolean removeTrait(ItemStack stack, String traitId) {
        TraitHandle handle = resolveTrait(traitId);
        if (!TicToolStacks.isTicTarget(stack) || handle == null || !hasTrait(stack, traitId)) {
            return false;
        }

        NBTTagCompound root = TicToolNbt.getOrCreateRoot(stack);
        TagUtil.setTraitsTagList(root, TicToolNbt.copyStringListWithout(TagUtil.getTraitsTagList(root), traitId));
        List<String> remainingTraits = collectTraitsByModifier(root, handle.modifierIdentifier);
        if (remainingTraits.isEmpty()) {
            TagUtil.setModifiersTagList(root, TicToolNbt.copyModifierListWithout(TagUtil.getModifiersTagList(root), handle.modifierIdentifier));
            syncBaseModifier(root, handle.modifierIdentifier, false);
        } else {
            int color = getTraitColor(stack, traitId);
            rebuildModifierEntry(root, handle.modifierIdentifier, remainingTraits, color);
        }
        stack.setTagCompound(root);
        return true;
    }

    public static ItemStack withTrait(ItemStack stack, String traitId, int color, int level) {
        if (TicToolStacks.isEmpty(stack)) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        addTrait(copy, traitId, color, level);
        return copy;
    }

    public static ItemStack withoutTrait(ItemStack stack, String traitId) {
        if (TicToolStacks.isEmpty(stack)) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        removeTrait(copy, traitId);
        return copy;
    }

    private static NBTTagCompound getModifierTag(ItemStack stack, String traitId) {
        TraitHandle handle = resolveTrait(traitId);
        if (!TicToolStacks.isTicTarget(stack) || handle == null) {
            return null;
        }
        NBTTagList modifiers = TagUtil.getModifiersTagList(stack);
        for (int i = 0; i < modifiers.tagCount(); i++) {
            NBTTagCompound modifier = modifiers.getCompoundTagAt(i);
            if (handle.modifierIdentifier.equals(modifier.getString("identifier"))) {
                return modifier;
            }
        }
        return null;
    }

    private static TraitHandle resolveTrait(String traitId) {
        if (!isValidTraitId(traitId)) {
            return null;
        }
        ITrait trait = TinkerRegistry.getTrait(traitId);
        if (trait == null) {
            return null;
        }
        if (!(TinkerRegistry.getModifier(traitId) instanceof AbstractTrait)) {
            return null;
        }
        AbstractTrait modifier = (AbstractTrait) TinkerRegistry.getModifier(traitId);
        return new TraitHandle(trait, modifier.getModifierIdentifier());
    }

    private static List<String> collectTraitsByModifier(NBTTagCompound root, String modifierIdentifier) {
        List<String> traits = new ArrayList<>();
        NBTTagList traitList = TagUtil.getTraitsTagList(root);
        for (int i = 0; i < traitList.tagCount(); i++) {
            String traitId = traitList.getStringTagAt(i);
            TraitHandle handle = resolveTrait(traitId);
            if (handle != null && modifierIdentifier.equals(handle.modifierIdentifier)) {
                traits.add(traitId);
            }
        }
        return traits;
    }

    private static void rebuildModifierEntry(NBTTagCompound root, String modifierIdentifier, List<String> traitIds, int color) {
        NBTTagCompound rebuiltRoot = new NBTTagCompound();
        rebuiltRoot.setTag(Tags.TOOL_MODIFIERS, new NBTTagList());
        rebuiltRoot.setTag(Tags.TOOL_TRAITS, new NBTTagList());

        for (String traitId : traitIds) {
            TraitHandle handle = resolveTrait(traitId);
            if (handle != null) {
                ToolBuilder.addTrait(rebuiltRoot, handle.trait, color);
            }
        }

        NBTTagCompound rebuiltTag = TinkerUtil.getModifierTag(rebuiltRoot, modifierIdentifier);
        NBTTagList modifiers = TagUtil.getModifiersTagList(root);
        int index = TinkerUtil.getIndexInCompoundList(modifiers, modifierIdentifier);
        if (index >= 0) {
            modifiers.set(index, rebuiltTag);
        } else if (!rebuiltTag.isEmpty()) {
            modifiers.appendTag(rebuiltTag);
        }
        TagUtil.setModifiersTagList(root, modifiers);
    }

    private static void syncBaseModifier(NBTTagCompound root, String traitId, boolean add) {
        if (!root.hasKey(Tags.BASE_DATA, 10)) {
            return;
        }
        NBTTagCompound baseData = root.getCompoundTag(Tags.BASE_DATA);
        if (!baseData.hasKey(Tags.BASE_MODIFIERS, 9)) {
            return;
        }

        NBTTagList modifiers = baseData.getTagList(Tags.BASE_MODIFIERS, 8);
        if (add) {
            if (!TicToolNbt.stringListContains(modifiers, traitId)) {
                baseData.setTag(Tags.BASE_MODIFIERS, TicToolNbt.copyStringListAppending(modifiers, traitId));
            }
            return;
        }

        if (TicToolNbt.stringListContains(modifiers, traitId)) {
            baseData.setTag(Tags.BASE_MODIFIERS, TicToolNbt.copyStringListWithout(modifiers, traitId));
        }
    }

    private static boolean isValidTraitId(String traitId) {
        return traitId != null && !traitId.trim().isEmpty();
    }

    private static final class TraitHandle {
        private final ITrait trait;
        private final String modifierIdentifier;

        private TraitHandle(ITrait trait, String modifierIdentifier) {
            this.trait = trait;
            this.modifierIdentifier = modifierIdentifier;
        }
    }
}
