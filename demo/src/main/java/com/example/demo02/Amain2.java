package com.example.demo02;

import java.io.File;
import java.math.BigDecimal;

public class Amain2 {

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal("0.06");
        BigDecimal taxRate = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);

        System.out.println(taxRate);
        BigDecimal add = taxRate.add(new BigDecimal(1));

        System.out.println(taxRate);
        System.out.println(add);

        System.out.println(File.separatorChar);

        String str = "C:\\Users\\Administrator\\Desktop\\12331.xlsx";
        int i = str.lastIndexOf(".");
        System.out.println(str.substring(i+1));

    }
}
