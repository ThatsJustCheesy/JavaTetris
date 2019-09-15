// Written by Ian A. Gregory

public interface Renderer {
    
    enum Color {
        DEFAULT, BLACK, DARK_RED, DARK_GREEN, DARK_YELLOW, DARK_BLUE, DARK_MAGENTA, DARK_CYAN, LIGHT_GRAY, DARK_GRAY, BRIGHT_RED, BRIGHT_GREEN, BRIGHT_YELLOW, BRIGHT_BLUE, BRIGHT_MAGENTA, BRIGHT_CYAN, WHITE;
        static Color[] allValues = Color.values();
        static Color fromInt(int i) {
            return allValues[i];
        }
    }
    
    public void newFrame();
    public void render(String string);
    public void setColor(Color color);
    
} // end interface
