package dispatcher;

import monitor.Monitor;
import treaded.Threaded;

import java.util.ArrayList;


public class ThreadDispatcher {
    private static ThreadDispatcher instance;
    private Monitor monitor = new Monitor();

    private ThreadDispatcher() { Add(monitor); }

    synchronized public static ThreadDispatcher getInstance()
    {
        if (instance == null) instance = new ThreadDispatcher();
        return instance;
    }

    public void Add(Threaded task) {
        Thread thread = new Thread(task);
        thread.setName(task.getName());
        monitor.writeToCollection(thread);
        thread.start();
    }

    public ArrayList<Thread> getMonitor(){ return monitor.getResult(); }

}
