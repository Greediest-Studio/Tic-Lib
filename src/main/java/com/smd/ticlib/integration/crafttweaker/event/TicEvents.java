package com.smd.ticlib.integration.crafttweaker.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.ticlib.TicEvents")
@ZenRegister
public final class TicEvents {

    public static final TicEvents INSTANCE = new TicEvents();

    private final EventList<TicToolBuildEvent> toolBuildHandlers = new EventList<>();
    private final EventList<TicArmorBuildEvent> armorBuildHandlers = new EventList<>();

    private TicEvents() {
    }

    @ZenMethod
    public static IEventHandle onToolBuild(IEventHandler<TicToolBuildEvent> handler) {
        return INSTANCE.toolBuildHandlers.add(handler);
    }

    @ZenMethod
    public static IEventHandle onArmorBuild(IEventHandler<TicArmorBuildEvent> handler) {
        return INSTANCE.armorBuildHandlers.add(handler);
    }

    @ZenMethod
    public static void clear() {
        INSTANCE.toolBuildHandlers.clear();
        INSTANCE.armorBuildHandlers.clear();
    }

    public boolean hasToolBuildHandlers() {
        return toolBuildHandlers.hasHandlers();
    }

    public void publishToolBuild(TicToolBuildEvent event) {
        toolBuildHandlers.publish(event);
    }

    public boolean hasArmorBuildHandlers() {
        return armorBuildHandlers.hasHandlers();
    }

    public void publishArmorBuild(TicArmorBuildEvent event) {
        armorBuildHandlers.publish(event);
    }
}
