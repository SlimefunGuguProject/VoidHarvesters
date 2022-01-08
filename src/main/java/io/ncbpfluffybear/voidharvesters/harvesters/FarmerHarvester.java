package io.ncbpfluffybear.voidharvesters.harvesters;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.upgrades.Upgrade;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


/**
 * Equivalent of an android miner
 */
public class FarmerHarvester extends Harvester implements Listener {

    public FarmerHarvester(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    /**
     * This method is triggered by the {@link io.ncbpfluffybear.voidharvesters.tasks.HarvesterTask} runnable
     */
    @Override
    public void run(Block b, BlockMenu menu, BlockFace facing, Map<Upgrade.UpgradeType, Integer> upgrades) {

        for (int x = -upgrades.get(Upgrade.UpgradeType.RANGE); x < upgrades.get(Upgrade.UpgradeType.RANGE); x++) {
            for (int y = -upgrades.get(Upgrade.UpgradeType.RANGE); y < upgrades.get(Upgrade.UpgradeType.RANGE); y++) {
                Block toCheck = b.getRelative(x, 0, y);
                if(isSFBlock(toCheck)) {
                    continue;
                }

                if (validateBlock(toCheck)) {
                    if(b.getBlockData() instanceof Ageable) {
                        Ageable ageable = (Ageable) b.getBlockData();
                        if(ageable.getAge() == ageable.getMaximumAge()) {
                            for (ItemStack item : toCheck.getDrops()) {
                                addItem(menu, item, b.getLocation());
                            }
                            ageable.setAge(CropState.SEEDED.ordinal());
                            b.setBlockData(ageable);
                        }
                    }

                    toCheck.setType(Material.AIR);
                }
            }
        }
    }

    @Override
    public HarvesterType getHarvesterType() {
        return HarvesterType.FARMER;
    }

    private boolean validateBlock(Block b) {
        Material mat = b.getType();

        return SlimefunTag.STONE_VARIANTS.isTagged(mat) || SlimefunTag.ORES.isTagged(mat)
                || SlimefunTag.DEEPSLATE_ORES.isTagged(mat) || SlimefunTag.NETHER_ORES.isTagged(mat)
                || mat == Material.COBBLESTONE;
    }

}
