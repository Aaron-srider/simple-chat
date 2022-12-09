package fit.wenchao.simplechatparent.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncPrinterHelper {

    private SyncPrinterHelper() {
    }

    private static final SyncPrinterHelper SINGLETON = new SyncPrinterHelper();

    public static SyncPrinterHelper getSingleton() {
        return SINGLETON;
    }

    public static class Printer {

        private Printer() {
        }

        private boolean valid;

        public void println(String str) {
            if (valid) {
                System.out.println(str);
            }
        }

        public void println() {
            if (valid) {
                System.out.println();
            }
        }

        public void print(String str) {
            if (valid) {
                System.out.print(str);
            }
        }
    }

    private ThreadLocal<Printer> threadLocal  = new ThreadLocal<>();

    private final static Lock lock = new ReentrantLock();

    public Printer lock() {
        lock.lock();
        Printer printer = new Printer();
        printer.valid = true;
        threadLocal.set(printer);
        return printer;
    }

    public void unlock() {
        Printer printer = threadLocal.get();
        printer.valid = false;
        lock.unlock();
    }

    public static void main(String[] args) {
        SyncPrinterHelper syncPrinterHelper = SyncPrinterHelper.getSingleton();

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            new Thread(() -> {
                Printer printer = syncPrinterHelper.lock();
                printer.print("thread " + finalI);
                printer.print("thread " + finalI);
                printer.print("thread " + finalI);
                printer.print("thread " + finalI);
                printer.println("thread " + finalI);
                syncPrinterHelper.unlock();
            }).start();
        }

    }


}

