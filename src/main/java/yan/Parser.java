package yan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {

  protected static String ASSIGN = "assign";
  protected static String SEP = "separator";


  private Set<Integer> usedIDs = new HashSet<>();
  private Integer availableID = 0;


  public List<TokenType> parse(List<String> lines) throws Exception {
    List<TokenType> tokenTypes = new ArrayList<>();
    for (String line : lines) {
      // ignore empty line
      if (line.strip().equals("")) {
        continue;
      }
      TokenType tokenType = parse(line);
      tokenTypes.add(tokenType);
      if (tokenType.id != null) {
        usedIDs.add(tokenType.id);
      }
    }
    for (TokenType tokenType : tokenTypes) {
      fillDefault(tokenType);
    }
    return tokenTypes;
  }

  public TokenType parse(String line) throws Exception {
    // 先处理转义

    List<String> chars = new ArrayList<>();
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == '/') {
        if (i + 1 == line.length()) {
          throw new Exception("bad escape character");
        }
        chars.add(String.valueOf(line.charAt(i + 1)));
        i++;
      } else if (c == '=') {
        chars.add(ASSIGN);
      } else if (c == ',') {
        chars.add(SEP);
      } else {
        chars.add(String.valueOf(line.charAt(i)));
      }
    }
    chars.add(SEP);

    // 切分转义后的字符形成各个fields

    List<List<String>> fields = new ArrayList<>();
    int start = 0;
    for (int i = 0; i < chars.size(); i++) {
      if (chars.get(i).equals(SEP)) {
        fields.add(chars.subList(start, i));
        start = i + 1;
      }
    }

    // 构造TokenType

    TokenType tokenType = new TokenType();

    for (int i = 0; i < fields.size(); i++) {
      List<String> field = fields.get(i);

      // 可能是positional field，也可能是name field
      List<List<String>> pairs = new ArrayList<>();
      start = 0;
      for (int j = 0; j < field.size(); j++) {
        if (field.get(j).equals(ASSIGN)) {
          pairs.add(field.subList(start, j));
          start = j + 1;
        }
      }
      pairs.add(field.subList(start, field.size()));

      if (pairs.size() == 1) {
        fillField(tokenType, pairs.get(0).stream().reduce((a, b) -> a + b).get(), i);
      } else if (pairs.size() == 2) {
        fillField(tokenType, pairs.get(1).stream().reduce((a, b) -> a + b).get(),
                  pairs.get(0).stream().reduce((a, b) -> a + b).get());
      } else {
        throw new Exception("invalid name field");
      }
    }

    // name可能需要转义以下

    tokenType.name = escape(tokenType.name);

    return tokenType;
  }

  private void fillDefault(TokenType type) {
    if (type.description == null) {
      type.description = type.name;
    }
    if (type.symbol == null) {
      type.symbol = type.description;
    }
    if (type.id == null) {
      type.id = getID();
    }
  }

  private void fillField(TokenType type, String value, String pos) {
    value = value.strip();
    pos = pos.strip();
    switch (pos) {
      case "description":
        type.description = value;
        break;
      case "symbol":
        type.symbol = value;
        break;
      case "id":
        type.id = Integer.valueOf(value);
        break;
      case "group":
        type.group = value;
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + pos);
    }
  }

  private void fillField(TokenType type, String value, int pos) {
    value = value.strip();
    switch (pos) {
      case 0:
        type.name = value;
      case 1:
        type.description = value;
        break;
      case 2:
        type.symbol = value;
        break;
      case 3:
        type.id = Integer.valueOf(value);
        break;
      case 4:
        type.group = value;
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + pos);
    }
  }

  private String escape(String name) {
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

}
