package com.example.kravc.textalignment;

public class Program {
    public static void main(String[] args) throws Exception {
        FileWorker fileWorker = new FileWorker();
        TextMixer textMixer = new TextMixer();
        Converter converter = new Converter();

        String english = fileWorker.readTxt("ParallelTexts/English_4.txt");
        String russian = fileWorker.readTxt("ParallelTexts/Russian_4.txt");
        byte[] enBytes = converter.strToByte(english);
        byte[] ruBytes = converter.strToByte(russian);

        //String eng = english.replaceAll("\r\n\r\n", "\n");
        //fileWorker.writeTxt("ParallelTexts/English_21.txt", eng);

        int[] stat = textMixer.getStat(enBytes, ruBytes);

        String resStr = textMixer.alignTxt(0, 0, enBytes, ruBytes);
        String output = "------------------------------------------ STATISTICS ------------------------------------------\n" +
                "Paragraphs in English = " + stat[0] +
                "\nParagraphs in Russian = " + stat[1] +
                "\nSentences in English = " + stat[2] +
                "\nSentences in Russian = " + stat[3] +
                "\nWords in English = " + stat[4] +
                "\nWords in Russian = " + stat[5] +
                textMixer.getLogs() +
                "------------------------------------------- RESULT -------------------------------------------\n" +
                resStr;

        fileWorker.writeTxt("AlignmentResults/Result_4.txt", output);

        String[] wordsPair = fileWorker.getWordsPair();
		/*for (int i = 0; i < wordsPair.length; i++) {
			System.out.println(wordsPair[i]);
		}*/

		/*
		String strRu = "Ранним морозным утром, в пять часов по местному времени, вдоль платформы сирийской станции Алеппо вытянулся состав, который железнодорожные справочники торжественно именовали экспресс «Тавры».";
		String strEn = "It was five o’clock on a winter’s morning in Syria.";

		strRu = textMixer.removePunct(strRu);
		String[] strRu_arr = strRu.split(" ");
		for (int i = 0; i < strRu_arr.length; i++) {
			System.out.print(stemmer.stem(strRu_arr[i]) + " ");
		}

		System.out.print("\n");

		strEn = textMixer.removePunct(strEn);
		String[] strEn_arr = strEn.split(" ");
		for (int i = 0; i < strEn_arr.length; i++) {
			System.out.print(strEn_arr[i] + " ");
		}
		*/
        //System.out.println(stemmer.stem(sentence));
    }
}
