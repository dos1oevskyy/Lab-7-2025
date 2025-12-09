package threads;

public class Semaphore {
    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;

    // Флаги для координации
    private boolean taskReady = false;
    private boolean generatorWaiting = false;
    private boolean integratorWaiting = false;

    public synchronized void beginRead() throws InterruptedException {
        while (writers > 0 || writeRequests > 0) {
            wait();
        }
        readers++;
    }

    public synchronized void endRead() {
        readers--;
        notifyAll();
    }

    public synchronized void beginWrite() throws InterruptedException {
        writeRequests++;
        while (readers > 0 || writers > 0) {
            wait();
        }
        writeRequests--;
        writers++;
    }

    public synchronized void endWrite() {
        writers--;
        notifyAll();
    }

    // Методы для координации между генератором и интегратором

    public synchronized void waitForIntegrator() throws InterruptedException {
        generatorWaiting = true;
        notifyAll();
        while (generatorWaiting) {
            wait();
        }
    }

    public synchronized void waitForGenerator() throws InterruptedException {
        integratorWaiting = true;
        notifyAll();
        while (!taskReady && integratorWaiting) {
            wait();
        }
        integratorWaiting = false;
    }

    public synchronized void signalGenerator() {
        if (generatorWaiting) {
            generatorWaiting = false;
            notifyAll();
        }
    }

    public synchronized void setTaskReady(boolean ready) {
        taskReady = ready;
        if (ready) {
            notifyAll(); // Будим интегратор, когда задание готово
        }
    }
}
