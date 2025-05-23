package me.mushrim.hg.game;

import java.util.HashMap;
import java.util.UUID;

import me.mushrim.hg.HGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.mushrim.hg.utils.ChatUtils;

public class ScoreboardManager {

    private final GameManager gameManager;
    private final HashMap<UUID, Scoreboard> scoreboards;

    public ScoreboardManager(HGPlugin plugin) {
        this.gameManager = plugin.getGameManager();
        this.scoreboards = new HashMap<>();
    }

    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("hgscore", "dummy");
        objective.setDisplayName(ChatUtils.color("&6&lMushrimHG"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateScoreboard(player, gameManager.getGameState() == GameState.STARTING ?
                gameManager.getCountdownTime() : -1);

        Team team = scoreboard.registerNewTeam("players");
        team.setPrefix(ChatUtils.color("&7"));
        team.addEntry(player.getName());

        player.setScoreboard(scoreboard);
        scoreboards.put(player.getUniqueId(), scoreboard);
    }

    public void updateScoreboard(Player player, int timeLeft) {
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) return;

        Objective objective = scoreboard.getObjective("hgscore");
        if (objective == null) return;

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        objective.setDisplayName(ChatUtils.color("&6&lMushrimHG"));

        Score stateScore = objective.getScore(ChatUtils.color("&fEstado: &a" + getGameStateText()));
        stateScore.setScore(10);

        if (timeLeft > 0) {
            Score timeScore = objective.getScore(ChatUtils.color("&fIniciando em: &a" + timeLeft + "s"));
            timeScore.setScore(9);
        }

        Score playersScore = objective.getScore(ChatUtils.color("&fJogadores: &a" + gameManager.getPlayers().size()));
        playersScore.setScore(8);

        Score killsScore = objective.getScore(ChatUtils.color("&fAbates: &a" +
                gameManager.getKills().getOrDefault(player.getUniqueId(), 0)));
        killsScore.setScore(7);

        Score blankScore = objective.getScore(ChatUtils.color("&f"));
        blankScore.setScore(6);

        Score ipScore = objective.getScore(ChatUtils.color("&emushrim.shop"));
        ipScore.setScore(5);
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        scoreboards.remove(player.getUniqueId());
    }

    private String getGameStateText() {
        switch (gameManager.getGameState()) {
            case WAITING: return "Aguardando";
            case STARTING: return "Iniciando";
            case ACTIVE: return "Em jogo";
            case ENDED: return "Terminado";
            default: return "Desconhecido";
        }
    }
}