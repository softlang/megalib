/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.softlang.megalib.visualizer.exceptions.CommandLineException;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class CommandLine {

    private static final String FILE_OPTION_NAME = "f";

    private static final String TYPE_OPTION_NAME = "t";

    private CommandLineParser parser = new DefaultParser();

    private HelpFormatter help = new HelpFormatter();

    private Options options;

    private org.apache.commons.cli.CommandLine cli;

    private Collection<String> allowedTypes;

    public CommandLine(Collection<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
        this.options = createCommandLineOptions();
    }

    public CommandLine parse(String[] arguments) {
        try {
            this.cli = this.parser.parse(options, arguments);
            checkAllowedType();
        } catch (ParseException ex) {
            throwHelpException(ex);
        }
        return this;
    }

    private void checkAllowedType() throws ParseException {
        String type = this.cli.getOptionValue(TYPE_OPTION_NAME).toLowerCase();
        if (!this.allowedTypes.contains(type)) {
            throw new ParseException("Bad type argument: " + type + ".");
        }
    }

    private void throwHelpException(Exception parent) {
        StringWriter messageWriter = new StringWriter();
        PrintWriter helpWriter = new PrintWriter(messageWriter);

        printException(helpWriter, parent);
        this.help.printUsage(helpWriter, this.help.getWidth(), "visualizer", this.options);
        helpWriter.flush();

        throw new CommandLineException(messageWriter.toString());
    }

    private void printException(PrintWriter helpWriter, Exception parent) {
        helpWriter.print(parent.getMessage());
        helpWriter.print("\n\n");
    }

    public CommandLineArguments getRequiredArguments() {
        return new CommandLineArguments(this.cli.getOptionValue(FILE_OPTION_NAME), this.cli.getOptionValue(TYPE_OPTION_NAME));
    }

    private Options createCommandLineOptions() {
        Option file = Option.builder(FILE_OPTION_NAME)
            .required()
            .hasArg()
            .argName("file path")
            .longOpt("file")
            .desc("The megamodel file that is to be visualized")
            .build();
        Option type = Option.builder(TYPE_OPTION_NAME)
            .required()
            .hasArg()
            .argName(this.allowedTypes.stream().collect(Collectors.joining("|")))
            .longOpt("type")
            .desc("The type of visualization.")
            .build();
        return new Options()
            .addOption(file)
            .addOption(type);
    }

}
