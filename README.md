# Token Utility

A tiny program to generate java code for token type definition from txt file. See example at [Input format]() Section


## Getting started

Download the executable from release, or click here ([tokenutil.jar](https://github.com/yan-lang/token-util/releases/download/v1.0/tokenutil-1.0-jar-with-dependencies.jar))

### Usage

```
Usage: tokenutil [-hV] [-o=<outputFile>] <inputFile>
A useful utility for generating token-type java interface for Yan compiler
framework.
      <inputFile>   The file contain the token definition.
  -h, --help        Show this help message and exit.
  -o, --output=<outputFile>
                    Output file for result
  -V, --version     Print version information and exit.
```

### Input format

You have to follow this format to write your token type definition to make program work correctly.

1. One token type per line.
2. Each line has at least one field:  (1) type name, and several optional fields: (2) description, (3) symbol name, (4) id, (5) group.
   1. If description is not provided, type name will be used as description. 
   2. If symbol name is not provided, description will be used as symbol name.
   3. If id is not provided, the number of line will be used as id (if this id was already used, the next number will be used, and etc).
   4. If a group is provided, you must also specify a symbol name, this name will be used as the key of group map.
3. You can declare optional fields in order without specifying the field type, but if one field is omit in the middle, all the fields after it have to specify it type with the form `type=value`, such as `id=3`, the complete list of type name can be found below.
4. Every block should be separated by comma: `,` .
5. **It is your responsibility to make sure the type name is a valid Java identifier.**

**Note**

> If you want to use these character `,`, `=`, `/`, you have to escape them first. 
> The method is identical as it is in C string escape.

Example:

- Input:

```
unknown, unknown
identifier, identifier
int_const, integer constant
plus, plus+
minus, minus-, id=3, group=binaryOp
kw_if, Keyword if, if, group=keyword
kw_true, Keyword true, true, group=keyword
kw_false, Keyword false, false, group=keyword
```

- Output:

```java
import java.util.List;

public interface Tokens {
    int UNKNOWN = 0;

    int IDENTIFIER = 1;

    int INT_CONST = 2;

    int PLUS = 3;

    int MINUS = 3;

    int KW_IF = 4;

    int KW_TRUE = 5;

    int KW_FALSE = 6;


    List<String> tokenNames = List.of("unknown", "identifier", "integer constant", "plus+", "minus-", "Keyword if", "Keyword true", "Keyword false");

    List<String> tokenSymbolNames = List.of("unknown", "identifier", "integer constant", "plus+", "minus-", "if", "true", "false");

    Map<String, Integer> binaryOp = Map.of("minus-", 3);

    Map<String, Integer> keyword = Map.of("if", 4, "true", 5, "false", 6);


}
```

## Change log

**tokenutil-2.0**

- Allow empty line.
- Support more fields: symbol name and group.

**tokenutil-1.0**

- The first useable version.