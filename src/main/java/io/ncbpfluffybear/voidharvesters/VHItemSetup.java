package io.ncbpfluffybear.voidharvesters;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.harvesters.Harvester;
import io.ncbpfluffybear.voidharvesters.harvesters.MinerHarvester;
import io.ncbpfluffybear.voidharvesters.harvesters.WoodcutterHarvester;
import io.ncbpfluffybear.voidharvesters.harvesters.FarmerHarvester;
import io.ncbpfluffybear.voidharvesters.upgrades.Upgrade;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public final class VHItemSetup {

    // We need to collect instances for the ticker
    public static MinerHarvester minerHarvesterInstance;
    public static WoodcutterHarvester woodcutterHarvesterInstance;
    public static FarmerHarvester farmerHarvesterInstance;

    private VHItemSetup() {
    }

    public static void setup(VoidHarvesters plugin) {
        minerHarvesterInstance =
                new MinerHarvester(VHItems.category, VHItems.MINER_HARVESTER, RecipeType.NULL, new ItemStack[0]);
        minerHarvesterInstance.register(plugin);
        HarvesterType.MINER.setInstance(minerHarvesterInstance);

        woodcutterHarvesterInstance =
                new WoodcutterHarvester(VHItems.category, VHItems.WOODCUTTER_HARVESTER, RecipeType.NULL, new ItemStack[0]);
        woodcutterHarvesterInstance.register(plugin);
        HarvesterType.WOODCUTTER.setInstance(woodcutterHarvesterInstance);

        farmerHarvesterInstance = new FarmerHarvester(VHItems.category, VHItems.FARMER_HARVESTER, RecipeType.NULL, new ItemStack[0]);
        farmerHarvesterInstance.register(plugin);
        HarvesterType.FARMER.setInstance(farmerHarvesterInstance);

        // Do not add items after this line
        for (Upgrade.UpgradeType upgrade : Upgrade.UpgradeType.values()) {

            ArrayList<String> lore = upgrade.getLore();

            lore.add("&7满级: " + upgrade.getMaxLvl());
            lore.add("&7可插入:");
            for (HarvesterType harvesterType : upgrade.getAcceptableHarvesters()) {
                lore.add("  &7- " + WordUtils.capitalize(harvesterType.name().toLowerCase()));
            }

            String[] loreArray = Arrays.copyOf(lore.toArray(), lore.size(), String[].class);

            SlimefunItemStack UPGRADE_ITEM = new SlimefunItemStack(
                    upgrade.name() + "_UPGRADE",
                    Material.BOOK,
                    "&b" + WordUtils.capitalize(upgrade.getName()) + "升级模块",
                    loreArray
            );

            new Upgrade(VHItems.category, UPGRADE_ITEM, RecipeType.NULL, upgrade.getRecipe(), upgrade).register(plugin);
        }
    }

}
