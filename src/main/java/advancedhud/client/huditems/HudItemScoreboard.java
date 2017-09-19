package advancedhud.client.huditems;

import java.util.Collection;
import java.util.Iterator;
import advancedhud.api.Alignment;
import advancedhud.api.HUDRegistry;
import advancedhud.api.HudItem;
import advancedhud.client.GuiAdvancedHUD;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

public class HudItemScoreboard extends HudItem {

    @Override
    public String getName() {
        return "scoreboard";
    }

    @Override
    public int getDefaultID() {
        return 11;
    }

    @Override
    public Alignment getDefaultAlignment() {
        return Alignment.CENTERRIGHT;
    }

    @Override
    public int getDefaultPosX() {
        return HUDRegistry.screenWidth - 40;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int getDefaultPosY() {
        ScoreObjective objective = this.getScoreboardObjective();
        if (objective != null) {
            Scoreboard scoreboard = objective.getScoreboard();
            Collection collection = scoreboard.getSortedScores(objective);
            if (collection.size() <= 15) {
                int l = collection.size() * this.mc.fontRendererObj.FONT_HEIGHT;
                return HUDRegistry.screenHeight / 2 + l / 3;
            }
        }
        return HUDRegistry.screenHeight / 2 + 21 / 3;
    }

    @Override
    public int getWidth() {
        return 40;
    }

    @Override
    public int getHeight() {
        return 40;
    }

    @Override
    public void render(float partialTicks) {
        ScoreObjective objective = this.getScoreboardObjective();
        if (objective != null) {
            Scoreboard scoreboard = objective.getScoreboard();
            Collection collection = scoreboard.getSortedScores(objective);

            if (collection.size() <= 15) {
                int k = this.mc.fontRendererObj.getStringWidth(objective.getDisplayName());
                String s;

                for (Iterator iterator = collection.iterator(); iterator.hasNext(); k = Math.max(k, this.mc.fontRendererObj.getStringWidth(s))) {
                    Score score = (Score)iterator.next();
                    ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
                    s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
                }

                int i1 = this.posY;
                byte b0 = 3;
                int j1 = HUDRegistry.screenWidth - k - b0;
                int k1 = 0;
                Iterator iterator1 = collection.iterator();

                while (iterator1.hasNext()) {
                    Score score1 = (Score)iterator1.next();
                    ++k1;
                    ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
                    String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                    String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
                    int l1 = i1 - k1 * this.mc.fontRendererObj.FONT_HEIGHT;
                    int i2 = HUDRegistry.screenWidth - b0 + 2;
                    this.posX = j1;
                    this.drawRect(j1 - 2, l1, i2, l1 + this.mc.fontRendererObj.FONT_HEIGHT, 0x50000000);
                    this.mc.fontRendererObj.drawString(s1, j1, l1, 0x20FFFFFF);
                    this.mc.fontRendererObj.drawString(s2, i2 - this.mc.fontRendererObj.getStringWidth(s2), l1, 0x20FFFFFF);

                    if (k1 == collection.size()) {
                        String s3 = objective.getDisplayName();
                        this.drawRect(j1 - 2, l1 - this.mc.fontRendererObj.FONT_HEIGHT - 1, i2, l1 - 1, 0x60000000);
                        this.drawRect(j1 - 2, l1 - 1, i2, l1, 0x50000000);
                        this.mc.fontRendererObj.drawString(s3, j1 + k / 2 - this.mc.fontRendererObj.getStringWidth(s3) / 2, l1 - this.mc.fontRendererObj.FONT_HEIGHT, 0x20FFFFFF);
                    }
                }
            }
        }
    }

    private ScoreObjective getScoreboardObjective() {
        if (this.mc.ingameGUI instanceof GuiAdvancedHUD) {
            GuiAdvancedHUD ingameGUI = (GuiAdvancedHUD)this.mc.ingameGUI;
            ScoreObjective objective = ingameGUI.getScoreboardObjective();
            return objective;
        } else {
            return null;
        }
    }

}
