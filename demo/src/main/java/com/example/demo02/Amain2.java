package com.example.demo02;

import java.math.BigDecimal;

public class Amain2 {

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal("0.06");
        BigDecimal taxRate = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);

        System.out.println(taxRate);
        BigDecimal add = taxRate.add(new BigDecimal(1));

        System.out.println(taxRate);
        System.out.println(add);


    }
}
