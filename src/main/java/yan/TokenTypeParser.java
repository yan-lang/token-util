package yan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenTypeParser {

  private Set<Integer> usedIDs = new HashSet<>();
  private Integer availableID = 0;


  public List<TokenType> parse(List<String> lines) {
    List<TokenType> tokenTypes = new ArrayList<>();

    // extract user defined ID
    for (String line : lines) {
      // ignore empty line
      if (line.strip().equals("")) {
        continue;
      }
      String[] items = line.split(",");
      for (String item : items) {
        String[] pair = item.split("=");
        if (pair.length == 2 && pair[0].equals("id")) {
          usedIDs.add(Integer.valueOf(pair[1]));
        }
      }
    }

    for (String line : lines) {
      if (line.strip().equals("")) {
        continue;
      }
      tokenTypes.add(parse(line));
    }
    return tokenTypes;
  }

  public TokenType parse(String line) {
    if (line.strip().equals("")) {
      return null;
    }
    String[] fields = line.split(",");
    assert fields.length >= 1;
    TokenType type = new TokenType();
    type.name = escape(fields[0]);

    for (int i = 1; i < fields.length; i++) {
      if (fields[i].contains("=")) {
        String[] items = fields[i].split("=");
        fillField(type, items[1].strip(), items[0].strip());
      } else {
        fillField(type, fields[i].strip(), i);
      }
    }

    fillDefault(type);

    return type;
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
    switch (pos) {
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
