package io.ncbpfluffybear.voidharvesters;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.ncbpfluffybear.voidharvesters.data.PlayerData;
import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.harvesters.Harvester;
import io.ncbpfluffybear.voidharvesters.tasks.HarvesterTask;
import io.ncbpfluffybear.voidharvesters.tasks.InitializeTask;
import io.ncbpfluffybear.voidharvesters.tasks.SaveTask;
import net.guizhanss.guizhanlib.updater.GuizhanBuildsUpdater;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Level;

@SuppressWarnings("ConstantConditions")
public class VoidHarvesters extends JavaPlugin implements SlimefunAddon {

    private static VoidHarvesters instance;
    private static final PlayerData harvesters = new PlayerData();
    private static HashMap<Material, Double> fuelSources = new HashMap<>();
    public static Config harvestersConfig;
    public static Config fuelSourcesConfig;
    private BukkitTask harvesterTask;
    private BukkitTask saveTask;

    @Override
    public void onEnable() {
        instance = this;

        if(!getInstance().getDataFolder().exists()) {
            getInstance().getDataFolder().mkdir();
        }

        // Generate files
        final File harvestersFile = new File(getInstance().getDataFolder(), "harvesters.yml");
        try {
            //noinspection ResultOfMethodCallIgnored
            harvestersFile.createNewFile();
        } catch (IOException e) {
            getInstance().getLogger().log(Level.SEVERE, "创建挖掘机文件时出现错误.", e);
        }

        final File fuelSourcesFile = new File(getInstance().getDataFolder(), "fuel-sources.yml");
        if (!fuelSourcesFile.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/fuel-sources.yml"), fuelSourcesFile.toPath());
            } catch (IOException e) {
                getInstance().getLogger().log(Level.SEVERE, "无法创建默认的 fuel-sources.yml", e);
            }
        }


        // Read something from your config.yml
        Config cfg = new Config(this);
        harvestersConfig = new Config(this, "harvesters.yml");
        fuelSourcesConfig = new Config(this, "fuel-sources.yml");

        if (cfg.getBoolean("options.auto-update")) {
            new GuizhanBuildsUpdater(this, getFile(), "SlimefunGuguProject", "VoidHarvesters", "main", false).start();
        }

        VoidHarvesterCommand vhc = new VoidHarvesterCommand();
        getCommand("voidharvesters").setExecutor(vhc);
        getCommand("voidharvesters").setTabCompleter(vhc);
        VHItemSetup.setup(this);

        // Collect all fuel values from fuel-sources.yml
        fuelSources = getFuelSources(fuelSourcesConfig);

        // TODO: Make delays changeable via config options
        // Add all harvesters, put in runnable so it starts after server starts
        getServer().getScheduler().runTask(this, new InitializeTask());

        // Start harvester ticker
        harvesterTask = getServer().getScheduler().runTaskTimer(this, new HarvesterTask(),
                200L, Harvester.getDelay() * 20);

        // Save all harvesters every 10 minutes (Same as Slimefun default save delay)
        saveTask = getServer().getScheduler().runTaskTimer(this, new SaveTask(),
                12000L, 12000L);
    }

    @Override
    public void onDisable() {
        // Stop harvester ticker
        harvesterTask.cancel();
        saveTask.cancel();

        saveHarvesters();

    }

    @Override
    public String getBugTrackerURL() {
        // You can return a link to your Bug Tracker instead of null here
        return null;
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        /*
         * You will need to return a reference to your Plugin here.
         * If you are using your main class for this, simply return "this".
         */
        return this;
    }

    public static VoidHarvesters getInstance() {
        return instance;
    }

    private HashMap<Material, Double> getFuelSources(Config cfg) {
        HashMap<Material, Double> fuelSources = new HashMap<>();

        for (String key : cfg.getKeys()) {
            fuelSources.put(Material.getMaterial(key), cfg.getDouble(key));
        }

        return fuelSources;
    }

    public static PlayerData getPlayerData() {
        return harvesters;
    }

    public static Config getHarvestersConfig() {
        return harvestersConfig;
    }

    public static HashMap<Material, Double> getFuelSources() {
        return fuelSources;
    }

    public static void saveHarvesters() {
        harvestersConfig.clear();

        int savedHarvesters = 0;
        // Iterate through all players
        for (OfflinePlayer p : getPlayerData().getPlayers()) {
            // Iterate through all of their harvester types
            for (HarvesterType type : getPlayerData().getHarvesterTypes(p)) {
                // Iterate through the harvester blocks
                int i = 1;
                for (Block b : getPlayerData().getHarvesters(p, type)) {
                    // Add new harvester
                    harvestersConfig.setValue(p.getUniqueId() + "." + type.name() + "." + i, b.getLocation());
                    i++;
                    savedHarvesters++;
                }
            }
        }

        getInstance().getLogger().log(Level.INFO, MessageFormat.format("已保存 {0} 个挖掘机", savedHarvesters));
        harvestersConfig.save();
    }

}
