package advancedhud.client;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.lwjgl.opengl.GL11;
import advancedhud.ReflectionHelper;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
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

    public static float partialTicks;
    private long lastTick = System.currentTimeMillis();

    private ScaledResolution res = null;

    public String recordPlaying;
    public boolean recordIsPlaying;
    public int recordPlayingUpFor = 0;

    public GuiAdvancedHUD(Minecraft mc) {
        super(mc);
    }

    @Override
    public void renderGameOverlay(float partialTicks, boolean hasScreen, int mouseX, int mouseY) {

        //        this.mc.mcProfiler.startSection("Advanced Hud");
        GuiAdvancedHUD.partialTicks = partialTicks;

        this.res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        HUDRegistry.checkForResize(this.mc, this.res);
        ReflectionHelper.setFieldValue(eventParentField, this, new RenderGameOverlayEvent(partialTicks, this.res, mouseX, mouseY));
        int width = this.res.getScaledWidth();
        int height = this.res.getScaledHeight();
        renderHealthMount = this.mc.thePlayer.ridingEntity instanceof EntityLivingBase;
        renderFood = this.mc.thePlayer.ridingEntity == null;
        renderJumpBar = this.mc.thePlayer.isRidingHorse();

        if (this.pre(ALL)) return;

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

            if (this.mc.playerController.shouldDrawHUD()) {
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

        this.post(ALL);
        //        this.mc.mcProfiler.endSection();
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

        HudItem crosshairs = HUDRegistry.getHudItemByName("crosshair");
        crosshairs.fixBounds();
        crosshairs.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(CROSSHAIRS);
    }

    @Override
    protected void renderBossHealth() {
        if (this.pre(BOSSHEALTH)) return;
        this.mc.mcProfiler.startSection("bossHealth");

        HudItem bosshealth = HUDRegistry.getHudItemByName("bossbar");
        bosshealth.fixBounds();
        bosshealth.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(BOSSHEALTH);
    }

    @Override
    public void renderHealth(int width, int height) {
        if (this.pre(HEALTH)) return;
        this.mc.mcProfiler.startSection("health");

        HudItem health = HUDRegistry.getHudItemByName("health");
        health.fixBounds();
        health.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(HEALTH);
    }

    @Override
    protected void renderArmor(int width, int height) {
        if (this.pre(ARMOR)) return;
        this.mc.mcProfiler.startSection("armor");

        HudItem armor = HUDRegistry.getHudItemByName("armor");
        armor.fixBounds();
        armor.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(ARMOR);
    }

    @Override
    public void renderFood(int width, int height) {
        if (this.pre(FOOD)) return;
        this.mc.mcProfiler.startSection("food");

        HudItem food = HUDRegistry.getHudItemByName("food");
        food.fixBounds();
        food.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(FOOD);
    }

    @Override
    protected void renderHealthMount(int width, int height) {
        if (this.pre(HEALTHMOUNT)) return;
        this.mc.mcProfiler.endStartSection("mountHealth");

        HudItem healthmount = HUDRegistry.getHudItemByName("healthmount");
        healthmount.fixBounds();
        healthmount.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(HEALTHMOUNT);
    }

    @Override
    protected void renderAir(int width, int height) {
        if (this.pre(AIR)) return;
        this.mc.mcProfiler.startSection("air");

        HudItem air = HUDRegistry.getHudItemByName("air");
        air.fixBounds();
        air.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(AIR);
    }

    @Override
    protected void renderHotbar(int width, int height, float partialTicks) {
        if (this.pre(HOTBAR)) return;
        this.mc.mcProfiler.startSection("actionBar");

        HudItem hotbar = HUDRegistry.getHudItemByName("hotbar");
        hotbar.fixBounds();
        hotbar.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(HOTBAR);
    }

    @Override
    protected void renderJumpBar(int width, int height) {
        if (this.pre(JUMPBAR)) return;
        this.mc.mcProfiler.startSection("jumpBar");

        HudItem jumpbar = HUDRegistry.getHudItemByName("jumpbar");
        jumpbar.fixBounds();
        jumpbar.render(partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(JUMPBAR);
    }

    @Override
    protected void renderExperience(int width, int height) {
        if (this.pre(EXPERIENCE)) return;
        this.mc.mcProfiler.startSection("experiencebar");

        HudItem experienceBar = HUDRegistry.getHudItemByName("experiencebar");
        experienceBar.fixBounds();
        experienceBar.render(partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(EXPERIENCE);
    }

    @Override
    protected void renderToolHightlight(int width, int height) {
        if (this.mc.gameSettings.heldItemTooltips) {
            this.mc.mcProfiler.startSection("toolHighlight");

            HudItem toolhighlight = HUDRegistry.getHudItemByName("itemtooltip");
            toolhighlight.fixBounds();
            toolhighlight.render(GuiAdvancedHUD.partialTicks);

            this.mc.mcProfiler.endSection();
        }
    }

    @Override
    protected void renderRecordOverlay(int width, int height, float partialTicks) {
        this.mc.mcProfiler.startSection("overlayMessage");

        HudItem recordoverlay = HUDRegistry.getHudItemByName("record");
        recordoverlay.fixBounds();
        recordoverlay.render(GuiAdvancedHUD.partialTicks);

        this.mc.mcProfiler.endSection();
    }

    @Override
    protected void renderScoreboard(ScoreObjective objective, int width, int height, FontRenderer fontrenderer) {
        if (objective != null) {
            HudItem scoreboard = HUDRegistry.getHudItemByName("scoreboard");
            scoreboard.fixBounds();
            scoreboard.render(GuiAdvancedHUD.partialTicks);
        }
    }

    @Override
    public ScaledResolution getResolution() {
        return this.res;
    }

    @Override
    public void updateTick() {
        this.mc.mcProfiler.startSection("Advanced HUD - UpdateTick");

        if (this.mc.theWorld != null) {
            for (HudItem huditem : HUDRegistry.getHudItemList()) {
                this.mc.mcProfiler.startSection(huditem.getName());
                if (this.mc.playerController.isInCreativeMode() && !huditem.isRenderedInCreative()) {
                    this.mc.mcProfiler.endSection();
                    continue;
                }
                if (huditem.needsTick()) {
                    huditem.tick();
                }
                this.mc.mcProfiler.endSection();
            }
        }

        this.updateCounter++;
        HUDRegistry.updateCounter = this.updateCounter;

        this.mc.mcProfiler.endSection();
    }

    @Override
    public void setRecordPlaying(String recordName, boolean isPlaying) {
        this.recordPlaying = recordName;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = isPlaying;
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
