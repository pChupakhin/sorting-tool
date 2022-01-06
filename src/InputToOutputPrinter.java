import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Scanner;

public class InputToOutputPrinter {
    
    private final String output;
    private final File outputFile;
    
    public InputToOutputPrinter(CommandLineArgumentsHandler handler) throws FileNotFoundException {
        final Map<String, Integer> occurrencesByData
                = getAllRequiredDataFromInput(handler.getDataType(), handler.getInputFile());
        output = getSortedOutput(occurrencesByData, handler.getDataType(), handler.getSortingType());
        outputFile = handler.getOutputFile();
    }
    
    public void print() throws IOException {
        if (outputFile.isFile()) {
            printToFile();
        }
        else {
            System.out.print(output);
        }
    }
    
    private void printToFile() throws IOException{
        final FileWriter fWriter = new FileWriter(outputFile);
        fWriter.write(output);
        fWriter.close();
    }
    
    private Map<String, Integer> getAllRequiredDataFromInput(final String dataType, final File inputFile) throws FileNotFoundException {
        final int DATA_OCCURRENCE_INCREMENT_VALUE = 1;
        
        final Scanner scanner = inputFile.isFile() ? new Scanner(inputFile) : new Scanner(System.in);
        return scanner.useDelimiter(getRequiredDelimiter(dataType)).tokens()
                .map(data -> validateData(data, dataType))
                .sorted(isLong(dataType) ? Comparator.comparingLong(Long::valueOf) : String::compareToIgnoreCase)
                .map(data -> Map.entry(data, DATA_OCCURRENCE_INCREMENT_VALUE))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum, LinkedHashMap::new));
    }
    
    private String getSortedOutput(final Map<String, Integer> occurrencesByData, final String dataType, final String sortingType) {
        final int totalDataOccurrence = occurrencesByData.values().stream().mapToInt(i -> i).sum();
        
        String output = "Total " + dataType.toLowerCase() + ": " + totalDataOccurrence;
        if (isNatural(sortingType)) {
            output += occurrencesByData.entrySet().stream()
                    .flatMap(DataAndOccurrence -> Stream.generate(DataAndOccurrence::getKey)
                            .limit(DataAndOccurrence.getValue()))
                    .collect(Collectors.joining(" ", "\nSorted data: ", "\n"));
        }
        if (isByCount(sortingType)) {
            output += occurrencesByData.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(DataAndOccurrence -> String.format("%s: %s time(s), %d", DataAndOccurrence.getKey(),
                            DataAndOccurrence.getValue(), occurrencesByData.size() / DataAndOccurrence.getValue()))
                    .collect(Collectors.joining("%\n", "\n", "%\n"));
        }
        return output;
    }
    
    private String validateData(final String data, final String dataType) {
        final String WHOLE_NUMBER_REGEX = "-?[1-9]\\d*";
        
        if(!isLong(dataType) || data.matches(WHOLE_NUMBER_REGEX)) {
            return data;
        }
        System.out.print('"' + data + "\" is not a long. It will be skipped.\n");
        return "";
    }
    
    private String getRequiredDelimiter(String dataType) {
        return isLine(dataType) ? "\n" : "(?> +|\n)";
    }
    
    private boolean isLong(final String dataType) {
        return dataType.hashCode() == "LONG".hashCode() && dataType.equals("LONG");
    }
    private boolean isLine(final String dataType) {
        return dataType.hashCode() == "LINE".hashCode() && dataType.equals("LINE");
    }
    
    private boolean isNatural(final String sortingType) {
        return sortingType.hashCode() == "NATURAL".hashCode() && sortingType.equals("NATURAL");
    }
    private boolean isByCount(final String sortingType) {
        return sortingType.hashCode() == "BY_COUNT".hashCode() && sortingType.equals("BY_COUNT");
    }
    
}
