package com.company;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Main {

    private final File file = new File("C://Flow//currentdata.txt");

    public static void main(String[] args) throws InterruptedException {

        Main m = new Main();
        Flow1 flow1 = new Flow1(m);
        Flow2 flow2 = new Flow2(m);
        flow1.start();
        flow2.start();

        Thread.sleep(65000);
        flow1.interrupt();
        flow2.interrupt();

    }

    public synchronized void writeStringToFile() throws IOException, InterruptedException {
        FileWriter writer = new FileWriter(file, true);
        String text = "the first thread writes the time:";
        for (byte b : text.getBytes(StandardCharsets.UTF_8)) {
            writer.write(b);
            writer.flush();
            Thread.sleep(200);
        }
        Calendar gi = Calendar.getInstance();
        writer.write(String.valueOf(gi.getTime()));
        writer.write('\n');
        writer.flush();
        System.out.println(text + gi.getTime());
        writer.close();
        notify();
    }


    public synchronized void writeAnotherFile() throws InterruptedException {
        while (file.length() < 200) {
            wait();
        }
        File file1 = new File("C://Flow", new SimpleDateFormat("yyyy.MM.dd HH.mm.ss'.txt'").format(new Date()));

        try {
            copy(file, file1);
            System.out.println("successfully copies");
        } catch (IOException ex) {
            System.err.println(Arrays.toString(ex.getStackTrace()) + "ОШИБКА КОПИРОВАНИЯ ФАЙЛА");
        }

        boolean delete = file.delete();
        System.out.println(delete);
    }

    private void copy(File from, File to) throws IOException {
        FileWriter fileWriter = new FileWriter(to);
        FileReader fileReader = new FileReader(from);
        while (fileReader.ready()) {
            fileWriter.write(fileReader.read());
        }
        fileReader.close();
        fileWriter.flush();
        fileWriter.close();
    }
}

//1 поток
class Flow1 extends Thread {
    private Main main;

    public Flow1(Main main) {
        this.main = main;
    }

    public void run() {
        while (!isInterrupted()) {
            try {
                main.writeStringToFile();
                Thread.sleep(1000);
            } catch (IOException | InterruptedException ex) {
                break;
            }
        }
        System.out.println("Поток N1 завершил работу");
    }
}


//2 поток
class Flow2 extends Thread {
    private Main main;

    public Flow2(Main main) {
        this.main = main;
    }

    public void run() {
        while (!isInterrupted()) {
            try {
                Thread.sleep(15000);
                main.writeAnotherFile();
            } catch (InterruptedException ex) {
                break;
            }
        }
        System.out.println("Поток N2 завершил работу");
    }
}


