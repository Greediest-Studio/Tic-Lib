package com.smd.ticlib.integration.crafttweaker.event;

import c4.conarm.lib.events.ArmoryEvent;
import crafttweaker.mc1120.events.ScriptRunEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.events.TinkerEvent;

public final class TicBuildEventForwarder {

    public static final TicBuildEventForwarder INSTANCE = new TicBuildEventForwarder();

    private TicBuildEventForwarder() {
    }

    @SubscribeEvent
    public void onScriptsReload(ScriptRunEvent.Pre event) {
        TicEvents.clear();
    }

    @SubscribeEvent
    public void onToolBuild(TinkerEvent.OnItemBuilding event) {
        if (TicEvents.INSTANCE.hasToolBuildHandlers()) {
            TicEvents.INSTANCE.publishToolBuild(new TicToolBuildEvent(event.tag, event.materials, event.tool));
        }
    }

    @SubscribeEvent
    public void onArmorBuild(ArmoryEvent.OnItemBuilding event) {
        if (TicEvents.INSTANCE.hasArmorBuildHandlers()) {
            TicEvents.INSTANCE.publishArmorBuild(new TicArmorBuildEvent(event.tag, event.materials, event.armor));
        }
    }
}
