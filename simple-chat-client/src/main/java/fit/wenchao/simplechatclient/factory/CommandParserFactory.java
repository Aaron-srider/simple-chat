package fit.wenchao.simplechatclient.factory;

import fit.wenchao.simplechatparent.utils.cmd.CommandParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandParserFactory {

    @Bean
    public CommandParser getCommandParser() {
        CommandParser commandParser = new CommandParser();
        commandParser.add("u", "user", true, "target user", false);
        commandParser.add("t", "text", false, "text msg", false);
        commandParser.add("f", "file", true, "file", false);
        return commandParser;
    }
}
