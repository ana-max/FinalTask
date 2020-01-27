package treaded;


abstract public class Threaded implements Runnable {
    abstract public void run();
    abstract public String getName();
    abstract public void setName(String name);
}
