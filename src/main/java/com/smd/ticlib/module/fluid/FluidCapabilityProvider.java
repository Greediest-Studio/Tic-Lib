package com.smd.ticlib.module.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FluidCapabilityProvider implements ICapabilityProvider {

    private final ItemStack stack;
    private final IFluidHandlerItem handler;

    public FluidCapabilityProvider(ItemStack stack) {
        this.stack = stack;
        this.handler = new FluidHandler(stack);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && FluidModule.INSTANCE.hasTank(stack);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(handler) : null;
    }
}
