package com.example.kravc.textalignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextMixer {
    String alignedTxt = "";
    String logs = "\n\n--------------------------------------------- LOG ---------------------------------------------\n";
    int lengthEn = 0, lengthRu = 0;
    int counterRu = 0, counterEn = 0;
    Byte[] separators = {'.', '!', '?', (byte) '…'};
    Stemmer stemmer = new Stemmer();
    FileWorker fileWorker = new FileWorker();

    //--------------------------------------------------------------------------------------------------------
    //------------------------------------ Вспомагательные функции -------------------------------------------
    //--------------------------------------------------------------------------------------------------------

    // Для рассчета статистики паралельных текстов
    int[] getStat (byte[] english, byte[] russian) throws Exception {
        int tabEn = 0, tabRu = 0;
        int dotEn = 0, dotRu = 0;
        int wordEn = 0, wordRu = 0;

        for(int i = 0; i < english.length; i++) {
            switch(english[i]) {
                case '	':
                    tabEn++;
                    break;
                case '.':
                case '!':
                case '?':
                    dotEn++;
                    break;
                case ' ':
                    wordEn++;
                    break;
                default:
                    break;
            }
        }

        for(int i = 0; i < russian.length; i++) {
            switch(russian[i]) {
                case '	':
                    tabRu++;
                    break;
                case '.':
                case '!':
                case '?':
                    dotRu++;
                    break;
                case ' ':
                    wordRu++;
                    break;
                default:
                    break;
            }
        }

        int[] data = {tabEn, tabRu, dotEn, dotRu, wordEn + 1, wordRu + 1};

        return data;
    }

    // Для рассчета коэффициента соответствия паралельных предложений
    double getCoefficient (int a, int b) {
        double k;
        if(a >= b) {
            k = (double) a / b;
        } else {
            k = (double) b / a;
        }
        return k;
    }

    // Для получения записанных логов
    String getLogs () {
        return logs;
    }

    // Для удаления знаков пунктуации
    String removePunct(String str) {
        StringBuilder result = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isAlphabetic(c) || Character.isDigit(c) || Character.isSpaceChar(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    //-------------------------------------------------------------------------------------------------------
    //----------------------- ФУНКЦИИ ДЛЯ АЛГОРИТМА ОСНОВАННОГО НА ПУНКТУАЦИИ -------------------------------
    //-------------------------------------------------------------------------------------------------------
    //--------------------------------------- Основные функции ----------------------------------------------
    //--------------------------------------- Нулевая глубина -----------------------------------------------
    //-------------------------------------------- 1-к-1 ----------------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    String alignTxt (int startEn, int startRu, byte[] english, byte[] russian) throws Exception {
        Converter converter = new Converter();

        List<Byte> buffered = new ArrayList<Byte>();
        List<String> added = new ArrayList<String>();

        byte[] arrEn = null;
        byte[] arrRu = null;

        String sentenceEn = "";
        String sentenceRu = "";

        for(int i = startEn; i < english.length; i++) {
            buffered.add(english[i]);
            if(buffered.get(i - startEn).equals(separators[0]) || buffered.get(i - startEn).equals(separators[1]) || buffered.get(i - startEn).equals(separators[2]) || buffered.get(i - startEn).equals(separators[3])) {
                arrEn = converter.toPrimitives(buffered.toArray(new Byte[buffered.size()]));
                sentenceEn = converter.byteToStr(arrEn);
                buffered.clear();
                break;
            }
        }

        for(int i = startRu; i < russian.length; i++) {
            buffered.add(russian[i]);
            if(buffered.get(i - startRu).equals(separators[0]) || buffered.get(i - startRu).equals(separators[1]) || buffered.get(i - startRu).equals(separators[2]) || buffered.get(i - startRu).equals(separators[3])) {
                arrRu = converter.toPrimitives(buffered.toArray(new Byte[buffered.size()]));
                sentenceRu = converter.byteToStr(arrRu);
                buffered.clear();
                break;
            }
        }

        double k = getCoefficient(converter.byteToStr(arrEn).length(), converter.byteToStr(arrRu).length());

        if(k < 1.25) {
            alignedTxt += sentenceEn + "&&" + sentenceRu + "&&";	// Записываем подходящие предложения
            lengthEn += arrEn.length;	// Увеличиваем указатель английского текста
            lengthRu += arrRu.length;	// Увеличиваем указатель российского текста

            counterRu++; counterEn++;

            if (lengthEn != english.length || lengthRu != russian.length) {
                alignTxt(lengthEn, lengthRu, english, russian);	// Продолжаем виравнивать предложения, рекурсивно вызывая эту же функцию
            }
        } else {
            added = addSentence(arrEn, arrRu, lengthEn, lengthRu, english, russian);

            alignedTxt += added.get(0);	// Записываем выровненные предложения
            lengthEn += converter.strToByte(added.get(1)).length;	// Увеличиваем указатель английского текста
            lengthRu += converter.strToByte(added.get(2)).length;	// Увеличиваем указатель российского текста

            if (lengthEn != english.length || lengthRu != russian.length) {
                alignTxt(lengthEn, lengthRu, english, russian);	// Продолжаем виравнивать предложения, рекурсивно вызывая эту же функцию
            }
        }
        return alignedTxt;
    }

    //-------------------------------------------------------------------------------------------------------
    //---------------------------------------- Первая глубина -----------------------------------------------
    //-------------------------------------------- 1-к-2 ----------------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    List<String> addSentence (byte[] arrEn, byte[] arrRu, int startEn, int startRu, byte[] english, byte[] russian) {
        Converter converter = new Converter();
        String newPair = "";

        boolean check = true;
        byte[] arr = null;

        int startEnTemp = startEn + arrEn.length;
        int startRuTemp = startRu + arrRu.length;

        List<Byte> buffered = new ArrayList<Byte>();
        List<String> result = new ArrayList<String>();

        String oldStrEn = converter.byteToStr(arrEn);
        String oldStrRu = converter.byteToStr(arrRu);
        String strEn = converter.byteToStr(arrEn);
        String strRu = converter.byteToStr(arrRu);

        if(strEn.length() > strRu.length()) {
            for(int i = startRuTemp; i < russian.length; i++) {
                buffered.add(russian[i]);
                if(buffered.get(i - startRuTemp).equals(separators[0]) || buffered.get(i - startRuTemp).equals(separators[1]) || buffered.get(i - startRuTemp).equals(separators[2]) || buffered.get(i - startRuTemp).equals(separators[3])) {
                    arr = converter.toPrimitives(buffered.toArray(new Byte[buffered.size()]));
                    String s = converter.byteToStr(arr);
                    strRu += s;
                    counterRu++;
                    check = true;
                    buffered.clear();
                    break;
                }
            }
        } else {
            for(int i = startEnTemp; i < english.length; i++) {
                buffered.add(english[i]);
                if(buffered.get(i - startEnTemp).equals(separators[0]) || buffered.get(i - startEnTemp).equals(separators[1]) || buffered.get(i - startEnTemp).equals(separators[2]) || buffered.get(i - startEnTemp).equals(separators[3])) {
                    arr = converter.toPrimitives(buffered.toArray(new Byte[buffered.size()]));
                    String s = converter.byteToStr(arr);
                    strEn += s;
                    counterEn++;
                    check = false;
                    buffered.clear();
                    break;
                }
            }
        }

        double k = getCoefficient(strEn.length(), strRu.length());

        if(k < 1.25) {
            counterRu++; counterEn++;
            newPair += strEn + "&&" + strRu + "&&";
            result.add(newPair);
            result.add(strEn);
            result.add(strRu);
        } else {
            if (check) {
                result = addSentence2(converter.strToByte(strEn), converter.strToByte(strRu), startEn, startRu, english, russian);
            } else {
                result = addSentence2(converter.strToByte(strEn), converter.strToByte(strRu), startEn, startRu, english, russian);
            }
        }

        return result;
    }

    //-------------------------------------------------------------------------------------------------------
    //---------------------------------------- Вторая глубина -----------------------------------------------
    //---------------------------------------- 2-к-2 || 1-к-3 -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    List<String> addSentence2 (byte[] arrEn, byte[] arrRu, int startEn, int startRu, byte[] english, byte[] russian) {
        Converter converter = new Converter();
        String newPair = "";

        boolean check = true;
        byte[] arr = null;

        int startEnTemp = startEn + arrEn.length;
        int startRuTemp = startRu + arrRu.length;

        List<Byte> buffered = new ArrayList<Byte>();
        List<String> result = new ArrayList<String>();

        String oldStrEn = converter.byteToStr(arrEn);
        String oldStrRu = converter.byteToStr(arrRu);
        String strEn = converter.byteToStr(arrEn);
        String strRu = converter.byteToStr(arrRu);

        if(strEn.length() > strRu.length()) {
            for(int i = startRuTemp; i < russian.length; i++) {
                buffered.add(russian[i]);
                if(buffered.get(i - startRuTemp).equals(separators[0]) || buffered.get(i - startRuTemp).equals(separators[1]) || buffered.get(i - startRuTemp).equals(separators[2]) || buffered.get(i - startRuTemp).equals(separators[3])) {
                    arr = converter.toPrimitives(buffered.toArray(new Byte[buffered.size()]));
                    String s = converter.byteToStr(arr);
                    strRu += s;
                    counterRu++;
                    check = true;
                    buffered.clear();
                    break;
                }
            }
        } else {
            for(int i = startEnTemp; i < english.length; i++) {
                buffered.add(english[i]);
                if(buffered.get(i - startEnTemp).equals(separators[0]) || buffered.get(i - startEnTemp).equals(separators[1]) || buffered.get(i - startEnTemp).equals(separators[2]) || buffered.get(i - startEnTemp).equals(separators[3])) {
                    arr = converter.toPrimitives(buffered.toArray(new Byte[buffered.size()]));
                    String s = converter.byteToStr(arr);
                    strEn += s;
                    counterEn++;
                    check = false;
                    buffered.clear();
                    break;
                }
            }
        }

        double k = getCoefficient(strEn.length(), strRu.length());

        if(k < 1.25) {
            counterRu++; counterEn++;
            newPair += strEn + "&&" + strRu + "&&";
            result.add(newPair);
            result.add(strEn);
            result.add(strRu);
            System.out.println(strEn + "\n" + strRu);
        } else {
            if (check) {
                result = addSentence3(converter.strToByte(strEn), converter.strToByte(strRu), startEn, startRu, english, russian);
            } else {
                result = addSentence3(converter.strToByte(strEn), converter.strToByte(strRu), startEn, startRu, english, russian);
            }
        }

        return result;
    }

    //-------------------------------------------------------------------------------------------------------
    //---------------------------------------- Третья глубина -----------------------------------------------
    //---------------------------------------- 2-к-3 || 1-к-4 -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    List<String> addSentence3 (byte[] arrEn, byte[] arrRu, int startEn, int startRu, byte[] english, byte[] russian) {
        Converter converter = new Converter();
        String newPair = "";

        boolean check = true;
        byte[] arr = null;

        int startEnTemp = startEn + arrEn.length;
        int startRuTemp = startRu + arrRu.length;

        List<Byte> buffered = new ArrayList<Byte>();
        List<String> result = new ArrayList<String>();

        String oldStrEn = converter.byteToStr(arrEn);
        String oldStrRu = converter.byteToStr(arrRu);
        String strEn = converter.byteToStr(arrEn);
        String strRu = converter.byteToStr(arrRu);

        if(strEn.length() > strRu.length()) {
            for(int i = startRuTemp; i < russian.length; i++) {
                buffered.add(russian[i]);
                if(buffered.get(i - startRuTemp).equals(separators[0]) || buffered.get(i - startRuTemp).equals(separators[1]) || buffered.get(i - startRuTemp).equals(separators[2]) || buffered.get(i - startRuTemp).equals(separators[3])) {
                    arr = converter.toPrimitives(buffered.toArray(new Byte[buffered.size()]));
                    String s = converter.byteToStr(arr);
                    strRu += s;
                    counterRu++;
                    check = true;
                    buffered.clear();
                    break;
                }
            }
        } else {
            for(int i = startEnTemp; i < english.length; i++) {
                buffered.add(english[i]);
                if(buffered.get(i - startEnTemp).equals(separators[0]) || buffered.get(i - startEnTemp).equals(separators[1]) || buffered.get(i - startEnTemp).equals(separators[2]) || buffered.get(i - startEnTemp).equals(separators[3])) {
                    arr = converter.toPrimitives(buffered.toArray(new Byte[buffered.size()]));
                    String s = converter.byteToStr(arr);
                    strEn += s;
                    counterEn++;
                    check = false;
                    buffered.clear();
                    break;
                }
            }
        }

        double k = getCoefficient(strEn.length(), strRu.length());

        if(k < 1.25) {
            counterRu++; counterEn++;
            newPair += strEn + "&&" + strRu + "&&";
            result.add(newPair);
            result.add(strEn);
            result.add(strRu);
        } else {
            if (check) {
                //result = addSentence3(converter.strToByte(strEn), converter.strToByte(strRu), startEn, startRu, english, russian);
            } else {
                //result = addSentence3(converter.strToByte(strEn), converter.strToByte(strRu), startEn, startRu, english, russian);
            }
        }

        return result;
    }

}
