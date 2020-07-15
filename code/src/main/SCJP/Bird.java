package SCJP;

import java.io.Serializable;

public class Bird extends Animal implements Serializable {
    String name;

    Bird(int w, String n) {
        weight = w;
        name = n;
    }
}
