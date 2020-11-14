# DecimalExpr2
_A precise math expression evaluator for java_

---
**_DecimalExpr_** evaluates `String` math expressions to decimal form of numbers. \
It stores the exact values of integers and most rational numbers, and complex numbers. \
For numbers that are not exact, **_DecimalExpr_** stores them in `BigDecimal` form which 
eliminates the conversion error between decimal number and `double` in java.

## Classes
* `DecimalExpr` the main framework
* `DecimalExpr.Builder` the builder of the expression.

## Usage
To construct and evaluate an expression, using a `DecimalExpr.Builder` is the recommended way. \
Users can declare variables (unknowns) and macro expressions in the builder. \
A variable must have a value when evaluating the expression. Values of varaibles
can be set in class `DecimalExpr`.

#### Example
```
DecimalExpr expr = new DecimalExpr.Builder()
        .expression("2x+1")
        .build();
expr.setVariable("x", 3);
Number result = expr.evaluate();
System.out.print(result);
```
The above code snippet will output `7`, of class `Rational`.

## Operators

### Builtin Operators
There are several builtin operator in _**DecimalExpr**_. \
They can be overloaded by using the same way that defines an operator.

#### Unary Operators
* `-` negation

#### Binary Operators
* `+` add
* `-` subtract
* `*` multiply
* `/` divide
* `^` power

## Functions
### Builtin Functions
* `abs(x)`
* `sum(invariant, expression, begin, end)`

## Author
Bohan Zhang -- Github: ZhangBohan233 (TrashSoftwareStudio)
