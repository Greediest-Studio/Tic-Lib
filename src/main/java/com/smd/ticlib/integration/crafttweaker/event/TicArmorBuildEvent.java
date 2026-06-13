package com.smd.ticlib.integration.crafttweaker.event;

import c4.conarm.lib.tinkering.TinkersArmor;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.List;

@ZenRegister
@ZenClass("mods.ticlib.event.ArmorBuildEvent")
public final class TicArmorBuildEvent extends TicItemBuildEvent {

    private final TinkersArmor armor;

    TicArmorBuildEvent(NBTTagCompound root, List<Material> materials, TinkersArmor armor) {
        super(root, materials, armor == null || armor.getRegistryName() == null ? "" : armor.getRegistryName().toString());
        this.armor = armor;
    }

    @ZenGetter("armorId")
    public String getArmorId() {
        return itemId;
    }

    public TinkersArmor getArmor() {
        return armor;
    }
}
