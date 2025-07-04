package sec02;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class TreeSetDemo {
    public static void main(String[] args) {
        Set<String> set = Set.of("포도", "수박", "사과", "키위", "망고");
        HashSet<String> hashset = new HashSet<>(set);
        System.out.println(hashset);
        TreeSet<String> fruits = new TreeSet<>(set);
        System.out.println(fruits);

        System.out.println(fruits.first());
        System.out.println(fruits.last());
        System.out.println(fruits.lower("사과"));
        System.out.println(fruits.higher("사과"));
    }
}
