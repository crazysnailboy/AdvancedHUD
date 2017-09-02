package advancedhud.client;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import com.mojang.authlib.GameProfile;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.GuiIngameForge;

public class GuiAdvancedHUD extends GuiIngameForge {

    public static float partialTicks;
    private long lastTick = System.currentTimeMillis();

    private ScaledResolution res = null;
    public String recordPlaying;
    public boolean recordIsPlaying;
    public int recordPlayingUpFor = 0;

    private GuiOverlayDebugForge debugOverlay;

    public GuiAdvancedHUD(Minecraft mc) {
        super(mc);
        debugOverlay = new GuiOverlayDebugForge(mc);
    }

    @Override
    public void renderGameOverlay(float partialTicks) {

        this.mc.mcProfiler.startSection("Advanced Hud");
        GuiAdvancedHUD.partialTicks = partialTicks;
        HUDRegistry.checkForResize();

        this.res = new ScaledResolution(this.mc);

        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend(); // GL11.glEnable(GL11.GL_BLEND);

        if (Minecraft.isFancyGraphicsEnabled()) {
            renderVignette(mc.thePlayer.getBrightness(partialTicks), res);
        } else {
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float delta = (System.currentTimeMillis() - this.lastTick) / 1000F;
        this.lastTick = System.currentTimeMillis();

        this.mc.mcProfiler.startSection("modules");
        for (HudItem huditem : HUDRegistry.getHudItemList()) {
            this.mc.mcProfiler.startSection(huditem.getName());

            if (huditem.needsTween()) {
                this.mc.mcProfiler.startSection("tween");
                huditem.update(delta);
                this.mc.mcProfiler.endSection();
            }

            if (this.mc.playerController.isInCreativeMode() && !huditem.isRenderedInCreative()) {
                this.mc.mcProfiler.endSection();
                continue;
            }

            GlStateManager.pushMatrix(); // GL11.glPushMatrix();
            GlStateManager.pushAttrib(); // GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GlStateManager.disableBlend(); // GL11.glDisable(GL11.GL_BLEND);
            if (this.mc.thePlayer.ridingEntity instanceof EntityLivingBase) {
                if (huditem.shouldDrawOnMount()) {
                    huditem.fixBounds();
                    huditem.render(partialTicks);
                }
            } else {
                if (huditem.shouldDrawAsPlayer()) {
                    huditem.fixBounds();
                    huditem.render(partialTicks);
                }
            }
            GlStateManager.popAttrib(); // GL11.glPopAttrib();
            GlStateManager.popMatrix(); // GL11.glPopMatrix();
            this.mc.mcProfiler.endSection();
        }
        this.mc.mcProfiler.endSection();

        this.renderHUDText(HUDRegistry.screenWidth, HUDRegistry.screenHeight);

        int width = HUDRegistry.screenWidth;
        int height = HUDRegistry.screenHeight;

        GlStateManager.enableBlend(); // GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0); // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha(); // GL11.glDisable(GL11.GL_ALPHA_TEST);

        this.renderChat(width, height);

        this.renderPlayerList(width, height);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting(); // GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.enableAlpha(); // GL11.glEnable(GL11.GL_ALPHA_TEST);

        this.mc.mcProfiler.endSection();
    }

    @Override
    protected void renderHUDText(int width, int height) {
        this.mc.mcProfiler.startSection("forgeHudText");
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        ArrayList<String> listL = new ArrayList<String>();
        ArrayList<String> listR = new ArrayList<String>();

        if (this.mc.isDemo()) {
            long time = this.mc.theWorld.getTotalWorldTime();
            if (time >= 120500L) {
                listR.add(I18n.format("demo.demoExpired"));
            } else {
                listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - time))));
            }
        }

        if (this.mc.gameSettings.showDebugInfo) {
            listL.addAll(debugOverlay.getLeft());
            listR.addAll(debugOverlay.getRight());
        }

        int top = 2;
        for (String msg : listL) {
            if (msg == null)
                continue;
            drawRect(1, top - 1, 2 + this.mc.fontRendererObj.getStringWidth(msg) + 1, top + this.mc.fontRendererObj.FONT_HEIGHT - 1, 0x90505050);
            this.mc.fontRendererObj.drawString(msg, 2, top, 0xE0E0E0);
            top += this.mc.fontRendererObj.FONT_HEIGHT;
        }

        top = 2;
        for (String msg : listR) {
            if (msg == null)
                continue;
            int w = this.mc.fontRendererObj.getStringWidth(msg);
            int left = width - 2 - w;
            drawRect(left - 1, top - 1, left + w + 1, top + this.mc.fontRendererObj.FONT_HEIGHT - 1, 0x90505050);
            this.mc.fontRendererObj.drawString(msg, left, top, 0xE0E0E0);
            top += this.mc.fontRendererObj.FONT_HEIGHT;
        }

        this.mc.mcProfiler.endSection();
    }

    @Override
    protected void renderPlayerList(int width, int height) {
        this.mc.mcProfiler.startSection("playerList");
        ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = this.mc.thePlayer.sendQueue;

        if (this.mc.gameSettings.keyBindPlayerList.isPressed() && (!this.mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null)) { // if (this.mc.gameSettings.keyBindPlayerList.isPressed() && (!this.mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null)) {
            List<?> players = (List<?>)handler.getPlayerInfoMap(); // List<?> players = handler.playerInfoList;
            int maxPlayers = handler.currentServerMaxPlayers;
            int rows = maxPlayers;
            int columns;

            for (columns = 1; rows > 20; rows = (maxPlayers + columns - 1) / columns) {
                columns++;
            }

            int columnWidth = 300 / columns;

            if (columnWidth > 150) {
                columnWidth = 150;
            }

            int left = (width - columns * columnWidth) / 2;
            byte border = 10;
            drawRect(left - 1, border - 1, left + columnWidth * columns, border + 9 * rows, Integer.MIN_VALUE);

            for (int i = 0; i < maxPlayers; i++) {
                int xPos = left + i % columns * columnWidth;
                int yPos = border + i / columns * 9;
                drawRect(xPos, yPos, xPos + columnWidth - 1, yPos + 8, 0x20FFFFFF);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);

                if (i < players.size()) {
                    NetworkPlayerInfo player = (NetworkPlayerInfo)players.get(i); // GuiPlayerInfo player = (GuiPlayerInfo)players.get(i);
                    GameProfile gameProfile = player.getGameProfile();
                    ScorePlayerTeam team = this.mc.theWorld.getScoreboard().getPlayersTeam(gameProfile.getName()); // ScorePlayerTeam team = this.mc.theWorld.getScoreboard().getPlayersTeam(player.name);
                    String displayName = ScorePlayerTeam.formatPlayerName(team, gameProfile.getName()); // String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);
                    this.mc.fontRendererObj.drawStringWithShadow(displayName, xPos, yPos, 0xFFFFFF);

                    if (scoreobjective != null) {
                        int endX = xPos + this.mc.fontRendererObj.getStringWidth(displayName) + 5;
                        int maxX = xPos + columnWidth - 12 - 5;

                        if (maxX - endX > 5) {
                            Score score = scoreobjective.getScoreboard().getValueFromObjective(gameProfile.getName(), scoreobjective); // Score score = scoreobjective.getScoreboard().func_96529_a(player.name, scoreobjective);
                            String scoreDisplay = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                            this.mc.fontRendererObj.drawStringWithShadow(scoreDisplay, maxX - this.mc.fontRendererObj.getStringWidth(scoreDisplay), yPos, 0xFFFFFF);
                        }
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    this.mc.renderEngine.bindTexture(Gui.icons);
                    int pingIndex = 4;
                    int ping = player.getResponseTime(); // int ping = player.responseTime;
                    if (ping < 0) {
                        pingIndex = 5;
                    } else if (ping < 150) {
                        pingIndex = 0;
                    } else if (ping < 300) {
                        pingIndex = 1;
                    } else if (ping < 600) {
                        pingIndex = 2;
                    } else if (ping < 1000) {
                        pingIndex = 3;
                    }

                    this.zLevel += 100.0F;
                    this.drawTexturedModalRect(xPos + columnWidth - 12, yPos, 0, 176 + pingIndex * 8, 10, 8);
                    this.zLevel -= 100.0F;
                }
            }
        }
        this.mc.mcProfiler.endSection();
    }

    @Override
    protected void renderChat(int width, int height) {
        this.mc.mcProfiler.startSection("chat");

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, this.mc.displayHeight / 2 - 40F, 0.0F);
        this.persistantChatGUI.drawChat(this.updateCounter);
        GL11.glPopMatrix();

        this.mc.mcProfiler.endSection();
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

    private class GuiOverlayDebugForge extends GuiOverlayDebug {
        private GuiOverlayDebugForge(Minecraft mc) {
            super(mc);
        }

        @Override
        protected void renderDebugInfoLeft() {
        }

        @Override
        protected void renderDebugInfoRight(ScaledResolution res) {
        }

        private List<String> getLeft() {
            return this.call();
        }

        private List<String> getRight() {
            return this.getDebugInfoRight();
        }
    }

}
