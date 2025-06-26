package ch03;

import java.util.Scanner;

public class ex0351 {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        double num1, num2;
        System.out.print("첫번째 수 : ");
        num1 = Double.parseDouble(scanner.nextLine());

        System.out.print("두번째 수 : ");
        num2 = Double.parseDouble(scanner.nextLine());
        
        System.out.println("-------------------");
        if ( num2 == 0.0){
            System.out.println("결과:무한대");
        } else {
            System.out.println("결과: " + (num1 / num2) );
        }
    }
}
