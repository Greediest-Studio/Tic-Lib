package com.smd.ticlib.module.fluid;

import com.smd.ticlib.core.data.TicDataAccess;
import com.smd.ticlib.core.lifecycle.TicLifecycleContext;
import com.smd.ticlib.core.lifecycle.TicModule;
import com.smd.ticlib.core.target.TicTarget;
import com.smd.ticlib.core.target.TicTargetKind;
import com.smd.ticlib.core.target.TicTargets;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public final class FluidModule implements TicModule {

    public static final FluidModule INSTANCE = new FluidModule();
    public static final String ID = "ticlib:fluid";

    private static final String CAPACITY = "Capacity";
    private static final String FLUID = "Fluid";

    private FluidModule() {
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean supports(TicTargetKind kind) {
        return kind == TicTargetKind.TOOL || kind == TicTargetKind.ARMOR;
    }

    @Override
    public void onValidate(TicLifecycleContext context) {
        ItemStack stack = context.stack();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        FluidStack fluid = getFluid(stack);
        int capacity = getCapacity(stack);
        if (capacity <= 0 || fluid == null) {
            setFluid(stack, null);
        } else if (fluid.amount > capacity) {
            fluid.amount = capacity;
            setFluid(stack, fluid);
        }
    }

    public boolean hasTank(ItemStack stack) {
        return TicTargets.isTarget(stack) && stack.getCount() == 1 && new FluidHandler(stack).getTankProperties().length > 0;
    }

    public int getCapacity(ItemStack stack) {
        TicDataAccess data = data(stack);
        NBTTagCompound component = data == null ? null : data.component(ID);
        return component == null ? 0 : Math.max(0, component.getInteger(CAPACITY));
    }

    public boolean setCapacity(ItemStack stack, int capacity) {
        if (!TicTargets.isTarget(stack)) {
            return false;
        }
        TicDataAccess data = data(stack);
        NBTTagCompound component = data.writableComponent(ID);
        component.setInteger(CAPACITY, Math.max(0, capacity));
        data.markDirty();
        data.commit();
        setFluid(stack, getFluid(stack));
        return true;
    }

    @Nullable
    public FluidStack getFluid(ItemStack stack) {
        TicDataAccess data = data(stack);
        NBTTagCompound component = data == null ? null : data.component(ID);
        if (component == null || !component.hasKey(FLUID, Constants.NBT.TAG_COMPOUND)) {
            return null;
        }
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(component.getCompoundTag(FLUID));
        return fluid == null || fluid.amount <= 0 ? null : fluid;
    }

    @Nullable
    public FluidStack setFluid(ItemStack stack, @Nullable FluidStack fluid) {
        if (!TicTargets.isTarget(stack)) {
            return null;
        }
        int capacity = getCapacity(stack);
        TicDataAccess data = data(stack);
        NBTTagCompound component = data.writableComponent(ID);
        if (fluid == null || fluid.amount <= 0 || capacity <= 0) {
            component.removeTag(FLUID);
            data.markDirty();
            data.commit();
            return null;
        }
        FluidStack stored = fluid.copy();
        if (stored.amount > capacity) {
            stored.amount = capacity;
        }
        component.setTag(FLUID, stored.writeToNBT(new NBTTagCompound()));
        data.markDirty();
        data.commit();
        return stored;
    }

    public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0 || !TicTargets.isTarget(stack) || stack.getCount() != 1) {
            return 0;
        }
        return new FluidHandler(stack).fill(resource, doFill);
    }

    @Nullable
    public FluidStack drain(ItemStack stack, FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0 || !TicTargets.isTarget(stack) || stack.getCount() != 1) {
            return null;
        }
        return new FluidHandler(stack).drain(resource, doDrain);
    }

    @Nullable
    public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        if (maxDrain <= 0 || !TicTargets.isTarget(stack) || stack.getCount() != 1) {
            return null;
        }
        return new FluidHandler(stack).drain(maxDrain, doDrain);
    }

    int fillInternal(ItemStack stack, FluidStack resource, boolean doFill) {
        int capacity = getCapacity(stack);
        if (capacity <= 0 || resource == null || resource.amount <= 0) {
            return 0;
        }
        FluidStack current = getFluid(stack);
        if (current == null) {
            int filled = Math.min(capacity, resource.amount);
            if (doFill && filled > 0) {
                setFluid(stack, new FluidStack(resource, filled));
            }
            return filled;
        }
        if (!current.isFluidEqual(resource) || current.amount >= capacity) {
            return 0;
        }
        int filled = Math.min(resource.amount, capacity - current.amount);
        if (doFill && filled > 0) {
            current.amount += filled;
            setFluid(stack, current);
        }
        return filled;
    }

    @Nullable
    FluidStack drainInternal(ItemStack stack, FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) {
            return null;
        }
        FluidStack current = getFluid(stack);
        if (current == null || !current.isFluidEqual(resource)) {
            return null;
        }
        return drainCurrent(stack, current, resource.amount, doDrain);
    }

    @Nullable
    FluidStack drainInternal(ItemStack stack, int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return null;
        }
        FluidStack current = getFluid(stack);
        return current == null ? null : drainCurrent(stack, current, maxDrain, doDrain);
    }

    @Nullable
    private FluidStack drainCurrent(ItemStack stack, FluidStack current, int maxDrain, boolean doDrain) {
        int drained = Math.min(maxDrain, current.amount);
        if (drained <= 0) {
            return null;
        }
        FluidStack result = new FluidStack(current, drained);
        if (doDrain) {
            current.amount -= drained;
            setFluid(stack, current.amount <= 0 ? null : current);
        }
        return result;
    }

    private static TicDataAccess data(ItemStack stack) {
        TicTarget target = TicTargets.resolve(stack);
        return target.isValid() ? target.data() : null;
    }
}
