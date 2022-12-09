package fit.wenchao.simplechatparent.utils.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cli {

    String cmdName;

    boolean correct;

    CommandParser parser;

    HelpFormatter helpFormatter;

    CommandLine cli;

    public String getCmdName() {

        return cmdName;

    }

    public boolean contains(String option) {
        return cli.hasOption(option);
    }

    public String get(String option) {
        return cli.getOptionValue(option);
    }

    public void usage() {
        helpFormatter.printHelp(">>>>>>", parser.getOptions());
    }
}