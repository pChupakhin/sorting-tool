import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class CommandLineArgumentsHandler {
    
    private final File inputFile;
    private final File outputFile;
    private final DataType dataType;
    private final SortingType sortingType;
    
    public CommandLineArgumentsHandler (final String[] args) {
        final List<String> validArgs = getValidArgs(args);
        
        printInvalidArgsToConsole(args, validArgs);
    
        final List<String> options = toConstantCase(validArgs.stream()
                .filter(arg -> arg.startsWith("-")));
        final List<String> parameters = toConstantCase(validArgs.stream()
                .filter(arg -> !arg.startsWith("-")));
        
        final int DEFAULT_PARAMETER_INDEX = 0;
        dataType = isExplicitOption(Option.DATA_TYPE, options)
                && isExplicitParameter(Option.DATA_TYPE, options, parameters)
                ? DataType.valueOf(parameters.get(indexOfOption(Option.DATA_TYPE, options)))
                : DataType.values()[DEFAULT_PARAMETER_INDEX];
        sortingType = isExplicitOption(Option.SORTING_TYPE, options)
                && isExplicitParameter(Option.SORTING_TYPE, options, parameters)
                ? SortingType.valueOf(parameters.get(indexOfOption(Option.SORTING_TYPE, options)))
                : SortingType.values()[DEFAULT_PARAMETER_INDEX];
        
        inputFile = new File(isExplicitOption(Option.INPUT_FILE, options)
                ? parameters.get(options.indexOf(Option.INPUT_FILE.name())) : "");
        outputFile = new File(isExplicitOption(Option.INPUT_FILE, options)
                ? parameters.get(options.indexOf(Option.OUTPUT_FILE.name())) : "");
    }
    
    public String getDataType() {
        return dataType.name();
    }
    public String getSortingType() {
        return sortingType.name();
    }
    
    public File getInputFile() {
        return inputFile;
    }
    public File getOutputFile() {
        return outputFile;
    }
    
    private List<String> getValidArgs(String[] args) {
        final String EXECUTABLE_PAIR_OF_OPTION_AND_ARGUMENT_REGEX
                = "-(?>(?>data|sorting)Type|(?>in|out)putFile)(?: \\w+(?:\\.\\w&&[^_]{2,4})?)?";
        
        return Stream.of(String.join(" ", args).split(" (?=-)"))
                .filter(inputArgsPair -> inputArgsPair.matches(EXECUTABLE_PAIR_OF_OPTION_AND_ARGUMENT_REGEX))
                .flatMap(validArgsPair -> Stream.of(validArgsPair.split(" ")))
                .toList();
    }
    
    private void printInvalidArgsToConsole(final String[] args, final List<String> validArgs) {
        Stream.of(args)
                .filter(arg -> !String.join(" ", validArgs).toLowerCase().contains(arg.toLowerCase()))
                .forEach(invalidArg -> System.out.println(invalidArg + " is not a valid parameter. It will be skipped."));
    }
    
    private List<String> toConstantCase(final Stream<String> argsStream) {
        return argsStream
                .map(arg -> arg.replaceAll("((?<=[a-z]))((?=[A-Z]))", "$1_$2")
                        .replaceFirst("^-", "")
                        .toUpperCase())
                .toList();
    }
    
    private int indexOfOption(final Option option, final List<String> options) {
        return options.indexOf(option.name());
    }
    private boolean isExplicitOption(final Option option, final List<String> options) {
        return indexOfOption(option, options) != -1;
    }
    private boolean isExplicitParameter(final Option option, final List<String> options, final List<String> parameters) {
        return parameters.size() > indexOfOption(option, options);
    }
    
    private enum Option { DATA_TYPE, SORTING_TYPE, INPUT_FILE, OUTPUT_FILE }
    private enum DataType  { WORD, LINE, LONG }
    private enum SortingType { NATURAL, BY_COUNT }
    
}
