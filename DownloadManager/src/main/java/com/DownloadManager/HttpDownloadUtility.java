package com.DownloadManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpDownloadUtility {
    Thread[] threads;
    RandomAccessFile[] randomAccessFiles;
    String[] listPause;
    boolean status = false;

    public HttpDownloadUtility(int n) {
        randomAccessFiles = new RandomAccessFile[n];
    }

    public void downloadFile(String fileURL, String path, int n) throws IOException, InterruptedException {


        listPause = new String[n + 1];
        URL url = new URL(fileURL);
        listPause[0] = fileURL;
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {

            String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1, fileURL.length());
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);


            String FilePath = path + File.separator + fileName;
            String name = fileName.split("\\.")[0];
            String RandomFilesdir = "src\\main\\resources\\RandomAccessFile" + File.separator + name;
            Files.createDirectories(Paths.get(RandomFilesdir));

            threads = new Thread[n];
            int part, r;
            AtomicInteger start = new AtomicInteger();
            part = (contentLength / n);
            r = contentLength % n;

            for (int x = 0; x < threads.length; x++) {
                int finalX = x;
                String Path = RandomFilesdir + File.separator + name + finalX;
                RandomAccessFile file = new RandomAccessFile(Path, "rw");
                threads[x] = new Thread(() -> {
                    HttpURLConnection httpURLConnection;
                    int startByte = (finalX * part) + start.get();
                    int endByte = ((finalX + 1) * part) + r;
                    int buffer_size = endByte - startByte;
                    try {
                        httpURLConnection = (HttpURLConnection) url.openConnection();

                        httpURLConnection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
                        httpURLConnection.connect();
                        System.out.printf("Thread %d start %d is end %d buffer_size %d...\n", finalX, startByte, endByte, buffer_size);

                        InputStream stream = httpURLConnection.getInputStream();
                        listPause[finalX + 1] = startByte + "-" + endByte;

                        int data;
                        while ((data = stream.read()) != -1) {
                            if (status == true) {
                                System.out.println("status pause");
                                file.close();
                                break;
                            } else
                                file.write(data);
                        }
                        file.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    start.set(r);
                });
                threads[x].start();

            }

            for (Thread thread : threads)
                thread.join();
            if (status == false)
                ReadAndMergeFile(FilePath, RandomFilesdir);


        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

    public void ReadAndMergeFile(String FilePath, String RandomFilesdir) throws IOException {
        System.out.println("Merg files.......");
        status = false;
        if (status == false) {
            File directoryPath = new File(RandomFilesdir);
            File inFiles[] = directoryPath.listFiles();
            FileOutputStream outputStream = new FileOutputStream(FilePath);
            for (File file : inFiles) {
                String inFileName = file.getName();
                String path = RandomFilesdir + File.separator + inFileName;
                RandomAccessFile inFile = new RandomAccessFile(path, "r");
                int data;
                while ((data = inFile.read()) != -1) {
                    outputStream.write(data);
                }

                inFile.close();
            }
            outputStream.close();
            System.out.println("File downloaded");
        } else {
            System.out.println("merging file is pause");
        }
    }

    public void writePauseUrl() throws IOException {
        List<List<String>> pauseList = ReadPause();
        FileWriter csvWriter = new FileWriter("src\\main\\resources\\pause.csv");
        pauseList.add(Arrays.asList(listPause));
        for (List<String> row : pauseList) {
            for (String entry : row)
                csvWriter.append(entry + ",");
            csvWriter.append("\n");
        }
        csvWriter.close();
    }

    public List<List<String>> ReadPause() throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader("src\\main\\resources\\pause.csv"));
        String row;
        String[] data = new String[0];
        List<List<String>> pauseList = new ArrayList<>();
        while ((row = csvReader.readLine()) != null) {
            data = row.split(",");
            List<String> pauseElement = new ArrayList<>();
            for (String element : data)
                pauseElement.add(element);
            pauseList.add(pauseElement);
        }
        csvReader.close();
        return pauseList;
    }

    public String[] ReadPauseUrl(String url) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader("src\\main\\resources\\pause.csv"));
        String row;
        String[] data = new String[0];
        while ((row = csvReader.readLine()) != null) {
            data = row.split(",");
            if (data[0].equals(url))
                break;
        }
        csvReader.close();
        return data;
    }

    public boolean checkUrl(String url) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader("src\\main\\resources\\pause.csv"));
        String row;
        boolean isExist = false;
        String[] data;
        while ((row = csvReader.readLine()) != null) {
            data = row.split(",");
            if (data[0].equals(url)) {
                isExist = true;
                break;
            }
        }
        csvReader.close();
        return isExist;
    }

    public void resume(String URL) throws IOException, InterruptedException {
        status = false;
        URL url = new URL(URL);
        System.out.println(URL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {

            String fileName = URL.substring(URL.lastIndexOf('/') + 1, URL.length());

            String[] rangs = ReadPauseUrl(URL);
            for (int i = 0; i < rangs.length; i++)
                System.out.println(rangs[i]);

            String FilePath = "src\\main\\resources\\" + File.separator + fileName;
            String name = fileName.split("\\.")[0];
            String RandomFilesdir = "src\\main\\resources\\RandomAccessFile" + File.separator + name;

            threads = new Thread[rangs.length - 1];
            AtomicInteger start = new AtomicInteger();
            for (int x = 0; x < threads.length; x++) {
                int finalX = x;
                threads[x] = new Thread(() -> {
                    HttpURLConnection httpURLConnection;
                    String rafPath = RandomFilesdir + File.separator + name + finalX;
                    try {
                        RandomAccessFile raf = new RandomAccessFile(rafPath, "rw");
                        int startFile = (int) raf.length();
                        int startByte = Integer.parseInt(rangs[finalX + 1].split("-")[0]) + startFile;
                        int endByte = Integer.parseInt(rangs[finalX + 1].split("-")[1]);
                        System.out.printf("Thread %d start %d is end %d ...\n", finalX, startByte, endByte);
                        httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
                        httpURLConnection.connect();
                        InputStream stream = httpURLConnection.getInputStream();
//
//                        byte[] data = stream.readAllBytes();
//                        System.out.println("data" + finalX + " " + data.length);
                        String Path = RandomFilesdir + File.separator + name + finalX;
                        System.out.println("path file" + Path);
                        RandomAccessFile file = new RandomAccessFile(Path, "rw");
                        System.out.println("start byte " + finalX + " " + startByte);
                        int data;
                        file.seek(startFile);
                        while ((data = stream.read()) != -1) {
                            if (status == true) {
                                System.out.println("status pause");
                                file.close();
                                break;
                            } else {
                                file.write(data);
                            }
                        }
                        file.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                threads[x].start();
            }
            for (Thread thread : threads)
                thread.join();
            if (status == false)
                ReadAndMergeFile(FilePath, RandomFilesdir);

        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();

    }

    public void Range(int startByte, int endByte, URL url, String Path, int startFile) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        System.out.printf("start %d is end %d ...\n", startByte, endByte);
        httpURLConnection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
        httpURLConnection.connect();
        RandomAccessFile file = new RandomAccessFile(Path, "rw");
        file.seek(startFile);
        InputStream stream = httpURLConnection.getInputStream();
        int data;
        while ((data = stream.read()) != -1) {
            if (status == true) {
                System.out.println("status pause");
                file.close();
                break;
            } else {
                file.write(data);
            }
        }
        file.close();
    }

    public void pause() throws IOException {
        status = true;
        writePauseUrl();
    }
}