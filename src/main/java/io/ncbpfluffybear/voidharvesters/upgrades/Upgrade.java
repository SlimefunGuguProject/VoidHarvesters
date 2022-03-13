package io.ncbpfluffybear.voidharvesters.upgrades;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.Utils;
import io.ncbpfluffybear.voidharvesters.harvesters.Harvester;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Upgrade extends SimpleSlimefunItem<ItemUseHandler> {

    private final UpgradeType upgrade;
    private final String key;

    public Upgrade(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, UpgradeType upgrade) {
        super(itemGroup, item, recipeType, recipe);

        this.upgrade = upgrade;
        this.key = upgrade.getName();
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Optional<Block> optB = e.getClickedBlock();

            if (!optB.isPresent()) {
                return;
            }

            Block b = optB.get();
            SlimefunItem sfItem = BlockStorage.check(b);

            if (!(sfItem instanceof Harvester)) {
                return;
            }

            if (!canUpgrade(e.getPlayer(), (Harvester) sfItem, b, upgrade)) {
                return;
            }

            applyUpgrade(b);

            // Consume 1 upgrade item
            ItemStack upgrade = e.getItem();
            upgrade.setAmount(upgrade.getAmount() - 1);
        };
    }

    private boolean canUpgrade(Player p, Harvester h, Block b, UpgradeType upgrade) {
        // Check if it can be applied to harvester type
        if (!upgrade.getAcceptableHarvesters().contains(h.getHarvesterType())) {
            Utils.send(p, "&c此升级模块不能插入此挖掘机中!");
            return false;
        }

        // Check level limit
        int level = getLevel(b, key);
        if (level >= upgrade.getMaxLvl()) {
            Utils.send(p, "&cYou have reached the max level for this upgrade");
            return false;
        }

        return true;
    }

    private void applyUpgrade(@Nonnull Block b) {
        int level = getLevel(b, key);
        BlockStorage.addBlockInfo(b, key, String.valueOf(++level));
    }

    public static int getLevel(Block b, String upgrade) {
        String levelStr = BlockStorage.getLocationInfo(b.getLocation(), upgrade);
        int level = 0;

        if (levelStr != null) {
            level = Integer.parseInt(levelStr);
        }

        return level;
    }

    /**
     * All upgrades are defined here, the logic of the upgrade is defined in the respective harvester
     */
    public enum UpgradeType {
        RANGE(64, new HashSet<>(Collections.singleton(HarvesterType.MINER)), new ItemStack[]{
                SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack(Material.NETHERITE_PICKAXE), SlimefunItems.REINFORCED_ALLOY_INGOT,
                SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.REINFORCED_ALLOY_INGOT,
                SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.REINFORCED_ALLOY_INGOT
        }, "&7增加挖掘机的范围");
        
        RANGE(64, new HashSet<>(Collections.singleton(HarvesterType.WOODCUTTER)), new ItemStack[]{
                SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack(Material.NETHERITE_AXE), SlimefunItems.REINFORCED_ALLOY_INGOT,
                SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.REINFORCED_ALLOY_INGOT,
                SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.REINFORCED_ALLOY_INGOT
        }, "&7增加伐木机的范围");

        RANGE(64, new HashSet<>(Collections.singleton(HarvesterType.FARMER)), new ItemStack[]{
                SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack(Material.NETHERITE_HOE), SlimefunItems.REINFORCED_ALLOY_INGOT,
                SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.REINFORCED_ALLOY_INGOT,
                SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.REINFORCED_ALLOY_INGOT
        }, "&7增加收割机的范围");
            
        private final int maxLvl;
        private final Set<HarvesterType> acceptableHarvesters;
        private final ItemStack[] recipe;
        private final String[] lore;

        UpgradeType(int maxLvl, Set<HarvesterType> acceptableHarvesters, ItemStack[] recipe, String... lore) {
            this.maxLvl = maxLvl;
            this.acceptableHarvesters = acceptableHarvesters;
            this.recipe = recipe;
            this.lore = lore;
        }

        public String getName() {
            return this.name().toLowerCase();
        }

        public ItemStack[] getRecipe() {
            return recipe;
        }

        public ArrayList<String> getLore() {
            ArrayList<String> loreList = new ArrayList<>();
            Collections.addAll(loreList, this.lore);

            return loreList;
        }

        public int getMaxLvl() {
            return maxLvl;
        }

        public Set<HarvesterType> getAcceptableHarvesters() {
            return acceptableHarvesters;
        }
    }
}
