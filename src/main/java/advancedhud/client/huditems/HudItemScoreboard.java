package advancedhud.client.huditems;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
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
import net.minecraft.util.text.TextFormatting;

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
                int l = collection.size() * this.mc.fontRenderer.FONT_HEIGHT;
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

        if (!(enabled || configMode())) return;

        ScoreObjective objective = this.getScoreboardObjective();
        if (objective != null) {
            Scoreboard scoreboard = objective.getScoreboard();
            Collection<Score> collection = scoreboard.getSortedScores(objective);
            List<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>()
            {
                @Override
                public boolean apply(@Nullable Score p_apply_1_)
                {
                    return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
                }
            }));

            if (list.size() > 15) {
                collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
            } else {
                collection = list;
            }

            int i = this.mc.fontRenderer.getStringWidth(objective.getDisplayName());
            for (Score score : collection)
            {
                ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
                String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + TextFormatting.RED + score.getScorePoints();
                i = Math.max(i, this.mc.fontRenderer.getStringWidth(s));
            }

            int i1 = collection.size() * this.mc.fontRenderer.FONT_HEIGHT;
            int j1 = this.posY;
            int k1 = 3;
            int l1 = HUDRegistry.screenWidth - i - k1;
            int j = 0;

            for (Score score1 : collection) {

                ++j;
                ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
                String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
                String s2 = TextFormatting.RED + "" + score1.getScorePoints();
                int k = j1 - j * this.mc.fontRenderer.FONT_HEIGHT;
                int l = HUDRegistry.screenWidth - k1 + 2;
                this.posX = l1;

                this.drawRect(l1 - 2, k, l, k + this.mc.fontRenderer.FONT_HEIGHT, 0x50000000);
                this.mc.fontRenderer.drawString(s1, l1, k, 0x20FFFFFF);
                this.mc.fontRenderer.drawString(s2, l - this.mc.fontRenderer.getStringWidth(s2), k, 0x20FFFFFF);

                if (j == collection.size()) {
                    String s3 = objective.getDisplayName();
                    this.drawRect(l1 - 2, k - this.mc.fontRenderer.FONT_HEIGHT - 1, l, k - 1, 0x60000000);
                    this.drawRect(l1 - 2, k - 1, l, k, 0x50000000);
                    this.mc.fontRenderer.drawString(s3, l1 + k / 2 - this.mc.fontRenderer.getStringWidth(s3) / 2, k - this.mc.fontRenderer.FONT_HEIGHT, 0x20FFFFFF);
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
