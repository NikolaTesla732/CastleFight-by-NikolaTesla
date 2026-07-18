package com.custom.castlefight.custom_castlefight.client;

import com.custom.castlefight.custom_castlefight.blocks.BuildingBlock;
import com.custom.castlefight.custom_castlefight.client.bind.AllKeyUtils;
import com.custom.castlefight.custom_castlefight.client.clientFunc.ClientTempStorage;
import com.custom.castlefight.custom_castlefight.client.render.Draw;
import com.custom.castlefight.custom_castlefight.client.render.blockModel.BuildingBlockModel;
import com.custom.castlefight.custom_castlefight.client.screen.handled.HandledRegister;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.render.BlockRenderLayer;

public class Custom_castlefightClient implements ClientModInitializer {
    public static ClientTempStorage CLIENT_TEMP;
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(BuildingBlock.BUILDING_BLOCK, BlockRenderLayer.CUTOUT);
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.modifyBlockModelAfterBake().register(((model, context) -> {
                if (context.state().isOf(BuildingBlock.BUILDING_BLOCK)){
                    return new BuildingBlockModel();
                }
                return model;
            }));
        });
        AllKeyUtils.register();
        WorldRenderEvents.END_MAIN.register(Draw::SelectBlock);
        RegisterReciverS2CPackets.register();
        HandledRegister.register();
        ClientLifecycleEvents.CLIENT_STARTED.register( minecraftClient -> {
            CLIENT_TEMP = new ClientTempStorage();
        });

    }

}
