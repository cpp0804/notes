package SCJP;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Dog implements Serializable {

    private Collar theCollar;
    private int dogSize;

    public Dog(Collar collar, int size) {
        theCollar = collar;
        dogSize = size;
    }

    public Collar getCollar() {
        return theCollar;
    }


    private void writeObject(ObjectOutputStream os) { // throws IOException {
        try {
            os.defaultWriteObject();
            os.writeInt(theCollar.getCollarSize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream is) {
        try {
            is.defaultReadObject();
            theCollar = new Collar(is.readInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
