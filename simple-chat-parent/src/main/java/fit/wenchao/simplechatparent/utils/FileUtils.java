package fit.wenchao.simplechatparent.utils;

import fit.wenchao.simplechatparent.constants.FileSizes;

import java.io.*;

public class FileUtils {
    public static String stripNameSuffix(String filename) {
        int i = filename.lastIndexOf(".");
        if(i != -1) {
            return filename.substring(0, i);
        }
        return filename;
    }

    public static String getFileSuffix(String filename) {
        int i = filename.lastIndexOf(".");
        if(i != -1) {
            return filename.substring(i+1);
        }
        return "";
    }

    public static void fillFile(File file, long length) {
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(file, "rw");
            r.setLength(length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static void create(File file, long length) {
        long start = System.currentTimeMillis();
        RandomAccessFile r = null;
        try {
            r = new RandomAccessFile(file, "rw");
            r.setLength(length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }


    public static void main(String[] args) throws IOException {

        long l = FileSizes.ONE_M * 1000;

        //fillFile(new File("testfill"), l)
        //
        //;

        create(new File("testfill"), l);
    }

    public static FileSegReadCtx readFileSeg(long segLen, long spos, File file) throws IOException {

        try (RandomAccessFile randomAccess = new RandomAccessFile(file, "r")) {
            randomAccess.seek(spos);

            long totalSize = randomAccess.length();

            long realLen = Math.min(segLen, totalSize - spos);



            byte[] bytes = new byte[(int) realLen];

            randomAccess.read(bytes);

            boolean over =false;
            if(spos + bytes.length >= totalSize) {
                over = true;
            }

            FileSegReadCtx fileSegReadCtx = new FileSegReadCtx();
            fileSegReadCtx.setBytes(bytes);
            fileSegReadCtx.setOver(over);
            return fileSegReadCtx;
        }
    }
}
