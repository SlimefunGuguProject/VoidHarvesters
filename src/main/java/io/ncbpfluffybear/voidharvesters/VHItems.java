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
            new CustomItemStack(Material.CRYING_OBSIDIAN, "&5虚空挖掘机")
    );

    public static final SlimefunItemStack MINER_HARVESTER = new SlimefunItemStack(
            "MINER_HARVESTER",
            Material.DISPENSER,
            "&6挖掘机",
            "&7挖掘可破坏方块"
    );

    public static final SlimefunItemStack WOODCUTTER_HARVESTER = new SlimefunItemStack(
            "WOODCUTTER_HARVESTER",
            Material.DISPENSER,
            "&6伐木机",
            "&7用于快速伐木"
    );
    public static final SlimefunItemStack FARMER_HARVESTER = new SlimefunItemStack(
            "FARMER_HARVESTER",
            Material.DISPENSER,
            "&6收割机",
            "&7用于收割农作物"
    );

}
