// Written by Ian A. Gregory

import java.io.PrintStream;

public class TerminalRenderer implements Renderer {
    
    private PrintStream output;
    
    public TerminalRenderer(PrintStream output) {
        this.output = output;
    }
    
    public void newFrame() {
        printEscapeCode("?25l");
        printEscapeCode("H");
        printEscapeCode("?25h");
        setColor(Color.DEFAULT);
        // printEscapeCode("K");
    }
    
    public void render(String string) {
        output.print(string);
    }
    
    public void setColor(Color color) {
        printEscapeCode(terminalColorCode(color) + "m");
    }
    
    private static String terminalColorCode(Color color) {
        switch (color) {
            case DEFAULT: return "";
            case BLACK: return "30";
            case DARK_RED: return "31";
            case DARK_GREEN: return "32";
            case DARK_YELLOW: return "33";
            case DARK_BLUE: return "34";
            case DARK_MAGENTA: return "35";
            case DARK_CYAN: return "36";
            case LIGHT_GRAY: return "37";
            case DARK_GRAY: return "90";
            case BRIGHT_RED: return "91";
            case BRIGHT_GREEN: return "92";
            case BRIGHT_YELLOW: return "93";
            case BRIGHT_BLUE: return "94";
            case BRIGHT_MAGENTA: return "95";
            case BRIGHT_CYAN: return "96";
            case WHITE: return "97";
            default: return "";
        }
    }
    
    private void printEscapeCode(String code) {
        output.print("\u001b[" + code);
    }
    
} // end class
