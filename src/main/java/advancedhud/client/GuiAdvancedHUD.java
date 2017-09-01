package advancedhud.client;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.GuiIngameForge;

public class GuiAdvancedHUD extends GuiIngameForge {

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
        this.mc.mcProfiler.startSection("Advanced Hud");

        GuiAdvancedHUD.partialTicks = partialTicks;

        HUDRegistry.checkForResize();

        this.mc.entityRenderer.setupOverlayRendering();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);

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

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_BLEND);
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
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            this.mc.mcProfiler.endSection();
        }
        this.mc.mcProfiler.endSection();

        this.renderHUDText(HUDRegistry.screenWidth, HUDRegistry.screenHeight);

        int width = HUDRegistry.screenWidth;
        int height = HUDRegistry.screenHeight;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        this.renderChat(width, height);

        this.renderPlayerList(width, height);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        this.mc.mcProfiler.endSection();
    }

    @Override
    protected void renderHUDText(int width, int height) {
        this.mc.mcProfiler.startSection("forgeHudText");
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        ArrayList<String> left = new ArrayList<String>();
        ArrayList<String> right = new ArrayList<String>();

        if (this.mc.isDemo()) {
            long time = this.mc.theWorld.getTotalWorldTime();
            if (time >= 120500L) {
                right.add(I18n.format("demo.demoExpired"));
            } else {
                right.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - time))));
            }
        }

        if (this.mc.gameSettings.showDebugInfo) {
            GL11.glPushMatrix();
            left.add("Minecraft " + Loader.MC_VERSION + " (" + this.mc.debug + ")");
            left.add(this.mc.debugInfoRenders());
            left.add(this.mc.getEntityDebug());
            left.add(this.mc.debugInfoEntities());
            left.add(this.mc.getWorldProviderName());
            left.add(null); // Spacer

            long max = Runtime.getRuntime().maxMemory();
            long total = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            long used = total - free;

            right.add("Used memory: " + used * 100L / max + "% (" + used / 1024L / 1024L + "MB) of " + max / 1024L / 1024L + "MB");
            right.add("Allocated memory: " + total * 100L / max + "% (" + total / 1024L / 1024L + "MB)");

            int x = MathHelper.floor_double(this.mc.thePlayer.posX);
            int y = MathHelper.floor_double(this.mc.thePlayer.posY);
            int z = MathHelper.floor_double(this.mc.thePlayer.posZ);
            float yaw = this.mc.thePlayer.rotationYaw;
            int heading = MathHelper.floor_double(this.mc.thePlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

            left.add(String.format("x: %.5f (%d) // c: %d (%d)", this.mc.thePlayer.posX, x, x >> 4, x & 15));
            left.add(String.format("y: %.3f (feet pos, %.3f eyes pos)", this.mc.thePlayer.boundingBox.minY, this.mc.thePlayer.posY));
            left.add(String.format("z: %.5f (%d) // c: %d (%d)", this.mc.thePlayer.posZ, z, z >> 4, z & 15));
            left.add(String.format("f: %d (%s) / %f", heading, Direction.directions[heading], MathHelper.wrapAngleTo180_float(yaw)));

            if (this.mc.theWorld != null && this.mc.theWorld.blockExists(x, y, z)) {
                Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(x, z);
                left.add(String.format("lc: %d b: %s bl: %d sl: %d rl: %d", chunk.getTopFilledSegment() + 15, chunk.getBiomeGenForWorldCoords(x & 15, z & 15, this.mc.theWorld.getWorldChunkManager()).biomeName, chunk.getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15), chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15), chunk.getBlockLightValue(x & 15, y, z & 15, 0)));
            } else {
                left.add(null);
            }

            left.add(String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", this.mc.thePlayer.capabilities.getWalkSpeed(), this.mc.thePlayer.capabilities.getFlySpeed(), this.mc.thePlayer.onGround, this.mc.theWorld.getHeightValue(x, z)));
            if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
                left.add(String.format("shader: %s", this.mc.entityRenderer.getShaderGroup().getShaderGroupName()));
            }

            right.add(null);
            for (String brand : FMLCommonHandler.instance().getBrandings(false)) {
                right.add(brand);
            }
            GL11.glPopMatrix();
        }

        for (int x = 0; x < left.size(); x++) {
            String msg = left.get(x);
            if (msg == null) {
                continue;
            }
            this.mc.fontRendererObj.drawStringWithShadow(msg, 2, 2 + x * 10, 0xFFFFFF);
        }

        for (int x = 0; x < right.size(); x++) {
            String msg = right.get(x);
            if (msg == null) {
                continue;
            }
            int w = this.mc.fontRendererObj.getStringWidth(msg);
            this.mc.fontRendererObj.drawStringWithShadow(msg, width - w - 10, 2 + x * 10, 0xFFFFFF);
        }

        this.mc.mcProfiler.endSection();
    }

    @Override
    protected void renderPlayerList(int width, int height) {
        this.mc.mcProfiler.startSection("playerList");
        ScoreObjective scoreobjective = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = this.mc.thePlayer.sendQueue;

        if (this.mc.gameSettings.keyBindPlayerList.isPressed() && (!this.mc.isIntegratedServerRunning() || handler.playerInfoList.size() > 1 || scoreobjective != null)) {
            List<?> players = handler.playerInfoList;
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
                    GuiPlayerInfo player = (GuiPlayerInfo)players.get(i);
                    ScorePlayerTeam team = this.mc.theWorld.getScoreboard().getPlayersTeam(player.name);
                    String displayName = ScorePlayerTeam.formatPlayerName(team, player.name);
                    this.mc.fontRendererObj.drawStringWithShadow(displayName, xPos, yPos, 0xFFFFFF);

                    if (scoreobjective != null) {
                        int endX = xPos + this.mc.fontRendererObj.getStringWidth(displayName) + 5;
                        int maxX = xPos + columnWidth - 12 - 5;

                        if (maxX - endX > 5) {
                            Score score = scoreobjective.getScoreboard().getValueFromObjective(player.name, scoreobjective);
                            String scoreDisplay = EnumChatFormatting.YELLOW + "" + score.getScorePoints();
                            this.mc.fontRendererObj.drawStringWithShadow(scoreDisplay, maxX - this.mc.fontRendererObj.getStringWidth(scoreDisplay), yPos, 0xFFFFFF);
                        }
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    this.mc.renderEngine.bindTexture(Gui.icons);
                    int pingIndex = 4;
                    int ping = player.responseTime;
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
}
