package io.ncbpfluffybear.voidharvesters.harvesters;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.upgrades.Upgrade;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WoodcutterHarvester extends Harvester {

    public WoodcutterHarvester(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void run(Block b, BlockMenu menu, BlockFace facing, Map<Upgrade.UpgradeType, Integer> upgrades) {
        for (int i = 0; i <= upgrades.get(Upgrade.UpgradeType.RANGE); i++) {
            Block toCheck = b.getRelative(facing, i + 1);
            if (!isSFBlock(toCheck) && validateBlock(toCheck)) {
                for (ItemStack item : toCheck.getDrops()) {
                    addItem(menu, item, b.getLocation());
                }

                toCheck.setType(Material.AIR);
            }
        }
    }

    private boolean validateBlock(Block block) {
        Material mat = block.getType();

        return SlimefunTag.LOGS.isTagged(mat);
    }

    @Override
    public HarvesterType getHarvesterType() {
        return HarvesterType.WOODCUTTER;
    }
}
