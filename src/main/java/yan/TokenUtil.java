package yan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(description =
    "A useful utility for generating token-type java interface for Yan compiler framework.",
    name = "tokenutil", mixinStandardHelpOptions = true)
public class TokenUtil implements Callable<Integer> {

  static String incorrect_format_prompt = "Error - line(%d):\n"
      + "You must format your token definition as follow.\n"
      + "    token_type_name, description, id\n"
      + "where description and id are optional.\n"
      + "e.g.\n"
      + "    add, add+, 1\n"
      + "    sub\n"
      + "    kw_var, keyword var";

  @Parameters(index = "0", description = "The file contain the token definition.")
  File inputFile;
  @Option(names = {"-o",
      "--output"}, defaultValue = "Tokens.java", description = "Output file for result")
  File outputFile;

  Set<Integer> usedIDs = new HashSet<>();
  Integer availableID = 0;

  @Override
  public Integer call() {
    try {
      List<String> lines = FileUtils.readLines(inputFile, "UTF-8");
      List<TokenType> tokenTypes = parseTokenTypes(lines);
      String code = generateCode(tokenTypes);
      FileUtils.write(outputFile, code, "UTF-8");
    } catch (Exception e) {
      System.err.println(e.getLocalizedMessage());
      return 1;
    }
    return 0;
  }

  private String generateCode(List<TokenType> tokenTypes) {
    StringBuilder builder = new StringBuilder();
    List<String> descriptions = new ArrayList<>();
    builder.append("import java.util.List;\n\n");
    builder.append(String
        .format("public interface %s {\n",
            FilenameUtils.removeExtension(outputFile.getName())));
    for (var tokenType : tokenTypes) {
      builder.append(String.format("    int %s = %d;\n", tokenType.name, tokenType.id))
          .append('\n');
      descriptions.add(String.format("\"%s\"", tokenType.description));
    }
    builder.append(String
        .format("    List<String> tokenNames = List.of(%s);", String.join(", ", descriptions)));
    builder.append("\n}");
    return builder.toString();
  }

  /**
   * 解析Token定义文件.
   * <pre>
   *     格式: type name, [description], [id]
   *     后两个可选
   * </pre>
   *
   * @param lines 文件的每一行
   * @return 解析出来的TokenTypes
   */
  private List<TokenType> parseTokenTypes(List<String> lines) {
    List<TokenType> tokenTypes = new ArrayList<>();

    // extract user defined ID
    for (int i = 0; i < lines.size(); i++) {
      String[] items = lines.get(i).split(",");
      if (items.length < 1 | items.length > 3) {
        throw new RuntimeException(String.format(incorrect_format_prompt, i + 1));
      }
      if (items.length == 3) {
        usedIDs.add(Integer.valueOf(items[2].strip()));
      }
    }

    // the above code ensure items.length = 1, 2, 3
    for (String line : lines) {
      String[] items = line.split(",");

      String name = validateName(items[0]);
      String description = items.length > 1 ? items[1].strip() : name.toLowerCase();
      Integer id = items.length == 3 ? Integer.valueOf(items[2].strip()) : getID();
      tokenTypes.add(new TokenType(name, description, id));
    }
    return tokenTypes;
  }

  private String validateName(String name) {
    return name.strip().toUpperCase().replaceAll("[\\s]+", "_");
  }

  private Integer getID() {
    while (usedIDs.contains(availableID)) {
      availableID += 1;
    }
    Integer tmp = availableID;
    availableID += 1;
    return tmp;
  }

  static class TokenType {

    String name;
    String description;
    Integer id;

    public TokenType(String name, String description, Integer id) {
      this.name = name;
      this.description = description;
      this.id = id;
    }

    @Override
    public String toString() {
      return "TokenType{"
          + "name='" + name + '\''
          + ", description='" + description + '\''
          + ", id=" + id + '}';
    }
  }
}
