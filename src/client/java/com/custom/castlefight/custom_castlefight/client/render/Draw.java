package com.custom.castlefight.custom_castlefight.client.render;

import com.custom.castlefight.custom_castlefight.blocks.BuildBlock;
import com.custom.castlefight.custom_castlefight.blocks.blockitems.BuildItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class Draw {
    public static void drawBox(MatrixStack matrices,
                               VertexConsumer vc,
                               Box box,
                               float r, float g, float b, float a) {

        Matrix4f mat = matrices.peek().getPositionMatrix();

        line(vc, mat, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, r, g, b, a);
        line(vc, mat, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, r, g, b, a);
        line(vc, mat, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, r, g, b, a);
        line(vc, mat, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, r, g, b, a);

        line(vc, mat, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a);
        line(vc, mat, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
        line(vc, mat, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a);
        line(vc, mat, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, r, g, b, a);

        line(vc, mat, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, r, g, b, a);
        line(vc, mat, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a);
        line(vc, mat, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
        line(vc, mat, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a);
    }

    private static void line(VertexConsumer vc, Matrix4f mat,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             float r, float g, float b, float a) {

        vc.vertex(mat, (float)x1, (float)y1, (float)z1)
                .color(r, g, b, a)
                .normal(0, 0, 0);

        vc.vertex(mat, (float)x2, (float)y2, (float)z2)
                .color(r, g, b, a)
                .normal(0, 0, 0);
    }

    public static void SelectBlock(WorldRenderContext context){
        MatrixStack matrices = context.matrices();
        ClientWorld world = context.gameRenderer().getClient().world;
        Camera camera = context.gameRenderer().getCamera();
        PlayerEntity player = context.gameRenderer().getClient().player;
        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;
        HitResult PLAYERVIEW = player.raycast(8,1.0f,false);
        EquipmentSlot slot = player.getActiveHand().getEquipmentSlot();
        if (slot == EquipmentSlot.MAINHAND){

        }
        if ( PLAYERVIEW.getType() == HitResult.Type.BLOCK && player.getMainHandStack().getItem() instanceof BuildItem){
            BlockPos pos = ((BlockHitResult) PLAYERVIEW).getBlockPos().add(0,1,0);
            Box box = new Box(pos);
            box = box.stretch(1,4,1);
            box = box.stretch(-1,0,-1);
            Box shifted = box.offset(-camX,-camY,-camZ);

            VertexConsumerProvider consumers = context.consumers();
            VertexConsumer vc = consumers.getBuffer(RenderLayer.getLines());

            drawBox(matrices,vc,shifted,0f,1f,0f,1f);

        }
    }

}
