package io.ncbpfluffybear.voidharvesters.tasks;

import io.ncbpfluffybear.voidharvesters.data.HarvesterData;
import io.ncbpfluffybear.voidharvesters.data.PlayerData;
import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.VoidHarvesters;
import io.ncbpfluffybear.voidharvesters.harvesters.Harvester;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import java.util.logging.Level;

// TODO: We need to do strenuous testing to make sure this doesn't lag too hard
public class HarvesterTask implements Runnable {

    @Override
    public void run() {

        PlayerData data = VoidHarvesters.getPlayerData();

        for (OfflinePlayer p : data.getPlayers()) {
            typeLoop:
            for (HarvesterType type : data.getHarvesterTypes(p)) {
                for (Block b : data.getHarvesters(p, type)) {
                    if (type.getInstance() == null) {
                        VoidHarvesters.getInstance().getServer().getLogger().log(Level.SEVERE, type.name() + " has no linked instance!");
                        continue typeLoop;
                    }

                    // null check
                    if (!(BlockStorage.check(b) instanceof Harvester)) {
                        data.removeHarvester(p, type, b);
                        continue;
                    }

                    type.getInstance().tick(b);
                }
            }
        }
    }
}
