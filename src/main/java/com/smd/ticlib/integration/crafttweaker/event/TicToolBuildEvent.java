package com.smd.ticlib.integration.crafttweaker.event;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.List;

@ZenRegister
@ZenClass("mods.ticlib.event.ToolBuildEvent")
public final class TicToolBuildEvent extends TicItemBuildEvent {

    private final TinkersItem tool;

    TicToolBuildEvent(NBTTagCompound root, List<Material> materials, TinkersItem tool) {
        super(root, materials, tool == null || tool.getRegistryName() == null ? "" : tool.getRegistryName().toString());
        this.tool = tool;
    }

    @ZenGetter("toolId")
    public String getToolId() {
        return itemId;
    }

    public TinkersItem getTool() {
        return tool;
    }
}
