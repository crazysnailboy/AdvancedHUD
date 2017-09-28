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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class GuiAdvancedHUD extends GuiIngameForge {

    private static final Field eventParentField = ReflectionHelper.getDeclaredField(GuiIngameForge.class, "eventParent");
    private static final Field fontRendererField = ReflectionHelper.getDeclaredField(GuiIngameForge.class, "fontrenderer");
    private static final Method renderHelmetMethod = ReflectionHelper.getDeclaredMethod(GuiIngameForge.class, "renderHelmet", ScaledResolution.class, float.class, boolean.class, int.class, int.class);

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
    public void renderGameOverlay(float partialTicks, boolean hasScreen, int mouseX, int mouseY) {

        this.mc.mcProfiler.startSection(AdvancedHUD.NAME);
        this.partialTicks = partialTicks;

        this.res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        HUDRegistry.checkForResize(this.res);
        ReflectionHelper.setFieldValue(eventParentField, this, new RenderGameOverlayEvent(partialTicks, this.res, mouseX, mouseY));
        int width = this.res.getScaledWidth();
        int height = this.res.getScaledHeight();
        renderHealthMount = this.mc.thePlayer.ridingEntity instanceof EntityLivingBase;
        renderFood = this.mc.thePlayer.ridingEntity == null;
        renderJumpBar = this.mc.thePlayer.isRidingHorse();

        right_height = 39;
        left_height = 39;

        if (this.pre(ALL)) {
            this.mc.mcProfiler.endSection();
            return;
        }

        ReflectionHelper.setFieldValue(fontRendererField, this, this.mc.fontRendererObj);
        this.mc.entityRenderer.setupOverlayRendering();
        GL11.glEnable(GL11.GL_BLEND);

        if (Minecraft.isFancyGraphicsEnabled()) {
            this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), width, height);
        } else {
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        if (renderHelmet) this.renderHelmet(this.res, partialTicks, hasScreen, mouseX, mouseY);
        if (renderPortal && !this.mc.thePlayer.isPotionActive(Potion.confusion)) this.renderPortal(width, height, partialTicks);

        if (!this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.zLevel = -90.0F;
            this.rand.setSeed((long)(this.updateCounter * 312871));

            if (renderCrosshairs) this.renderCrosshairs(width, height);
            if (renderBossHealth) this.renderBossHealth();

            if (this.shouldDrawHUD()) {
                if (renderHealth) this.renderHealth(width, height);
                if (renderArmor) this.renderArmor(width, height);
                if (renderFood) this.renderFood(width, height);
                if (renderHealthMount) this.renderHealthMount(width, height);
                if (renderAir) this.renderAir(width, height);
            }
            if (renderHotbar) this.renderHotbar(width, height, partialTicks);
        }

        if (renderJumpBar) {
            this.renderJumpBar(width, height);
        } else if (renderExperiance) {
            this.renderExperience(width, height);
        }

        this.renderSleepFade(width, height);
        this.renderToolHightlight(width, height);
        this.renderHUDText(width, height);
        this.renderRecordOverlay(width, height, partialTicks);

        ScoreObjective objective = this.getScoreboardObjective();
        if (renderObjective && objective != null) {
            this.renderScoreboard(objective, height, width, this.mc.fontRendererObj);
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        this.renderChat(width, height);
        this.renderPlayerList(width, height);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        this.mc.mcProfiler.endSection();
        this.post(ALL);
    }

    private boolean shouldDrawHUD() {
        return
            this.mc.playerController.shouldDrawHUD() ||
            mc.currentScreen instanceof GuiAdvancedHUDConfiguration || mc.currentScreen instanceof GuiScreenReposition || mc.currentScreen instanceof GuiScreenHudItem;
    }

    public ScoreObjective getScoreboardObjective() {
        ScoreObjective objective = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
        return objective;
    }

    private void renderHelmet(ScaledResolution res, float partialTicks, boolean hasScreen, int mouseX, int mouseY) {
        ReflectionHelper.invokeMethod(renderHelmetMethod, this, res, partialTicks, hasScreen, mouseX, mouseY);
    }

    @Override
    protected void renderPortal(int width, int height, float partialTicks) {
        super.renderPortal(width, height, partialTicks);
    }

    @Override
    protected void renderCrosshairs(int width, int height) {
        if (this.pre(CROSSHAIRS)) return;
        this.mc.mcProfiler.startSection("crosshairs");

        crosshairs.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(CROSSHAIRS);
    }

    @Override
    protected void renderBossHealth() {
        if (this.pre(BOSSHEALTH)) return;
        this.mc.mcProfiler.startSection("bossHealth");

        bossbar.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(BOSSHEALTH);
    }

    @Override
    public void renderHealth(int width, int height) {
        if (this.pre(HEALTH)) return;
        this.mc.mcProfiler.startSection("health");

        health.render(this.partialTicks);
        left_height += 10;

        this.mc.mcProfiler.endSection();
        this.post(HEALTH);
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
    protected void renderHotbar(int width, int height, float partialTicks) {
        if (this.pre(HOTBAR)) return;
        this.mc.mcProfiler.startSection("actionBar");

        hotbar.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(HOTBAR);
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
        this.mc.mcProfiler.startSection("experiencebar");

        experiencebar.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(EXPERIENCE);
    }

    @Override
    protected void renderToolHightlight(int width, int height) {
        if (this.mc.gameSettings.heldItemTooltips) {
            this.mc.mcProfiler.startSection("toolHighlight");

            itemtooltip.render(this.partialTicks);

            this.mc.mcProfiler.endSection();
        }
    }

    @Override
    protected void renderRecordOverlay(int width, int height, float partialTicks) {
        this.mc.mcProfiler.startSection("overlayMessage");

        record.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
    }

    @Override
    protected void renderScoreboard(ScoreObjective objective, int width, int height, FontRenderer fontrenderer) {
        if (objective != null) {

            scoreboard.render(this.partialTicks);

        }
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
    public void setRecordPlaying(String recordName, boolean isPlaying) {
        record.setRecordPlaying(recordName, isPlaying);
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
