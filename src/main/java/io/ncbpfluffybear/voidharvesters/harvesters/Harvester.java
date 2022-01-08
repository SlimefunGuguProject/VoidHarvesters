package io.ncbpfluffybear.voidharvesters.harvesters;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.ncbpfluffybear.voidharvesters.VoidHarvesters;
import io.ncbpfluffybear.voidharvesters.enums.HarvesterType;
import io.ncbpfluffybear.voidharvesters.upgrades.Upgrade;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public abstract class Harvester extends SlimefunItem implements EnergyNetComponent, RecipeDisplayItem {

    // Delay in seconds
    private static final int DELAY = 5;

    private static final int ENERGY_CONSUMPTION = 128;
    private static final int CAPACITY = ENERGY_CONSUMPTION * 3;

    // Amount consumed per operation
    private static final double FUEL_CONSUMPTION = 0.1;

    private final int STATUS_SLOT = 4;

    private final int[] BORDER = new int[]{
            3, 5, 6, 7, 8,
            12,
            21,
            30,
            39,
            48,
    };

    private final int[] OUTPUT = new int[]{
            23, 24, 25,
            32, 33, 34,
            41, 42, 43
    };

    private final int[] OUTPUT_BORDER = new int[]{
            13, 14, 15, 16, 17,
            22, 26,
            31, 35,
            40, 44,
            49, 50, 51, 52, 53
    };

    /* TODO: Find a use for the upgrade slot or get rid of it
        - Current plan: Replace with a display of applied Upgrades and add a button to eject them
     */
    private final int UPGRADE_SLOT = 10;
    private final int UPGRADE_LABEL = 1;
    private final int[] UPGRADE_BORDER = new int[]{0, 2, 9, 11, 18, 19, 20};

    private final int FUEL_INPUT = 37;
    private final int FUEL_LABEL = 28;
    private final int[] FUEL_BORDER = new int[]{27, 29, 36, 38, 45, 46, 47};

    public Harvester(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        // Add placement handler
        addItemHandler(onPlace(), onBreak());

        // Build menu
        new BlockMenuPreset(item.getItemId(), item.getDisplayName()) {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(@Nonnull Block b, @Nonnull Player p) {
                if (Slimefun.getProtectionManager().hasPermission(p, b, Interaction.INTERACT_BLOCK)) {

                    // Update menu
                    setStatus(BlockStorage.getInventory(b), getCharge(b.getLocation()) >= getEnergyConsumption(),
                            Double.parseDouble(BlockStorage.getLocationInfo(b.getLocation(), "fuel")), false
                    );
                    return true;
                }

                return false;
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
                if (flow == ItemTransportFlow.WITHDRAW) {
                    return OUTPUT;
                } else if (flow == ItemTransportFlow.INSERT) {
                    return new int[]{FUEL_INPUT};
                }

                return null;
            }
        };
    }

    public static long getDelay() {
        return DELAY;
    }

    private void constructMenu(BlockMenuPreset preset) {
        for (int slot : BORDER) {
            preset.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        preset.addItem(STATUS_SLOT, new CustomItemStack(Material.RED_STAINED_GLASS_PANE,
                "&bStatus", "&eWaiting for input..."));
        preset.addMenuClickHandler(STATUS_SLOT, ChestMenuUtils.getEmptyClickHandler());

        for (int slot : FUEL_BORDER) {
            preset.addItem(slot, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        preset.addItem(FUEL_LABEL, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, "&bFuel Input",
                "&7Place fuel in the slot below"
        ), ChestMenuUtils.getEmptyClickHandler());

        for (int slot: UPGRADE_BORDER) {
            preset.addItem(slot, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE),
                    ChestMenuUtils.getEmptyClickHandler()
            );
        }

        preset.addItem(UPGRADE_LABEL, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, "&eUpgrade Slot",
                "&7Place upgrade in the slot below"
        ), ChestMenuUtils.getEmptyClickHandler());

        for (int slot: OUTPUT_BORDER) {
            preset.addItem(slot, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler()
            );
        }

        for (int slot : FUEL_BORDER) {
            preset.addItem(slot, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }
    }

    /**
     * Placement handler to do stuff when any harvester is placed
     */
    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                Block b = e.getBlock();
                VoidHarvesters.getPlayerData().addHarvester(e.getPlayer(), getHarvesterType(), b);

                BlockStorage.addBlockInfo(b, "level", "1");
                BlockStorage.addBlockInfo(b, "fuel", "0.00");
            }
        };
    }

    private BlockBreakHandler onBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent e, @Nonnull ItemStack item, @Nonnull List<ItemStack> drops) {

                Block b = e.getBlock();
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), FUEL_INPUT);
                    inv.dropItems(b.getLocation(), OUTPUT);
                }

                VoidHarvesters.getPlayerData().removeHarvester(e.getPlayer(), getHarvesterType(), e.getBlock());
            }
        };
    }

    /**
     * Occurs every {@value DELAY} seconds
     */
    public void tick(Block b) {
        if (b == null) {
            return;
        }

        // Should always be false because skulls are directional, but just in case
        if (!(b.getBlockData() instanceof Directional)) {
            return;
        }

        // Get direction harvester is facing
        BlockFace facing = ((Directional) b.getBlockData()).getFacing();

        // Prevent use on Slimefun Blocks
        if (BlockStorage.hasBlockInfo(b.getRelative(facing))) {
            return;
        }

        BlockMenu menu = BlockStorage.getInventory(b);
        double fuel = Double.parseDouble(BlockStorage.getLocationInfo(b.getLocation(), "fuel"));

        // Insert new fuel
        ItemStack item = menu.getItemInSlot(FUEL_INPUT);
        if (item != null && VoidHarvesters.getFuelSources().containsKey(item.getType())) {
            fuel += VoidHarvesters.getFuelSources().get(item.getType());
            setFuel(b, fuel);
            menu.consumeItem(FUEL_INPUT, 1);
        }

        // Stop if no power or fuel
        if (getCharge(b.getLocation()) < getEnergyConsumption() && fuel == 0) {
            setStatus(menu, false, fuel, true);
            return;
        }

        // Get harvester level
        int level = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "level"));

        // Has power
        if (getCharge(b.getLocation()) >= getEnergyConsumption()) {
            removeCharge(b.getLocation(), getEnergyConsumption());
            setStatus(menu, true, fuel, true);
            run(b, menu, facing, getUpgrades(b));

            // Use fuel as fallback power source
        } else if (fuel >= getFuelConsumption()) {
            // Round down because of calculation errors
            fuel = Math.floor((fuel - getFuelConsumption()) * 100) / 100;
            setFuel(b, fuel);
            setStatus(menu, false, fuel, true);
            run(b, menu, facing, getUpgrades(b));

        }

    }

    /**
     * Override this method to add specific harvester functionalities
     */
    protected void run(Block b, BlockMenu menu, BlockFace facing, Map<Upgrade.UpgradeType, Integer> upgrades) {

    }

    /**
     * Override the method to define which type of harvester this is
     */
    public HarvesterType getHarvesterType() {
        return null;
    }

    /**
     * Sets the amount of fuel in a Harvester
     */
    private void setFuel(Block b, double amount) {
        BlockStorage.addBlockInfo(b.getLocation(), "fuel", String.valueOf(amount));
    }

    /**
     * Sets the menu status of the Harvester
     */
    private void setStatus(BlockMenu menu, boolean energy, double fuel, boolean requireViewer) {

        if (requireViewer && !menu.hasViewer()) {
            return;
        }

        // Defaults
        String energyStatus = "&cOut of power";
        String fuelStatus = "&cFuel: 0.0";
        String currentStatus = "&cCurrently not running";
        Material statusMaterial = Material.LIME_STAINED_GLASS_PANE;

        if (energy) {
            energyStatus = "&aRunning on power";
            currentStatus = "&aCurrently using: &e" + getEnergyConsumption() / getDelay() + "J/s";
        } else if (fuel > getFuelConsumption()) {
            // Swap current status to fuel
            currentStatus = "&aCurrently using: &e" + getFuelConsumption() / getDelay() + "F/s";
        }

        if (fuel > 0) {
            fuelStatus = "&aFuel: " + fuel;
        }


        if (!energy && fuel < getFuelConsumption()) {
            statusMaterial = Material.RED_STAINED_GLASS_PANE;
        }

        menu.replaceExistingItem(STATUS_SLOT, new CustomItemStack(statusMaterial,
                "&bStatus",
                currentStatus,
                "",
                energyStatus,
                fuelStatus
        ));
    }

    /**
     * Adds an item to Harvester inventory or drops at harvester location if full
     *
     * @param menu The menu of the Harvester
     * @param item The item to add to the menu
     * @param l    The location to drop excess items
     */
    protected void addItem(BlockMenu menu, ItemStack item, Location l) {

        if (menu.fits(item, OUTPUT)) {
            menu.pushItem(item, OUTPUT);
        } else {
            l.getWorld().dropItemNaturally(l.add(0, 1, 0), item);
        }
    }

    /**
     * Check if block is a Slimefun block
     *
     * @param block block to check
     * @return if block is a Slimefun block
     */
    protected boolean isSFBlock(Block block) {
        return BlockStorage.checkID(block) != null;
    }

    /**
     * Adds a level to the Harvester
     */
    public void levelUp(Block b) {
        int currLevel = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "level"));

        BlockStorage.addBlockInfo(b, "level", String.valueOf(currLevel + 1));
    }

    /**
     * Gets the levels of all the upgrades on a Harvester
     */
    private Map<Upgrade.UpgradeType, Integer> getUpgrades(Block b) {
        Map<Upgrade.UpgradeType, Integer> upgrades = new HashMap<>();

        for (Upgrade.UpgradeType upgrade : Upgrade.UpgradeType.values()) {
            upgrades.put(upgrade, Upgrade.getLevel(b, upgrade.name().toLowerCase()));
        }

        return upgrades;
    }

    @Nonnull
    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }

    // Per second
    public double getFuelConsumption() {
        return FUEL_CONSUMPTION;
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> recipes = new ArrayList<>();

        VoidHarvesters.getFuelSources().forEach((mat, pts) -> recipes.add(new CustomItemStack(mat, null, "&7Adds " + pts + " fuel points",
                "&7Lasts " + pts / getFuelConsumption() + " seconds"
            )));

        return recipes;
    }
}
