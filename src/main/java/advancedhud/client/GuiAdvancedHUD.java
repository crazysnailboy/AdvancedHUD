package advancedhud.client;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.lwjgl.opengl.GL11;
import advancedhud.AdvancedHUD;
import advancedhud.ReflectionHelper;
import advancedhud.SaveController;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.client.huditems.HudItemAir;
import advancedhud.client.huditems.HudItemArmor;
import advancedhud.client.huditems.HudItemBossBar;
import advancedhud.client.huditems.HudItemCrosshairs;
import advancedhud.client.huditems.HudItemExperienceBar;
import advancedhud.client.huditems.HudItemFood;
import advancedhud.client.huditems.HudItemHealth;
import advancedhud.client.huditems.HudItemHealthMount;
import advancedhud.client.huditems.HudItemHotbar;
import advancedhud.client.huditems.HudItemJumpBar;
import advancedhud.client.huditems.HudItemRecordDisplay;
import advancedhud.client.huditems.HudItemScoreboard;
import advancedhud.client.huditems.HudItemTooltips;
import advancedhud.client.ui.GuiAdvancedHUDConfiguration;
import advancedhud.client.ui.GuiScreenHudItem;
import advancedhud.client.ui.GuiScreenReposition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class GuiAdvancedHUD extends GuiIngameForge {

    private static final Field eventParentField = ReflectionHelper.getDeclaredField(GuiIngameForge.class, "eventParent");
    private static final Field fontRendererField = ReflectionHelper.getDeclaredField(GuiIngameForge.class, "fontrenderer");
    private static final Field resField = ReflectionHelper.getDeclaredField(GuiIngameForge.class, "res");
    private static final Method renderHelmetMethod = ReflectionHelper.getDeclaredMethod(GuiIngameForge.class, "renderHelmet", ScaledResolution.class, float.class);

    private HudItemAir air = (HudItemAir)HUDRegistry.getHudItemByName("air");
    private HudItemArmor armor = (HudItemArmor)HUDRegistry.getHudItemByName("armor");
    private HudItemBossBar bossbar = (HudItemBossBar)HUDRegistry.getHudItemByName("bossbar");
    private HudItemCrosshairs crosshairs = (HudItemCrosshairs)HUDRegistry.getHudItemByName("crosshair");
    private HudItemExperienceBar experiencebar = (HudItemExperienceBar)HUDRegistry.getHudItemByName("experiencebar");
    private HudItemFood food = (HudItemFood)HUDRegistry.getHudItemByName("food");
    private HudItemHealth health = (HudItemHealth)HUDRegistry.getHudItemByName("health");
    private HudItemHealthMount healthmount = (HudItemHealthMount)HUDRegistry.getHudItemByName("healthmount");
    private HudItemHotbar hotbar = (HudItemHotbar)HUDRegistry.getHudItemByName("hotbar");
    private HudItemJumpBar jumpbar = (HudItemJumpBar)HUDRegistry.getHudItemByName("jumpbar");
    private HudItemRecordDisplay record = (HudItemRecordDisplay)HUDRegistry.getHudItemByName("record");
    private HudItemScoreboard scoreboard = (HudItemScoreboard)HUDRegistry.getHudItemByName("scoreboard");
    private HudItemTooltips itemtooltip = (HudItemTooltips)HUDRegistry.getHudItemByName("itemtooltip");

    public static float partialTicks;
    private long lastTick = System.currentTimeMillis();
    private ScaledResolution res = null;

    public GuiAdvancedHUD(Minecraft mc) {
        super(mc);
        if (!SaveController.loadConfig("config")) {
            HUDRegistry.checkForResize();
            HUDRegistry.resetAllDefaults();
            SaveController.saveConfig("config");
        }
    }

    @Override
    public void renderGameOverlay(float partialTicks) {

        this.mc.mcProfiler.startSection(AdvancedHUD.NAME);
        this.partialTicks = partialTicks;

        this.res = new ScaledResolution(this.mc); ReflectionHelper.setFieldValue(resField, this, this.res);
        HUDRegistry.checkForResize(this.res);
        ReflectionHelper.setFieldValue(eventParentField, this, new RenderGameOverlayEvent(partialTicks, this.res));
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        renderHealthMount = mc.player.getRidingEntity() instanceof EntityLivingBase;
        renderFood = mc.player.getRidingEntity() == null;
        renderJumpBar = mc.player.isRidingHorse();

        right_height = 39;
        left_height = 39;

        if (this.pre(ALL)) {
            this.mc.mcProfiler.endSection();
            return;
        }

        ReflectionHelper.setFieldValue(fontRendererField, this, this.mc.fontRenderer);
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();

        if (Minecraft.isFancyGraphicsEnabled()) {
            renderVignette(mc.player.getBrightness(partialTicks), res);
        } else {
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        if (renderHelmet) this.renderHelmet(this.res, partialTicks);
        if (renderPortal && !this.mc.player.isPotionActive(MobEffects.NAUSEA)) this.renderPortal(this.res, partialTicks);
        if (renderHotbar) this.renderHotbar(this.res, partialTicks);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.zLevel = -90.0F;
        this.rand.setSeed((long)(this.updateCounter * 312871));

        if (renderCrosshairs) renderCrosshairs(partialTicks);
        if (renderBossHealth) renderBossHealth();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.shouldDrawHUD()) {
            if (renderHealth) renderHealth(width, height);
            if (renderArmor) renderArmor(width, height);
            if (renderFood) renderFood(width, height);
            if (renderHealthMount) renderHealthMount(width, height);
            if (renderAir) renderAir(width, height);
        }

        renderSleepFade(width, height);

        if (renderJumpBar) {
            renderJumpBar(width, height);
        } else if (renderExperiance) {
            renderExperience(width, height);
        }

        renderToolHighlight(res);
        renderHUDText(width, height);
        renderPotionIcons(res);
        renderRecordOverlay(width, height, partialTicks);
        renderSubtitles(res);
        renderTitle(width, height, partialTicks);

        ScoreObjective objective = this.getScoreboardObjective();
        if (renderObjective && objective != null) {
            this.renderScoreboard(objective, this.res);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableAlpha();

        renderChat(width, height);
        renderPlayerList(width, height);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();

        this.mc.mcProfiler.endSection();
        post(ALL);
    }

    private boolean shouldDrawHUD() {
        return
            (this.mc.playerController.shouldDrawHUD() && this.mc.getRenderViewEntity() instanceof EntityPlayer) ||
            mc.currentScreen instanceof GuiAdvancedHUDConfiguration || mc.currentScreen instanceof GuiScreenReposition || mc.currentScreen instanceof GuiScreenHudItem;
    }

    public ScoreObjective getScoreboardObjective() {
        Scoreboard scoreboard = this.mc.world.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.player.getName());
        if (scoreplayerteam != null) {
            int slot = scoreplayerteam.getColor().getColorIndex();
            if (slot >= 0) objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
        }
        ScoreObjective scoreobjective1 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);
        return scoreobjective1;
    }

    private void renderHelmet(ScaledResolution res, float partialTicks) {
        ReflectionHelper.invokeMethod(renderHelmetMethod, this, res, partialTicks);
    }

    @Override
    protected void renderPortal(ScaledResolution res, float partialTicks) {
        super.renderPortal(res, partialTicks);
    }

    @Override
    protected void renderHotbar(ScaledResolution res, float partialTicks) {
        if (pre(HOTBAR)) return;
        if (mc.playerController.isSpectator()) { // TODO - spectator mode
            this.spectatorGui.renderTooltip(res, partialTicks);
        } else {

            hotbar.render(this.partialTicks);

        }
        post(HOTBAR);
    }

    @Override
    protected void renderCrosshairs(float partialTicks) {
        if (pre(CROSSHAIRS)) return;

        crosshairs.render(this.partialTicks);

        post(CROSSHAIRS);
    }

    @Override
    protected void renderBossHealth() {
        if (pre(BOSSHEALTH)) return;
        mc.mcProfiler.startSection("bossHealth");

        bossbar.render(this.partialTicks, this);

        mc.mcProfiler.endSection();
        post(BOSSHEALTH);
    }

    @Override
    public void renderHealth(int width, int height) {
        if (pre(HEALTH)) return;
        mc.mcProfiler.startSection("health");

        health.render(this.partialTicks);
        left_height += 10;

        mc.mcProfiler.endSection();
        post(HEALTH);
    }

    @Override
    protected void renderArmor(int width, int height) {
        if (this.pre(ARMOR)) return;
        this.mc.mcProfiler.startSection("armor");

        armor.render(this.partialTicks);
        left_height += 10;

        this.mc.mcProfiler.endSection();
        this.post(ARMOR);
    }

    @Override
    public void renderFood(int width, int height) {
        if (this.pre(FOOD)) return;
        this.mc.mcProfiler.startSection("food");

        food.render(this.partialTicks);
        right_height += 10;

        this.mc.mcProfiler.endSection();
        this.post(FOOD);
    }

    @Override
    protected void renderHealthMount(int width, int height) {
        if (this.pre(HEALTHMOUNT)) return;
        this.mc.mcProfiler.endStartSection("mountHealth");

        healthmount.render(this.partialTicks);
        right_height += 10;

        this.mc.mcProfiler.endSection();
        this.post(HEALTHMOUNT);
    }

    @Override
    protected void renderAir(int width, int height) {
        if (this.pre(AIR)) return;
        this.mc.mcProfiler.startSection("air");

        air.render(this.partialTicks);
        right_height += 10;

        this.mc.mcProfiler.endSection();
        this.post(AIR);
    }

    @Override
    protected void renderJumpBar(int width, int height) {
        if (this.pre(JUMPBAR)) return;
        this.mc.mcProfiler.startSection("jumpBar");

        jumpbar.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(JUMPBAR);
    }

    @Override
    protected void renderExperience(int width, int height) {
        if (this.pre(EXPERIENCE)) return;
        this.mc.mcProfiler.startSection("expBar");

        experiencebar.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(EXPERIENCE);
    }

    @Override
    protected void renderToolHighlight(ScaledResolution res)
    {
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            mc.mcProfiler.startSection("toolHighlight");

            itemtooltip.render(this.partialTicks);

            mc.mcProfiler.endSection();
        } else if (this.mc.player.isSpectator()) { // TODO spectator mode
            this.spectatorGui.renderSelectedItem(res);
        }
    }

    @Override
    protected void renderHUDText(int width, int height) {
        super.renderHUDText(width, height);
    }

    @Override
    protected void renderPotionIcons(ScaledResolution resolution) { // TODO movable potion effects
        super.renderPotionEffects(resolution);
    }

    @Override
    protected void renderRecordOverlay(int width, int height, float partialTicks) {
        if (this.overlayMessageTime > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");

            record.render(this.partialTicks);

            this.mc.mcProfiler.endSection();
        }
    }

    @Override
    protected void renderSubtitles(ScaledResolution resolution) { // TODO movable subtitles
        super.renderSubtitles(resolution);
    }

    @Override
    protected void renderTitle(int width, int height, float partialTicks) {
        super.renderTitle(width, height, partialTicks);
    }

    @Override
    protected void renderChat(int width, int height) { // TODO movable chat
        super.renderChat(width, height);
    }

    @Override
    protected void renderPlayerList(int width, int height) { // TODO movable player list
        super.renderPlayerList(width, height);
    }

    @Override
    public ScaledResolution getResolution() {
        return this.res;
    }

    @Override
    public void updateTick() {
        this.mc.mcProfiler.startSection("Advanced HUD - UpdateTick");

        for (HudItem huditem : HUDRegistry.getTickableItems()) {
            if (huditem.enabled) {
                this.mc.mcProfiler.startSection(huditem.getName());
                huditem.tick();
                this.mc.mcProfiler.endSection();
            }
        }
        this.updateCounter++;

        this.mc.mcProfiler.endSection();
    }

    @Override
    public void setOverlayMessage(String message, boolean animateColor) {
        record.setOverlayMessage(message, animateColor);
    }

    private boolean pre(ElementType type) {
        RenderGameOverlayEvent eventParent = ReflectionHelper.getFieldValue(eventParentField, this);
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }

    private void post(ElementType type) {
        RenderGameOverlayEvent eventParent = ReflectionHelper.getFieldValue(eventParentField, this);
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }

}
