// Written by Ian A. Gregory

public class Tetris {
    
    public static void main(String[] args) {
        try {
            setRawModeEnabled(true);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    setRawModeEnabled(false);
                }
            });
            
            Game game = new Game(new TerminalRenderer(System.out), new InputGetter());
            game.run();
            System.exit(0);
        } finally {
            setRawModeEnabled(false);
        }
    } // end main
    
    private static void setRawModeEnabled(boolean enable) {
        String preset = enable ? "raw" : "cooked";
        try {
            Runtime.getRuntime().exec(new String [] { "sh", "-c", "stty " + preset + " </dev/tty" }).waitFor();
        } catch (Exception e) {
        }
    }
    
} // end class
