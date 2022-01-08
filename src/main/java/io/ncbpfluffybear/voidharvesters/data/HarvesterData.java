package io.ncbpfluffybear.voidharvesters.data;

import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.harvesters.Harvester;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The harvesters and their blocks that a player owns
 */
public class HarvesterData {

    private final Map<HarvesterType, Set<Block>> data;

    protected HarvesterData() {
        data = new HashMap<>();
    }

    protected void addBlock(HarvesterType type, Block b) {
        Set<Block> blocks = data.get(type);

        // Initialize if does not exist
        if (blocks == null) {
            blocks = new HashSet<>();
        }

        blocks.add(b);

        data.put(type, blocks);
    }

    protected void removeBlock(HarvesterType type, Block b) {
        Set<Block> blocks = data.get(type);

        blocks.remove(b);
    }

    protected Set<HarvesterType> getTypes() {
        return data.keySet();
    }

    protected Set<Block> getBlocks(HarvesterType type) {
        return data.get(type);
    }
}
