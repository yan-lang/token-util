package yan;

import picocli.CommandLine;

public class Main {

  public static void main(String[] args) {
    int exitCode = new CommandLine(new TokenUtil()).execute(args);
    System.exit(exitCode);
  }
}
