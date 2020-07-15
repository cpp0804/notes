package SCJP;

import java.io.*;

public class IO {
    public static void main(String[] args) {
        extendSerializeTest();

    }

    public static void fileTest() {
        try { // warning: exceptions possible
            boolean newFile = false;
            File file = new File("fileWrite1.txt");
            System.out.println(file.exists());
            newFile = file.createNewFile();
            System.out.println(newFile);
            System.out.println(file.exists());
        } catch (IOException e) {
        }
    }

    public static void FileReaderWriterTest() {
        // to store input
        char[] in = new char[50];
        int size = 0;
        try {
            // just an object
            File file = new File("fileWrite2.txt");

            // create an actual file & a FileWriter obj
            FileWriter fw = new FileWriter(file);

            // write characters to the file
            fw.write("howdy\nfolks\n");

            //flush before closing
            fw.flush();
            // close file when done
            fw.close();

            // create a FileReader Object
            FileReader fr = new FileReader(file);

            // read the whole file!
            size = fr.read(in);
            System.out.print(size + " ");
            for (char c : in)
                System.out.print(c);
        } catch (IOException e) {
        }
    }

    public static void PrintWriterTest() {
        File file = new File("fileWrite2.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = null;
        pw = new PrintWriter(fw);

//        try {
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        pw.println("aaa");
        pw.println("bbb");

        pw.flush();
        pw.close();

        FileReader fr = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(fr);
        try {
            String data = br.readLine();
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void serializeTest() {
        Cat c = new Cat(); // 2
        try {
            FileOutputStream fs = new FileOutputStream("testSer.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(c); // 3
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fis = new FileInputStream("testSer.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            c = (Cat) ois.readObject(); // 4
            ois.close();
            System.out.println(c.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void serializeTest2() {
        Collar c = new Collar(3);
        Dog d = new Dog(c, 5);
        System.out.println("before: collar size is " + d.getCollar().getCollarSize());
        try {
            FileOutputStream fs = new FileOutputStream("testSer.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(d);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fis = new FileInputStream("testSer.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            d = (Dog) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("after: collar size is " + d.getCollar().getCollarSize());
    }

    public static void extendSerializeTest() {
        Bird d = new Bird(35, "Fido");
        System.out.println("before: " + d.name + " " + d.weight);

        try {
            FileOutputStream fs = new FileOutputStream("testSer.ser");
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(d);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fis = new FileInputStream("testSer.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            d = (Bird) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("after: " + d.name + " " + d.weight);
    }

}
