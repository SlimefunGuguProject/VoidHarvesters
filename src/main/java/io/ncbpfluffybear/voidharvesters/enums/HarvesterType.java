package io.ncbpfluffybear.voidharvesters.enums;

import io.ncbpfluffybear.voidharvesters.harvesters.Harvester;

public enum HarvesterType {

    MINER(),
    FARMER(),
    WOODCUTTER();

    private Harvester instance;

    public Harvester getInstance() {
        return instance;
    }

    public void setInstance(Harvester instance) {
        this.instance = instance;
    }
}
