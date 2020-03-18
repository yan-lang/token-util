package yan;

public class TokenType {

  String name;
  String description;
  String symbol;
  Integer id;
  String group;

  public TokenType() {
  }

  @Override
  public String toString() {
    return String
        .format("TokenType{name='%s', description='%s', symbolName='%s', id=%d, group='%s'}", name,
            description, symbol, id, group);
  }
}