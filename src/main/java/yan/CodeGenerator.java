package yan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class CodeGenerator {

  public final String className;

  public CodeGenerator(String className) {
    this.className = className;
  }

  public String generate(List<TokenType> tokenTypes) {
    String importPart = "import java.util.List;";

    List<String> descriptions = new ArrayList<>();
    List<String> symbols = new ArrayList<>();
    StringBuilder typePart = new StringBuilder();
    Map<String, List<Pair<String, Integer>>> groups = new HashMap<>();

    tokenTypes.forEach((tokenType) -> {
      typePart.append(String.format("    int %s = %d;\n", tokenType.name, tokenType.id))
          .append('\n');
      descriptions.add(String.format("\"%s\"", tokenType.description));
      symbols.add(String.format("\"%s\"", tokenType.symbol));
      if (tokenType.group != null) {
        if (groups.containsKey(tokenType.group)) {
          ArrayList<Pair<String, Integer>> members = new ArrayList<>(groups.get(tokenType.group));
          members.add(new Pair<>(tokenType.symbol, tokenType.id));
          groups.put(tokenType.group, members);
        } else {
          groups.put(tokenType.group, List.of(new Pair<>(tokenType.symbol, tokenType.id)));
        }
      }
    });

    StringBuilder groupsPart = new StringBuilder();
    groups.forEach((groupName, members) -> {
      StringBuilder elements = new StringBuilder();
      members.forEach(pair -> {
        elements.append(String.format("\"%s\"", pair.getKey())).append(", ")
            .append(pair.getValue()).append(", ");
      });
      elements.deleteCharAt(elements.length() - 1);
      elements.deleteCharAt(elements.length() - 1);
      String group = String.format(groupTemplate, groupName, elements.toString());
      groupsPart.append(group).append("\n\n");
    });

    String interfacePart = String.format(interfaceTemplate, className, typePart.toString(),
        String.join(", ", descriptions),
        String.join(", ", symbols),
        groupsPart.toString());

    return String.format(fileTemplate, importPart, interfacePart);
  }

  // package, import, interface
  private static String fileTemplate = "%s\n\n%s";

  /**
   * <pre>
   * public interface 'name' {
   *   'list of token type'
   *
   *   List<String> tokenNames = List.of('names');
   *
   *   List<String> tokenSymbolNames = List.of('names');
   *
   *   'list of groups'
   * }
   * </pre>
   */
  private static String interfaceTemplate = "public interface %s {\n"
      + "%s\n"
      + "    List<String> tokenNames = List.of(%s);\n\n"
      + "    List<String> tokenSymbolNames = List.of(%s);\n\n"
      + "%s\n"
      + "}";

  private static String groupTemplate = "    Map<String, Integer> %s = Map.of(%s);";
}
