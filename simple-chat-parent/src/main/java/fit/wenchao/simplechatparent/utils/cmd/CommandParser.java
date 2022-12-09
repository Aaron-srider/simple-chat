package fit.wenchao.simplechatparent.utils.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandParser
{

    private List<CmdOptionAttr> cmdOpAttrList = new ArrayList<>();

    public void add(String option, String optionFullName, boolean hasValue, String desc, boolean required)
    {
        cmdOpAttrList.add(new CmdOptionAttr(option, optionFullName, hasValue, desc, required));
    }

    private Options options = new Options();

    public Cli parse(String cmd)
    {
        String[] args = cmd.split("\\s+");
        return this.parse(args);
    }

    public Cli parse(String[] args)
    {
        Option opt;

        for (CmdOptionAttr cmdOptionAttr : cmdOpAttrList)
        {
            opt = new Option(
                    cmdOptionAttr.getOption(),
                    cmdOptionAttr.getOptionFullName(),
                    cmdOptionAttr.isHasValue(),
                    cmdOptionAttr.getDesc());
            opt.setRequired(cmdOptionAttr.isRequired());
            options.addOption(opt);
        }


        CommandLine cli = null;
        CommandLineParser cliParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();

        try
        {
            cli = cliParser.parse(options, args);
        } catch (ParseException e)
        {
            helpFormatter.printHelp("usage", options);
            e.printStackTrace();
            return null;
        }


        return new Cli(args[0], true, this, helpFormatter, cli);
    }

    private List<String> cmdlist = new ArrayList<>();

    public void addCmd(String cmdName)
    {
        cmdlist.add(cmdName);
    }
}
