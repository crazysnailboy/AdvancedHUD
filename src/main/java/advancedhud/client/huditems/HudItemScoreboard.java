package advancedhud.client.huditems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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

            ArrayList arraylist = Lists.newArrayList(Iterables.filter(collection, new Predicate() {
                public boolean apply(Score score) {
                    return score.getPlayerName() != null && !score.getPlayerName().startsWith("#");
                }

                @Override
                public boolean apply(Object apply) {
                    return this.apply((Score)apply);
                }
            }));
            ArrayList arraylist1;

            if (arraylist.size() > 15) {
                arraylist1 = Lists.newArrayList(Iterables.skip(arraylist, collection.size() - 15));
            } else {
                arraylist1 = arraylist;
            }

            int i = this.mc.fontRendererObj.getStringWidth(objective.getDisplayName());
            String s;

            for (Iterator iterator = collection.iterator(); iterator.hasNext(); i = Math.max(i, this.mc.fontRendererObj.getStringWidth(s))) {
                Score score = (Score)iterator.next();
                ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
                s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            }

            int i1 = arraylist1.size() * this.mc.fontRendererObj.FONT_HEIGHT;
            int j1 = this.posY;
            byte b0 = 3;
            int k1 = HUDRegistry.screenWidth - i - b0;
            int j = 0;
            Iterator iterator1 = arraylist1.iterator();

            while (iterator1.hasNext()) {

                Score score1 = (Score)iterator1.next();
                ++j;
                ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
                String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
                int k = j1 - j * this.mc.fontRendererObj.FONT_HEIGHT;
                int l = HUDRegistry.screenWidth - b0 + 2;
                this.posX = k1;

                this.drawRect(k1 - 2, k, l, k + this.mc.fontRendererObj.FONT_HEIGHT, 0x50000000);
                this.mc.fontRendererObj.drawString(s1, k1, k, 0x20FFFFFF);
                this.mc.fontRendererObj.drawString(s2, l - this.mc.fontRendererObj.getStringWidth(s2), k, 0x20FFFFFF);

                if (j == collection.size()) {
                    String s3 = objective.getDisplayName();
                    this.drawRect(k1 - 2, k - this.mc.fontRendererObj.FONT_HEIGHT - 1, l, k - 1, 0x60000000);
                    this.drawRect(k1 - 2, k - 1, l, k, 0x50000000);
                    this.mc.fontRendererObj.drawString(s3, k1 + k / 2 - this.mc.fontRendererObj.getStringWidth(s3) / 2, k - this.mc.fontRendererObj.FONT_HEIGHT, 0x20FFFFFF);
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
