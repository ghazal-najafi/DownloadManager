//package com.DownloadManager;
//
//import java.util.Scanner;
//
//public class quera {
//    public static void main(String args[]) {
//
//        Scanner input = new Scanner(System.in);
//        int rows = input.nextInt();
//        for (int j = 1; j <= rows + 2; j++)
//            System.out.print("*");
//        System.out.println();
//        for (int i = 0; i <= rows / 2; i++) {
//            for (int j = 0; j <= rows / 2 - i; j++)
//                System.out.print("*");
//            for (int j = 0; j <= 2 * i; j++)
//                System.out.print(" ");
//            for (int j = 0; j <= rows / 2 - i; j++)
//                System.out.print("*");
//            System.out.println();
//        }
//        for (int i = 1; i <= rows / 2; i++) {
//            for (int j = 0; j < i + 1; j++)
//                System.out.print("*");
//            for (int j = 0; j < rows - 2 * i; j++)
//                System.out.print(" ");
//            for (int j = 0; j < i + 1; j++)
//                System.out.print("*");
//            System.out.println();
//        }
//        for (int j = 1; j <= rows + 2; j++)
//            System.out.print("*");
//
//    }
//}
