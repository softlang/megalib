/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.cli;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.softlang.megalib.visualizer.Visualizer;
import org.softlang.megalib.visualizer.VisualizerOptions;
import org.softlang.megalib.visualizer.exceptions.CommandLineException;
import org.softlang.megalib.visualizer.models.Graph;
import org.softlang.megalib.visualizer.models.ModelToGraph;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class CommandLineTest {

    @Test
    public void testValidData() {
        CommandLine cli = new CommandLine(Arrays.asList("graphviz", "testtype"));

        String data[] = {
            "-f",
            "abc123",
            "-t",
            "graphviz"
        };
        cli.parse(data);
        Assert.assertEquals(
            new CommandLineArguments("abc123", "graphviz"), cli.getRequiredArguments());
    }

    @Test
    public void testValidData2() {
        CommandLine cli = new CommandLine(Arrays.asList("graphviz", "testtype"));
        String another[] = {
            "-f",
            "abc123",
            "-t",
            "testtype"
        };
        cli.parse(another);
        Assert.assertEquals(
            new CommandLineArguments("abc123", "testtype"), cli.getRequiredArguments());
    }

    @Test
    public void testWebBrowser() {
        CommandLine cli = new CommandLine(Arrays.asList("graphviz", "testtype"));

        String data[] = {"-f", "../models/webbrowser/Webbrowser.megal", "-t", "graphviz"};
        cli.parse(data);
        VisualizerOptions options = VisualizerOptions.of(cli.getRequiredArguments());

        Graph graph = new ModelToGraph(options).createGraph();

        Visualizer visualizer = new Visualizer(options);
        visualizer.plotModel(graph);

        System.out.println("Visualization complete.");
    }

    @Test(expected = CommandLineException.class)
    public void testWithNoSupportedTypes() {
        CommandLine cli = new CommandLine(Collections.emptyList());

        String data[] = {
            "-f",
            "abc123",
            "-t",
            "test"
        };

        cli.parse(data);
    }

    @Test(expected = CommandLineException.class)
    public void testWithInvalidType() {
        CommandLine cli = new CommandLine(Arrays.asList("abc", "123"));

        String data[] = {
            "-f",
            "abc123",
            "-t",
            "test"
        };

        cli.parse(data);
    }

    @Test(expected = CommandLineException.class)
    public void testWithMissingFileArgument() {
        CommandLine cli = new CommandLine(Arrays.asList("abc", "123"));

        String data[] = {
            "-t",
            "test"
        };

        cli.parse(data);
    }

    @Test(expected = CommandLineException.class)
    public void testWithMissingTypeArgument() {
        CommandLine cli = new CommandLine(Arrays.asList("abc", "123"));

        String data[] = {
            "-f",
            "abc123",
        };

        cli.parse(data);
    }

}
