package com.example.kravc.textalignment;

import java.nio.charset.Charset;

public class Converter {

    // Конвертация строки в массив байт
    byte[] strToByte(String s) {
        byte [] arr = s.getBytes(Charset.forName("UTF-8"));
        return arr;
    }

    // Конвертация массива байт в строку
    String byteToStr(byte[] arr) {
        String s = new String(arr, Charset.forName("UTF-8"));
        return s;
    }

    // Конвертация массива объектов байт в массив байт
    byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for(int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }
}
