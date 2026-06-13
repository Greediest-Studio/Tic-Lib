package com.smd.ticlib.integration.crafttweaker.event;

import com.smd.ticlib.stats.TicStatPatches;
import com.smd.ticlib.util.TicToolTraits;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.TagUtil;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenRegister
@ZenClass("mods.ticlib.event.ItemBuildEvent")
public class TicItemBuildEvent {

    protected final NBTTagCompound root;
    protected final List<Material> materials;
    protected final String itemId;

    TicItemBuildEvent(NBTTagCompound root, List<Material> materials, String itemId) {
        this.root = root;
        this.materials = materials;
        this.itemId = itemId == null ? "" : itemId;
    }

    @ZenGetter("itemId")
    public String getItemId() {
        return itemId;
    }

    @ZenGetter("materials")
    public String[] getMaterials() {
        String[] result = new String[materials.size()];
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            result[i] = material == null ? "" : material.getIdentifier();
        }
        return result;
    }

    @ZenMethod
    public boolean hasMaterial(String materialId) {
        if (materialId == null || materialId.trim().isEmpty()) {
            return false;
        }
        for (Material material : materials) {
            if (material != null && materialId.equals(material.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    @ZenMethod
    public String[] getTraits() {
        return TicToolTraits.getTraits(root);
    }

    @ZenMethod
    public boolean hasTrait(String traitId) {
        return TicToolTraits.hasTrait(root, traitId);
    }

    @ZenMethod
    public boolean addTrait(String traitId, int color, int level) {
        return applyRegisteredTrait(traitId, color, level);
    }

    @ZenMethod
    public boolean applyRegisteredTrait(String traitId, int color, int level) {
        return TicToolTraits.applyBuildTrait(root, traitId, color, level);
    }

    @ZenMethod
    public boolean removeTrait(String traitId) {
        return TicToolTraits.removeBuildTrait(root, traitId);
    }

    @ZenMethod
    public String[] getBaseModifiers() {
        return TicToolTraits.getBaseModifiers(root);
    }

    @ZenMethod
    public boolean hasBaseModifier(String traitOrModifierId) {
        return TicToolTraits.hasBaseModifier(root, traitOrModifierId);
    }

    @ZenMethod
    public boolean addBaseModifier(String traitOrModifierId) {
        return TicToolTraits.addBaseModifier(root, traitOrModifierId);
    }

    @ZenMethod
    public boolean removeBaseModifier(String traitOrModifierId) {
        return TicToolTraits.removeBaseModifier(root, traitOrModifierId);
    }

    @ZenMethod
    public String[] getStats() {
        return TicStatPatches.getNumericStatKeys(TagUtil.getToolTag(root));
    }

    @ZenMethod
    public boolean hasStat(String statName) {
        return TicStatPatches.hasNumericStat(TagUtil.getToolTag(root), statName);
    }

    @ZenMethod
    public float getFloatStat(String statName) {
        if (!hasStat(statName)) {
            return 0.0F;
        }
        return TagUtil.getToolTag(root).getFloat(statName);
    }

    @ZenMethod
    public int getIntStat(String statName) {
        if (!hasStat(statName)) {
            return 0;
        }
        return TagUtil.getToolTag(root).getInteger(statName);
    }

    @ZenMethod
    public boolean addStat(String statName, float amount) {
        NBTTagCompound stats = TagUtil.getToolTag(root).copy();
        if (!TicStatPatches.addNumericStat(stats, statName, amount)) {
            return false;
        }
        TagUtil.setToolTag(root, stats);
        return true;
    }

    @ZenMethod
    public boolean addIntStat(String statName, int amount) {
        return addStat(statName, amount);
    }
}
