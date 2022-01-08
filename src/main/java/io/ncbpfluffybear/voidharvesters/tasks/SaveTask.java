package io.ncbpfluffybear.voidharvesters.tasks;

import io.ncbpfluffybear.voidharvesters.VoidHarvesters;

public class SaveTask implements Runnable {
    @Override
    public void run() {
        VoidHarvesters.saveHarvesters();
    }
}
