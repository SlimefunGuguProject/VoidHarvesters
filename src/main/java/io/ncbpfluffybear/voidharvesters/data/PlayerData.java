package io.ncbpfluffybear.voidharvesters.data;

import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerData {

    private final Map<OfflinePlayer, HarvesterData> data;

    public PlayerData() {
        data = new HashMap<>();
    }

    public void addHarvester(OfflinePlayer p, HarvesterType type, Block b) {
        HarvesterData harvesterData = data.get(p);

        if (harvesterData == null) {
            harvesterData = new HarvesterData();
        }

        harvesterData.addBlock(type, b);

        data.put(p, harvesterData);
    }

    public Set<HarvesterType> getHarvesterTypes(OfflinePlayer p) {
        return data.get(p).getTypes();
    }

    public Set<Block> getHarvesters(OfflinePlayer p, HarvesterType type) {
        return data.get(p).getBlocks(type);
    }

    public void removeHarvester(OfflinePlayer p, HarvesterType type, Block b) {
        data.get(p).removeBlock(type, b);
    }

    public Set<Block> getAllHarvesters(Player p) {
        Set<Block> allHarvesters = new HashSet<>();
        for (HarvesterType type : getHarvesterTypes(p)) {
           allHarvesters.addAll(data.get(p).getBlocks(type));
        }

        return allHarvesters;
    }

    public Set<OfflinePlayer> getPlayers() {
        return data.keySet();
    }
}
