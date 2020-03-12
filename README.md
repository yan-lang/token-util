# Token Utility

A tiny program to generate java code for token type definition from txt file. See example at [Input format]() Section


## Getting started

Download the executable from release, or click here ([tokenutil.jar]())

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

You have to follow this format to write your token type defintion to make program work correctly.

1. One token type per line.
2. Each line has three block:  (1) type name, (2) description, (3) id
   1. Description and id are optional. If description is not provided, type name will be used as description. If id is not provided, the number of line will be used as id.
   2. Every block should be seperated by comma: `,` .
   3. **It is your responsibility to make sure the type name is a valid Java identifier.**

Example:

- Input:

```
unknown, unknown
identifier, identifier
int_const, integer constant
plus, plus+
minus, minus-, 3
```

- Output:

```java
import java.util.List;

public interface Tokens {
    int UNKNOWN = 0;

    int IDENTIFIER = 1;

    int INT_CONST = 2;

    int PLUS = 4;

    int MINUS = 3;

    List<String> tokenNames = List.of("unknown", "identifier", "integer constant", "plus+", "minus-");
}
```

## Change log

**tokenutil-1.0**

- First useable version.