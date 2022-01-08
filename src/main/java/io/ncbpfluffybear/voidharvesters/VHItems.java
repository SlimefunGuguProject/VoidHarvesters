package io.ncbpfluffybear.voidharvesters;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public final class VHItems {

    private VHItems() {
    }


    // ItemGroup
    public static final ItemGroup category = new ItemGroup(new NamespacedKey(VoidHarvesters.getInstance(),
            "voidharvesters"),
            new CustomItemStack(Material.CRYING_OBSIDIAN, "&5Void Harvesters")
    );

    public static final SlimefunItemStack MINER_HARVESTER = new SlimefunItemStack(
            "MINER_HARVESTER",
            Material.DISPENSER,
            "&6Miner Harvester",
            "&7Breaks mineable blocks"
    );

    public static final SlimefunItemStack WOODCUTTER_HARVESTER = new SlimefunItemStack(
            "WOODCUTTER_HARVESTER",
            Material.DISPENSER,
            "&6Woodcutter Harvester",
            "&7Breaks wood"
    );
    public static final SlimefunItemStack FARMER_HARVESTER = new SlimefunItemStack(
            "FARMER_HARVESTER",
            Material.DISPENSER,
            "&6Farmer Harvester",
            "&7Harvests Crops"
    );

}
