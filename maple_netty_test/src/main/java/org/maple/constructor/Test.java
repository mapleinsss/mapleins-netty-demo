package org.maple.constructor;

public class Test {
    public static void main(String[] args) {
        Handsome handsome = (Handsome) new MyReflectivePersonFactory<Person>(Handsome.class).newPerson();
        handsome.setAge(13);
        System.out.println(handsome);

        Beauty beauty = (Beauty) new MyReflectivePersonFactory<Person>(Beauty.class).newPerson();
        beauty.setName("美女");
        System.out.println(beauty);
    }
}
