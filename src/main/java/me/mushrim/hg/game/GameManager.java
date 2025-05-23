package me.mushrim.hg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import me.mushrim.hg.HGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.mushrim.hg.utils.ChatUtils;

@Getter
public class GameManager {

    private final HGPlugin plugin;
    private final ScoreboardManager scoreboardManager;
    private final List<UUID> players;
    private final HashMap<UUID, Integer> kills;
    private GameState gameState;
    private Location lobbyLocation;
    private List<Location> spawnPoints;
    private int countdownTime;

    public GameManager(HGPlugin plugin) {
        this.plugin = plugin;
        this.gameState = GameState.WAITING;
        this.scoreboardManager = new ScoreboardManager(plugin); // TODO: Tirar esse instanciamento daq e botar na main
        this.players = new ArrayList<>();
        this.kills = new HashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        // Carregar lobby
        ConfigurationSection lobbySection = config.getConfigurationSection("lobby");
        if (lobbySection != null) {
            lobbyLocation = new Location(
                    Bukkit.getWorld(lobbySection.getString("world")),
                    lobbySection.getDouble("x"),
                    lobbySection.getDouble("y"),
                    lobbySection.getDouble("z"),
                    (float) lobbySection.getDouble("yaw"),
                    (float) lobbySection.getDouble("pitch")
            );
        } else {
            lobbyLocation = Bukkit.getWorlds().getFirst().getSpawnLocation();
            plugin.getLogger().warning("Configuração do lobby não encontrada, usando spawn padrão");
        }

        // Carregar spawn points
        spawnPoints = new ArrayList<>();
        ConfigurationSection spawnsSection = config.getConfigurationSection("spawn-points");
        if (spawnsSection != null) {
            for (String key : spawnsSection.getKeys(false)) {
                ConfigurationSection spawnSection = spawnsSection.getConfigurationSection(key);
                if (spawnSection != null) {
                    Location loc = new Location(
                            Bukkit.getWorld(spawnSection.getString("world")),
                            spawnSection.getDouble("x"),
                            spawnSection.getDouble("y"),
                            spawnSection.getDouble("z")
                    );
                    spawnPoints.add(loc);
                }
            }
        }

        if (spawnPoints.isEmpty()) {
            plugin.getLogger().warning("Nenhum spawn point configurado, usando spawn padrão");
            spawnPoints.add(Bukkit.getWorlds().getFirst().getSpawnLocation());
        }

        // Carregar tempos
        ConfigurationSection gameSettings = config.getConfigurationSection("game-settings");
        if (gameSettings != null) {
            countdownTime = gameSettings.getInt("countdown", 60);
        } else {
            countdownTime = 60;
            plugin.getLogger().warning("Configuração game-settings não encontrada, usando valores padrão");
        }
    }

    public void startGame() {
        if (gameState != GameState.WAITING) {
            Bukkit.broadcastMessage(ChatUtils.color("&cO jogo já está em andamento!"));
            return;
        }

        if (Bukkit.getOnlinePlayers().size() < 2) {
            Bukkit.broadcastMessage(ChatUtils.color("&cNão há jogadores suficientes para começar!"));
            return;
        }

        gameState = GameState.STARTING;
        Bukkit.broadcastMessage(ChatUtils.color("&aO jogo começará em " + countdownTime + " segundos!"));

        // Teleportar jogadores para o lobby
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(lobbyLocation);
            players.add(player.getUniqueId());
            kills.put(player.getUniqueId(), 0);
            scoreboardManager.createScoreboard(player);
        }

        // Iniciar contagem regressiva
        startCountdown();
    }

    private void startCountdown() {
        new Countdown(plugin, this, countdownTime) {
            @Override
            public void onTick(int timeLeft) {
                Bukkit.broadcastMessage(ChatUtils.color("&aO jogo começará em " + timeLeft + " segundos!"));

                // Atualizar scoreboard
                for (UUID uuid : players) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        scoreboardManager.updateScoreboard(player, timeLeft);
                    }
                }
            }

            @Override
            public void onFinish() {
                startHG();
            }
        }.start();
    }

    private void startHG() {
        gameState = GameState.ACTIVE;
        Bukkit.broadcastMessage(ChatUtils.color("&aO jogo começou! Boa sorte!"));

        // Teleportar jogadores para spawns aleatórios
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                Location spawn = spawnPoints.get((int) (Math.random() * spawnPoints.size()));
                player.teleport(spawn);
            }
        }
    }

    public void stopGame() {
        gameState = GameState.ENDED;
        Bukkit.broadcastMessage(ChatUtils.color("&cO jogo terminou!"));

        // Limpar dados
        players.clear();
        kills.clear();

        // Resetar scoreboard
        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboardManager.removeScoreboard(player);
        }

        gameState = GameState.WAITING;
    }

    public void addKill(Player player) {
        kills.put(player.getUniqueId(), kills.getOrDefault(player.getUniqueId(), 0) + 1);

        // Atualizar scoreboard
        scoreboardManager.updateScoreboard(player, -1);
    }

    public void setLobbyLocation(Location location) {
        this.lobbyLocation = location;

        // Salvar no config
        plugin.getConfig().set("lobby.world", location.getWorld().getName());
        plugin.getConfig().set("lobby.x", location.getX());
        plugin.getConfig().set("lobby.y", location.getY());
        plugin.getConfig().set("lobby.z", location.getZ());
        plugin.getConfig().set("lobby.yaw", location.getYaw());
        plugin.getConfig().set("lobby.pitch", location.getPitch());
        plugin.saveConfig();
    }

    public int getCountdownTime() {
        return plugin.getConfig().getInt("game-settings.countdown");
    }

    public void addSpawnPoint(Location location) {
        spawnPoints.add(location);

        // Salvar no config
        int index = spawnPoints.size() - 1;
        plugin.getConfig().set("spawn-points." + index + ".world", location.getWorld().getName());
        plugin.getConfig().set("spawn-points." + index + ".x", location.getX());
        plugin.getConfig().set("spawn-points." + index + ".y", location.getY());
        plugin.getConfig().set("spawn-points." + index + ".z", location.getZ());
        plugin.saveConfig();
    }
}