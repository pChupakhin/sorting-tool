package sorting;

import java.io.IOException;

public class Main {
    
    public static void main(final String[] args) {
        final var handler = new CommandLineArgumentsHandler(args);
        try {
            final var printer = new InputToOutputPrinter(handler);
            printer.print();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
