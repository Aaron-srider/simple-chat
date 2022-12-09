package fit.wenchao.simplechatparent.utils.cmd;

public class CliArgMissingException extends RuntimeException {
    public CliArgMissingException(String message) {
        super(message);
    }
}