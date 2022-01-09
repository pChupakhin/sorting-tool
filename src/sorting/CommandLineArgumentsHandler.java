package sorting;

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
        
        final List<String> options = getConstantCaseOptions(validArgs);
        final List<String> parameters = getConstantCaseParameters(validArgs);
        
        final int DEFAULT_PARAMETER_INDEX = 0;
        dataType = isExplicitOption(options, Option.DATA_TYPE)
                && isExplicitParameter(parameters, options, Option.DATA_TYPE)
                ? DataType.valueOf(getParameterValue(parameters, options, Option.DATA_TYPE))
                : DataType.values()[DEFAULT_PARAMETER_INDEX];
        sortingType = isExplicitOption(options, Option.SORTING_TYPE)
                && isExplicitParameter(parameters, options, Option.SORTING_TYPE)
                ? SortingType.valueOf(getParameterValue(parameters, options, Option.SORTING_TYPE))
                : SortingType.values()[DEFAULT_PARAMETER_INDEX];
        
        inputFile = new File(isExplicitOption(options, Option.INPUT_FILE)
                ? getParameterValue(parameters, options, Option.INPUT_FILE) : "");
        outputFile = new File(isExplicitOption(options, Option.INPUT_FILE)
                ? getParameterValue(parameters, options, Option.OUTPUT_FILE) : "");
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
    
    private List<String> getValidArgs(final String[] args) {
        final String EXECUTABLE_PAIR_OF_OPTION_AND_ARGUMENT_REGEX
                = "-(?>(?>data|sorting)Type|(?>in|out)putFile)(?: \\w+(?:\\.[\\w&&[^_]]{2,4})?)?";
        return Stream.of(String.join(" ", args).split(" (?=-)"))
                .filter(inputArgsPair -> inputArgsPair.matches(EXECUTABLE_PAIR_OF_OPTION_AND_ARGUMENT_REGEX))
                .flatMap(validArgsPair -> Stream.of(validArgsPair.split(" ")))
                .toList();
    }
    
    private List<String> getConstantCaseOptions(final List<String> validArgs) {
        return validArgs.stream().filter(this::isOption).map(this::toConstantCase).toList();
    }
    private List<String> getConstantCaseParameters(final List<String> validArgs) {
        return validArgs.stream().filter(this::isParameter).map(this::toConstantCase).toList();
    }
    
    private String toConstantCase(final String arg) {
        return arg.replaceFirst("^-", "").replaceAll("((?<=[a-z]))((?=[A-Z]))", "$1_$2").toUpperCase();
    }
    
    private boolean isOption(final String arg) {
        return arg.startsWith("-");
    }
    private boolean isParameter(final String arg) {
        return !arg.startsWith("-");
    }
    
    private int indexOfOption(final List<String> options, final Option option) {
        return options.indexOf(option.name());
    }
    
    private boolean isExplicitOption(final List<String> options, final Option option) {
        return indexOfOption(options, option) != -1;
    }
    private boolean isExplicitParameter(final List<String> parameters, final List<String> options, final Option option) {
        return parameters.size() > indexOfOption(options, option);
    }
    
    private String getParameterValue(final List<String> parameters, final List<String> options, final Option option) {
        return parameters.get(indexOfOption(options, option));
    }
    
    private void printInvalidArgsToConsole(final String[] args, final List<String> validArgs) {
        Stream.of(args)
                .filter(arg -> !String.join(" ", validArgs).toLowerCase().contains(arg.toLowerCase()))
                .forEach(invalidArg -> System.out.println(invalidArg + " is not a valid parameter. It will be skipped."));
    }
    
    private enum Option { DATA_TYPE, SORTING_TYPE, INPUT_FILE, OUTPUT_FILE }
    private enum DataType  { WORD, LINE, LONG }
    private enum SortingType { NATURAL, BY_COUNT }
    
}
