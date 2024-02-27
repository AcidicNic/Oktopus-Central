package com.thirtythreelabs.util;

import java.util.*;


public class ConvertWordToNumber {
   public static String WithSeparator(long number) {
       if (number < 0) {
           return "-" + WithSeparator(-number);
       }
       if (number / 1000L > 0) {
           return WithSeparator(number / 1000L) + "" + String.format("%1$03d", number % 1000L);
       } else {
           return String.format("%1$d", number);
       }
   }
   
   private static String[] numerals = { "eros", "cero", "zero", "uno", "un", "una", "dos",
           "tres", "cuatro", "cinco", "seis", "siete", "ocho", "mucho", "nueve", "diez",
           "once", "doce", "trece", "catorce", "quince", "dieciseis",
           "diecisiete", "dieciocho", "diecinueve", "veinte", "veintiuno", "veintiun", "veintiuna", "veintidos", 
           "veintitres", "veinticuatro", "veinticinco", "veintiseis", "veintisiete", "veintiocho", "veintinueve", "treinta", "cuarenta",
           "cincuenta", "sesenta", "setenta", "ochenta", "noventa", "cien", "ciento", 
           "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos",
           "1", "2", "3", "4", "5", "6", "7", "8", "99", "10", "30", "40", "50", "60", "70", "80", "90"};

   private static long[] values = { 0, 0, 0, 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 8,
           9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 21, 21, 22, 23, 24, 25, 26, 27, 28, 29, 
           30, 40, 50, 60, 70, 80, 90, 100, 100, 200, 300, 400, 500, 600, 700, 800, 900,
           1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 30, 40, 50, 60, 70, 80, 90};

   private static ArrayList<String> list = new ArrayList<String>(Arrays.asList(numerals));
   
   public static long parseNumerals(String text) throws Exception {
       long value = 0;
       String[] words = text.replaceAll(" y ", " ").replaceAll(" y", " ").split("\\s");
       
       for (String word : words) {
           if (!list.contains(word)) {
               throw new Exception("Unknown token : " + word);
           }

           long subval = getValueOf(word);
           if (subval == 100) {
               if (value == 0)
                   value = 100;
               else
                   value *= 100;
           } else
               value += subval;
       }

       return value;
   }

   private static long getValueOf(String word) {
       return values[list.indexOf(word)];
   }
   private static String[] words = { "millon", "mil" };

   private static long[] digits = { 1000000L, 1000L };

   public static long parse(String text) throws Exception {
       text = text.toLowerCase().replaceAll("[\\-,]", " ").replaceAll(" y "," ");
       long totalValue = 0;
       boolean processed = false;
       for (int n = 0; n < words.length; n++) {
           int index = text.indexOf(words[n]);
           if (index >= 0) {
               String text1 = text.substring(0, index).trim();
               String text2 = text.substring(index + words[n].length()).trim();

               if (text1.equals(""))
                   text1 = "uno";

               if (text2.equals(""))
                   text2 = "cero";

               totalValue = parseNumerals(text1) * digits[n]+ parse(text2);
               processed = true;
               break;
           }
       }

       if (processed)
           return totalValue;
       else
           return parseNumerals(text);
   }
   
   public static void main(String[] args) throws Exception {
       Scanner in = new Scanner(System.in);
           System.out.print("Number in words : ");
               String numberWordsText = in.nextLine();
               System.out.println("Value : "+ ConvertWordToNumber.WithSeparator(ConvertWordToNumber.parse(numberWordsText)));
           }
}