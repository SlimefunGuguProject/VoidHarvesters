package io.ncbpfluffybear.voidharvesters.tasks;

import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.VoidHarvesters;
import io.ncbpfluffybear.voidharvesters.harvesters.Harvester;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class InitializeTask implements Runnable {
    @Override
    public void run() {
        Config harvestersConfig = VoidHarvesters.getHarvestersConfig();

        // Get harvesters from config
        for (String playerKey : harvestersConfig.getKeys()) {

            for (String typeKey : harvestersConfig.getKeys(playerKey)) {

                HarvesterType type = HarvesterType.valueOf(typeKey);

                for (String blockKey : harvestersConfig.getKeys(playerKey + "." + typeKey)) {
                    Location l = harvestersConfig.getLocation(playerKey + "." + typeKey + "." + blockKey);

                    if (BlockStorage.check(l) instanceof Harvester) {
                        VoidHarvesters.getPlayerData().addHarvester(
                                Bukkit.getOfflinePlayer(UUID.fromString(playerKey)), type, l.getBlock()
                        );
                    }
                }
            }
        }
    }
}
