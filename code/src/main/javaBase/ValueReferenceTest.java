package javaBase;

/**
 * 测试值传递和引用传递
 */
public class ValueReferenceTest {
    public static void main(String[] args) {
//
//        int a=25;
//        float w=77.5f;
//        basicTest(a,w);
//        System.out.println("方法执行后的age："+a);
//        System.out.println("方法执行后的weight："+w);

        Person p= new Person();
        p.setName("我是pp");
        p.setAge(45);
        referenceTest(p);
        System.out.println("方法执行后的name："+p.getName());

    }

    public static void stringTest(String var) {
        var = var + "after";
        System.out.println(var);
    }

    public static void referenceTest(Person person) {
        System.out.println("传入的person的name：" + person.getName());
        person = new Person();
        person.setName("我是张小龙");
        System.out.println("方法内重新赋值后的name：" + person.getName());
    }

    public static void basicTest(int age, float weight) {
        System.out.println("传入的age：" + age);
        System.out.println("传入的weight：" + weight);
        age = 33;
        weight = 89.5f;
        System.out.println("方法内重新赋值后的age：" + age);
        System.out.println("方法内重新赋值后的weight：" + weight);
    }

    public static class Person {
        private String name;
        private int age;

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

}
