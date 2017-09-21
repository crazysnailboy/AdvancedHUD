package advancedhud.client.huditems;

import advancedhud.AdvancedHUD;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.api.RenderAssist;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class HudItemCrosshairs extends HudItem {

    private static final ResourceLocation CROSSHAIR_ICONS = new ResourceLocation(AdvancedHUD.MODID, "textures/gui/crosshairs.png");
    private int selectedIconX = -1;
    private int selectedIconY = -1;

    @Override
    public String getName() {
        return "crosshair";
    }

    @Override
    public int getDefaultID() {
        return 11;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return Alignment.CENTERCENTER;
    }

    @Override
    public int getDefaultPosX() {
        return HUDRegistry.screenWidth / 2 - 8;
    }

    @Override
    public int getDefaultPosY() {
        return HUDRegistry.screenHeight / 2 - 8;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void render(float partialTicks) {

        if (!(enabled || configMode())) return;

        GameSettings gamesettings = this.mc.gameSettings;
        if (gamesettings.thirdPersonView == 0) {
            if (this.mc.playerController.isSpectator() && this.mc.pointedEntity == null) {
                RayTraceResult raytraceresult = this.mc.objectMouseOver;
                if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                    return;
                }
                BlockPos blockpos = raytraceresult.getBlockPos();
                net.minecraft.block.state.IBlockState state = this.mc.world.getBlockState(blockpos);
                if (!state.getBlock().hasTileEntity(state) || !(this.mc.world.getTileEntity(blockpos) instanceof IInventory)) {
                    return;
                }
            }

            int l = HUDRegistry.screenWidth;
            int i1 = HUDRegistry.screenHeight;

            if (gamesettings.showDebugInfo && !gamesettings.hideGUI && !this.mc.player.hasReducedDebug() && !gamesettings.reducedDebugInfo) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(l / 2), (float)(i1 / 2), RenderAssist.zLevel);
                Entity entity = this.mc.getRenderViewEntity();
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(-1.0F, -1.0F, -1.0F);
                OpenGlHelper.renderDirections(10);
                GlStateManager.popMatrix();
            } else {

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableAlpha();

                if (this.selectedIconX >= 0 && this.selectedIconY >= 0) {
                    this.mc.renderEngine.bindTexture(CROSSHAIR_ICONS);
                    this.drawTexturedModalRect(this.posX, this.posY, this.selectedIconX, this.selectedIconY, 16, 16);
                } else {
                    this.mc.renderEngine.bindTexture(Gui.ICONS);
                    this.drawTexturedModalRect(this.posX, this.posY, 0, 0, 16, 16);
                }

                if (this.mc.gameSettings.attackIndicator == 1) {
                    float f = this.mc.player.getCooledAttackStrength(0.0F);
                    if (f < 1.0F) {
                        if (this.selectedIconX >= 0 && this.selectedIconY >= 0) {
                            this.mc.renderEngine.bindTexture(Gui.ICONS);
                        }
                        this.drawTexturedModalRect(this.posX, this.posY + 16, 36, 94, 16, 4);
                        this.drawTexturedModalRect(this.posX, this.posY + 16, 52, 94, (int)(f * 17.0F), 4);
                    }
                }

                GlStateManager.disableBlend();
            }
        }
    }

    public int getSelectedIconX() {
        return this.selectedIconX;
    }

    public int getSelectedIconY() {
        return this.selectedIconY;
    }

    public void setSelectedIconX(int selectedIconX) {
        this.selectedIconX = selectedIconX;
    }

    public void setSelectedIconY(int selectedIconY) {
        this.selectedIconY = selectedIconY;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound) {
        super.loadFromNBT(compound);
        this.selectedIconX = (compound.hasKey("selectedIconX") ? compound.getInteger("selectedIconX") : -1);
        this.selectedIconY = (compound.hasKey("selectedIconY") ? compound.getInteger("selectedIconY") : -1);
    }

    @Override
    public void saveToNBT(NBTTagCompound compound) {
        compound.setInteger("selectedIconX", this.selectedIconX);
        compound.setInteger("selectedIconY", this.selectedIconY);
        super.saveToNBT(compound);
    }

    @Override
    public boolean isMoveable() {
        return false;
    }

    @Override
    public boolean canRotate() {
        return false;
    }

    @Override
    public boolean shouldDrawOnMount() {
        return true;
    }

}
