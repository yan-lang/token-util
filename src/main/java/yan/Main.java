package yan;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(description =
    "A useful utility for generating token-type java interface for Yan compiler framework.",
    name = "tokenutil", version = "tokenutil 2.0", mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

  @Parameters(index = "0", description = "The file contain the token definition.")
  File inputFile;
  @Option(names = {"-o",
      "--output"}, defaultValue = "Tokens.java", description = "Output file for result")
  File outputFile;

  public static void main(String[] args) {
    int exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() {
    try {
      List<String> lines = FileUtils.readLines(inputFile, "UTF-8");
      List<TokenType> tokenTypes = new TokenTypeParser().parse(lines);
      String code = new CodeGenerator(FilenameUtils.removeExtension(outputFile.getName()))
          .generate(tokenTypes);
      FileUtils.write(outputFile, code, "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }
}
