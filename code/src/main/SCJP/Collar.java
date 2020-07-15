package SCJP;

import java.io.Serializable;

public class Collar implements Serializable {
    private int collarSize;

    public Collar(int size) {
        collarSize = size;
    }

    public int getCollarSize() {
        return collarSize;
    }
}
