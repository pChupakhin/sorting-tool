import java.io.IOException;

public class Main {
    
    public static void main(String[] args) {
        try {
            new InputToOutputPrinter(new CommandLineArgumentsHandler(args)).print();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
