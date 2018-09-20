package com.example.kravc.textalignment;

import java.io.FileInputStream;
import java.io.FileWriter;

public class FileWorker {
    // Чтения файла
    String readTxt(String fileName) throws Exception {
        FileInputStream in = new FileInputStream(fileName);
        byte[] str = new byte[in.available()];
        in.read(str);
        in.close();
        String textFromFile = new String(str);

        return textFromFile;
    }

    // Запись файла
    void writeTxt(String fileName, String msg) throws Exception {
        FileWriter ouptut = new FileWriter(fileName);
        ouptut.write(msg);
        ouptut.close();
    }

    // Получения массива пар слов из корпуса
    String[] getWordsPair() throws Exception {
        String corpora = readTxt("en-ru_corpora.txt");
        String[] wordsPair = corpora.split("\n");
        return wordsPair;
    }
}
