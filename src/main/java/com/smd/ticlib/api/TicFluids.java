package com.smd.ticlib.api;

import com.smd.ticlib.module.fluid.FluidHandler;
import com.smd.ticlib.module.fluid.FluidModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public final class TicFluids {

    private TicFluids() {
    }

    public static boolean hasTank(ItemStack stack) {
        return FluidModule.INSTANCE.hasTank(stack);
    }

    public static IFluidTankProperties[] getTankProperties(ItemStack stack) {
        return hasTank(stack) ? new FluidHandler(stack).getTankProperties() : new IFluidTankProperties[0];
    }

    public static int getCapacity(ItemStack stack) {
        return FluidModule.INSTANCE.getCapacity(stack);
    }

    public static boolean setCapacity(ItemStack stack, int capacity) {
        return FluidModule.INSTANCE.setCapacity(stack, capacity);
    }

    @Nullable
    public static FluidStack getFluid(ItemStack stack) {
        return FluidModule.INSTANCE.getFluid(stack);
    }

    @Nullable
    public static FluidStack setFluid(ItemStack stack, @Nullable FluidStack fluid) {
        return FluidModule.INSTANCE.setFluid(stack, fluid);
    }

    public static boolean clearFluid(ItemStack stack) {
        return setFluid(stack, null) == null;
    }

    public static int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        return FluidModule.INSTANCE.fill(stack, resource, doFill);
    }

    @Nullable
    public static FluidStack drain(ItemStack stack, FluidStack resource, boolean doDrain) {
        return FluidModule.INSTANCE.drain(stack, resource, doDrain);
    }

    @Nullable
    public static FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        return FluidModule.INSTANCE.drain(stack, maxDrain, doDrain);
    }

    public static boolean interactWithFluidHandler(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side) {
        if (player == null || hand == null || world == null || pos == null) {
            return false;
        }
        return hasTank(player.getHeldItem(hand)) && FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
    }
}
