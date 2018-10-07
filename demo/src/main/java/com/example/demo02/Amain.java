package com.example.demo02;

import java.text.MessageFormat;
import java.util.Date;

public class Amain {

    public static void main(String[] args){
        String str = "https://wxdev.i-yin.com.cn/mimiapp/living/xxx?userId=%s&orderNo=%s&token=%s";
        System.out.println(String.format(str,"111","222","333"));

        String str2 = "https://wxdev.i-yin.com.cn/mimiapp/living/xxx?userId={0}&orderNo={1}&token={2}";
        System.out.println(MessageFormat.format(str2, "111","222","333"));

        String stringFormat  = "lexical error at position %s, encountered %s, expected %s ";
        String messageFormat ="lexical error at position {0}, encountered {1}, expected {2}";
        System.out.println(String.format(stringFormat, 123, 100, 456));
        System.out.println(MessageFormat.format(messageFormat, new Date(), 100, 456));

    }
}
