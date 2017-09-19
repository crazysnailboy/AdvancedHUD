package advancedhud.client;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.lwjgl.opengl.GL11;
import advancedhud.ReflectionHelper;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
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
    private static final Method renderHelmetMethod = ReflectionHelper.getDeclaredMethod(GuiIngameForge.class, "renderHelmet", ScaledResolution.class, float.class);

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
    public void renderGameOverlay(float partialTicks) {

//        this.mc.mcProfiler.startSection("Advanced Hud");
        this.partialTicks = partialTicks;

        this.res = new ScaledResolution(this.mc);
        HUDRegistry.checkForResize();
        ReflectionHelper.setFieldValue(eventParentField, this, new RenderGameOverlayEvent(partialTicks, this.res));
        int width = this.res.getScaledWidth();
        int height = this.res.getScaledHeight();
        renderHealthMount = this.mc.thePlayer.ridingEntity instanceof EntityLivingBase;
        renderFood = this.mc.thePlayer.ridingEntity == null;
        renderJumpBar = this.mc.thePlayer.isRidingHorse();

        if (this.pre(ALL)) return;

        ReflectionHelper.setFieldValue(fontRendererField, this, this.mc.fontRendererObj);
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();

        if (Minecraft.isFancyGraphicsEnabled()) {
            renderVignette(mc.thePlayer.getBrightness(partialTicks), res);
        } else {
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        if (renderHelmet) this.renderHelmet(this.res, partialTicks);
        if (renderPortal && !this.mc.thePlayer.isPotionActive(Potion.confusion)) this.renderPortal(this.res, partialTicks);
        if (renderHotbar) this.renderTooltip(this.res, partialTicks);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.zLevel = -90.0F;
        this.rand.setSeed((long)(this.updateCounter * 312871));

        if (renderCrosshairs) this.renderCrosshairs(width, height);
        if (renderBossHealth) this.renderBossHealth();

        if (this.mc.playerController.shouldDrawHUD() && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (renderHealth) this.renderHealth(width, height);
            if (renderArmor) this.renderArmor(width, height);
            if (renderFood) this.renderFood(width, height);
            if (renderHealthMount) this.renderHealthMount(width, height);
            if (renderAir) this.renderAir(width, height);
        }

        this.renderSleepFade(width, height);

        if (renderJumpBar) {
            this.renderJumpBar(width, height);
        } else if (renderExperiance) {
            this.renderExperience(width, height);
        }

        this.renderToolHightlight(this.res);
        this.renderHUDText(width, height);
        this.renderRecordOverlay(width, height, partialTicks);
        this.renderTitle(width, height, partialTicks);

        ScoreObjective objective = this.getScoreboardObjective();
        if (renderObjective && objective != null) {
            this.renderScoreboard(objective, this.res);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableAlpha();

        this.renderChat(width, height);
        this.renderPlayerList(width, height);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();

        this.post(ALL);
//        this.mc.mcProfiler.endSection();
    }

    public ScoreObjective getScoreboardObjective() {
        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());
        if (scoreplayerteam != null) {
            int slot = scoreplayerteam.getChatFormat().getColorIndex();
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
    protected void renderTooltip(ScaledResolution res, float partialTicks) { // this method seems to be misnamed in 1.8.x
        if (this.pre(HOTBAR)) return;
        if (mc.playerController.isSpectator()) { // TODO - spectator mode
            this.spectatorGui.renderTooltip(res, partialTicks);
        } else {

            HudItem hotbar = HUDRegistry.getHudItemByName("hotbar");
            hotbar.fixBounds();
            hotbar.render(this.partialTicks);

        }
        this.post(HOTBAR);
    }

    @Override
    protected void renderCrosshairs(int width, int height) {
        if (this.pre(CROSSHAIRS)) return;
        if (this.showCrosshair()) {

            HudItem crosshairs = HUDRegistry.getHudItemByName("crosshair");
            crosshairs.fixBounds();
            crosshairs.render(this.partialTicks);

        }
        this.post(CROSSHAIRS);
    }

    @Override
    protected void renderBossHealth() {
        if (this.pre(BOSSHEALTH)) return;
        this.mc.mcProfiler.startSection("bossHealth");

        HudItem bosshealth = HUDRegistry.getHudItemByName("bossbar");
        bosshealth.fixBounds();
        bosshealth.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(BOSSHEALTH);
    }

    @Override
    public void renderHealth(int width, int height) {
        if (this.pre(HEALTH)) return;
        this.mc.mcProfiler.startSection("health");

        HudItem health = HUDRegistry.getHudItemByName("health");
        health.fixBounds();
        health.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(HEALTH);
    }

    @Override
    protected void renderArmor(int width, int height) {
        if (this.pre(ARMOR)) return;
        this.mc.mcProfiler.startSection("armor");

        HudItem armor = HUDRegistry.getHudItemByName("armor");
        armor.fixBounds();
        armor.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(ARMOR);
    }

    @Override
    public void renderFood(int width, int height) {
        if (this.pre(FOOD)) return;
        this.mc.mcProfiler.startSection("food");

        HudItem food = HUDRegistry.getHudItemByName("food");
        food.fixBounds();
        food.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(FOOD);
    }

    @Override
    protected void renderHealthMount(int width, int height) {
        if (this.pre(HEALTHMOUNT)) return;
        this.mc.mcProfiler.endStartSection("mountHealth");

        HudItem healthmount = HUDRegistry.getHudItemByName("healthmount");
        healthmount.fixBounds();
        healthmount.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(HEALTHMOUNT);
    }

    @Override
    protected void renderAir(int width, int height) {
        if (this.pre(AIR)) return;
        this.mc.mcProfiler.startSection("air");

        HudItem air = HUDRegistry.getHudItemByName("air");
        air.fixBounds();
        air.render(this.partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(AIR);
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
        this.mc.mcProfiler.startSection("expBar");

        HudItem experienceBar = HUDRegistry.getHudItemByName("experiencebar");
        experienceBar.fixBounds();
        experienceBar.render(partialTicks);

        this.mc.mcProfiler.endSection();
        this.post(EXPERIENCE);
    }

    @Override
    protected void renderToolHightlight(ScaledResolution res) {
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.mc.mcProfiler.startSection("toolHighlight");

            HudItem toolhighlight = HUDRegistry.getHudItemByName("itemtooltip");
            toolhighlight.fixBounds();
            toolhighlight.render(this.partialTicks);

            this.mc.mcProfiler.endSection();
        } else if (this.mc.thePlayer.isSpectator()) { // TODO spectator mode
            this.spectatorGui.renderSelectedItem(res);
        }
    }

    @Override
    protected void renderHUDText(int width, int height) {
        super.renderHUDText(width, height);
    }

    @Override
    protected void renderRecordOverlay(int width, int height, float partialTicks) {
        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");

            HudItem recordoverlay = HUDRegistry.getHudItemByName("record");
            recordoverlay.fixBounds();
            recordoverlay.render(this.partialTicks);

            this.mc.mcProfiler.endSection();
        }
    }

    @Override
    protected void renderTitle(int width, int height, float partialTicks) {
        super.renderTitle(width, height, partialTicks);
    }

    @Override
    protected void renderScoreboard(ScoreObjective objective, ScaledResolution res) {
        if (objective != null) {
            HudItem scoreboard = HUDRegistry.getHudItemByName("scoreboard");
            scoreboard.fixBounds();
            scoreboard.render(this.partialTicks);
        }
    }

    @Override
    protected void renderChat(int width, int height) {
        super.renderChat(width, height);
    }

    @Override
    protected void renderPlayerList(int width, int height) {
        super.renderPlayerList(width, height);
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
