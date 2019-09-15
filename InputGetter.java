// Written by Ian A. Gregory

import java.util.Scanner;

class InputGetter implements InputNotifier, Runnable {
    
    private InputListener listener;
    
    public void setListener(InputListener listener) {
        this.listener = listener;
    }
    
    private Thread thread;
    
    public void start() {
        thread = new Thread(this);
        thread.start();
    }
    
    public void run() {
        while (true) {
            try {
                int readByte = System.in.read();
                byte[] bytes = new byte[] { (byte)readByte };
                String str = new String(bytes, "UTF-8");
                listener.listenInput(str);
            } catch (Exception e) {
                System.out.print(e.toString());
            }
        }
    }
    
} // end class
