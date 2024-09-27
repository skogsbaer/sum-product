# Data Modeling with Sums and Products

*Authors: Mike Sperber and Stefan Wehr*

Data Modeling is often an underappreciated aspect of software architecture,
yet it plays a crucial role in achieving not only functional but also
usability and maintainability goals. Poor
data models and poorly integrated data models can greatly
hinder architecture work.  Consequently, data modeling — particularly
of a project's core information — should be considered a fundamental
responsibility of software architecture.

This is specifically relevant for the
[iSAQB Foundation](https://www.isaqb.org/de/zertifizierungen/zertifizierungen-uebersicht/cpsa-foundation-level/)
training, which
has just gained a new [learning goal on data
modeling](https://github.com/isaqb-org/curriculum-foundation/pull/475).

This article examines two basic tools for good data models: **sums**
and **products**.  These concepts are known under a variety of names,
depending on context, community, and programming language. Products are
also known as *records*, *structs*, *data classes*, *tuples*,
or *and data*, and sums are known as *discriminated union*,
*disjoint union*, *union*, *mixed data*, and *or data*.

Sums and products have their roots in algebraic data types, which
are well known from functional programming languages like Haskell
or OCaml. However, the underlying concept is
independent from a particular programming language. In many
years of experience in software architecture and development,
we have used this concept as a valuable tool for all kinds of
data modeling tasks, in various programming languages and contexts.

This article explains the simple concept of sums and products.
Further, it shows how to encode a simplified
real-world scenario using sums
and products in modern programming languages
(Java, Python, Haskell, Kotlin, C#, Racket, Clojure, Scala, F#, Swift, Rust,
Typescript).

## Scenario

Our scenario is based on long-standing experience with a
large, commercial
[software system](https://functional-architecture.org/events/funarch-2023/#a-software-architecture-based-on-coarse-grained-self-adjusting-computations)
 that implements a health information
system to be used by hospitals. Clearly, we have greatly simplified
the scenario and omitted many details.

A health information system should provide information about
the medication of a patient. In our simplified scenario,
a medication consists of the name of a drug and its dosage.
There are two different kinds of dosages, depending on whether
the drug is administered orally via tablets or intravenously
via an infusion.

The dosage for tablets specifies the number of tablets to be
taken in the morning, at midday, and in the evening. For example,
the dosage *1-0-2* specifies that one tablet should
be taken in the morning, no tablet at midday, and two tablets
in the evening.

The dosage for infusions specifies how fast the infusion should
flow into the body (in milliliters per minute) and how
long the infusion should run (in hours). For example,
the dosage *1.5ml/min for 2h* specifies that the infusion
should run for 2 hours at the speed of 1.5ml per minute.

## Products and Sums

Let's look at the structure of the data involved:

* *Medication* consists of the drug name and the dosage.
* *Dosage* is either a dosage of tablets or of infusions.
* *Dosage of tablets* consists of the amount of tablets to
  be taken at morning, midday, and evening.
* *Dosage of infusion* consists of the speed (ml/min)
  and the duration (h).

You can see a few recurring words here: Specifically, the description of
medication and the two different dosages uses "consists of" and the
word "and", whereas the description of dosage uses "or".  Different
wordings are possible (e.g. "has attributes" for the former and "is one of
the following" for the latter), but the wordings always describe the two sorts of
data that are fundamentally different - one is "and data", the other is "or
data".  As stated above, these two concepts go by different names, but
the most common ones are *products* (for "and data") and *sums* (for
"or data").

* A **product** has several, fixed attributes.
* A **sum** has several, distinct alternatives.

## Code for Sum and Products

Here is a translation of the data descriptions into Java code:

```java
public record Medication(String drugName, Dosage dosage) {}

public sealed interface Dosage {
    record Tablet(int morning, int midday, int evening) implements Dosage {}
    record Infusion(double speed, int duration) implements Dosage {}
}
```

Java supports products through [record classes](https://openjdk.org/jeps/395),
and sums through the combination of
[sealed interfaces](https://openjdk.org/jeps/409) and classes
implementing them.  (You might have noticed that this code is not
"classic Java" and uses fairly recent features - we'll get to that.)

Programming languages differ in how they support sums and products.
Here is Python, for example:

```python
from dataclasses import dataclass

@dataclass
class TabletDosage:
    morning: int
    midday: int
    evening: int

@dataclass
class InfusionDosage:
    speed: float
    duration: int

type Dosage = TabletDosage | InfusionDosage

@dataclass
class Medication:
    drugName: str
    dosage: Dosage
```

As you can see, products are similar to Java, only that records are called
[data classes](https://docs.python.org/3/library/dataclasses.html)
in Python.  Sums, however, are not realized
via interface implementation,
but via a separate definition of `Dosage` where `|` denotes "or".

The data in this example fits a pattern extremely common in data
modeling: Is the data of this kind *or* that kind,
and depending on the kind, data has attributes A1 *and* A2
or B1 *and* B2. In our
terminology, we call this a **sum of products**. (Clearly,
more than two kinds and attributes are possible.)

Typed functional languages have a feature that directly implements
sum-of-products - so-called *algebraic data types*.  Here
is code in the functional language Haskell:

```haskell
data Dosage
  = TabletDosage { morning :: Int, midday :: Int, evening :: Int }
  | InfusionDosage { speed :: Double, duration :: Int }

data Medication = Medication { drugName :: String, dosage :: Dosage }
```

`Dosage` is the sum of the products `TableDosage` (with attributes
`morning`, `midday`, and `evening`) and `InfusionDosage` (with
attributes `speed` and `duration`).
`Medication` can be seen as a sum with only one alternative,
namely the product `Medication` (with attributes `drugName` and
`dosage`).

## Sums and Products Gone Wrong

Even though the distinction between sums and products is usually quite
clear from a description of the information to be represented in the
software system, its implementation goes wrong surprisingly often.

One reason for this is that some popular languages, mechanisms and
formalisms do not have direct support for sums.

Take SQL, for example, and imagine you'd have to store medications in
a table.   A table/relation in SQL has a fixed set of columns, and we
have to somehow map the information about dosages into a fixed format.
One way to do it would be to make columns for all possible attributes,
like so:

```sql
CREATE TABLE medications(
	drugName VARCHAR(255) NOT NULL,
	dosageKind int NOT NULL, -- 1 for tablet, 2 for infusion
	morning int,
	midday int,
	evening int,
	speed double,
	duration int)
```

Here, the `dosageKind` column is a *tag* indicating which case of the
sum it is.  If it's 1, the row is supposed to represent a tablet
dosage, and we'd expect `morning`, `midday`, and `evening` to be
non-null integers.  Presumably, `speed` and
`duration` *should* be null.  Conversely for the other case.

This is a quite indirect *encoding* of a sum as a product,
with the help of nullable types. This encoding
poses significant risks of being misused: What if `dosageKind` is 1,
but `morning` is null, and `speed` is 5?  Everyone who has been around
real-world SQL databases has seen rows like that, and the ensuing
messiness and architecture problems.

Of course, tables in a relational database are just an external
representation of the data, and an application is free to convert
between a "proper" data model in the software itself and its
relational encoding.  And it well should, be it via explicit code or
via careful use of *data transfer objects*.

Note that a related problem exists in JSON: While JSON objects are not tied
to a fixed format, JSON has no native mechanism for sums.  Instead, we
would typically use explicit tags to encode sums:

```json
{ "drugName": "Paracetamol",
  "dosageKind": "tablet",
  "morning": 1,
  "midday": 0,
  "evening": 2
}
```

Again, this example emphasizes the need to separate between the *data model* in
the software and *encodings* in a database or serialization formats,
and use an anti-corruption layer between them as necessary.

## Sums and Products and the Open/Closed Principle

Consider writing functions or methods that operate on a sum of
products, such as one that formats the dosage for human readers.
These functions usually have to branch on the specific case of dosage.
In Java, there are fundamentally two ways of doing this branching.
The *object-oriented way* uses polymorphic method dispatch to execute
different code for different kind of dosages.

```java
public sealed interface Dosage {
    String format();

    record Tablet(int morning, int midday, int evening) implements Dosage {
        @Override
        public String format() {
            return morning + "-" + midday + "-" + evening;
        }

    }
    record Infusion(double speed, int duration) implements Dosage {
        @Override
        public String format() {
            return speed + "ml/min for " + duration + "h";
        }

    }
}
```

However, modern Java also offers a more *functional way* via pattern matching.
(Pattern matching in Java is greatly influence by functional languages with
algebraic data types,
see [JEP 394](https://openjdk.org/jeps/394), [JEP 440](https://openjdk.org/jeps/440),
[JEP 441](https://openjdk.org/jeps/441), [JEP 455](https://openjdk.org/jeps/455),
[JEP 456](https://openjdk.org/jeps/456)).

```java
public class Main {
    static String formatDosage(Dosage d) {
        return switch (d) {
            case Tablet(int morning, int midday, int evening) ->
                    morning + "-" + midday + "-" + evening;
            case Infusion(double speed, int duration) ->
                    speed + "ml/min for " + duration + "h";
            // Java compiler checks that we cover all cases.
        };
    }

    static String formatMedication(Medication m) {
        return m.drugName() + ": " + formatDosage(m.dosage());
    }
}
```

The functional way has the advantage that the complete logic
for formatting is in one place, so it is easy to reason about its behavior.
Further, new operations (e.g. serializing/deserializing) can
be added without touching the existing classes and interfaces.
The disadvantage is that adding new kinds of dosages is somewhat painful because
it requires extending all relevant `switch`-expressions with the
new alternative.
(Readers familiar with the visitor pattern might notice that this design pattern
has similar tradeoffs: adding new operations is easy, adding new alternatives
is hard.)

The object-oriented way via polymorphic method dispatch has advantages
and disadvantages reversed. It is easy to add new kinds of dosages,
one merely has to add a new class implementing `Dosage`
without touching the existing code. But adding new operations is painful
because it requires a new method in the `Dosage` interface, with changes
to all classes implementing it.

The familiar [Open/Closed Principle](https://public.isaqb.org/glossary/glossary-en.html#term-open-close-principle)
states that software - in the face of new requirements - should
ideally only require extension, not modification.
Code written in what we called the functional way (or with the visitor pattern)
enables openness for new operations, whereas the object-oriented
way enables openness for new alternatives.

The designers of functional languages and those who recently brought
record/data classes and pattern matching into object-oriented
languages felt that the tradeoffs of the functional way are worth
considering.  This is particularly the case with *combinator models*,
a subject for another post.

Of course, it would be nice if both adding more cases and more
functions would equally adhere to the open/closed principle.  This is
a problem in language design known as the [expression
problem](https://en.wikipedia.org/wiki/Expression_problem).

The formatting code in Python can also be written via pattern matching.
The static type checker [pyright](https://github.com/microsoft/pyright)
checks statically that the `match` covers all possible cases.

```python
def format(m: Medication) -> str:
    return f'{m.drugName}: {formatDosage(m.dosage)}'

def formatDosage(d: Dosage) -> str:
    match d:
        case TabletDosage():
            return f'{d.morning}-{d.midday}-{d.evening}'
        case InfusionDosage():
            return f'{d.speed} ml/min for {d.duration}h'
```

## Sums and Products in Various Languages

To illustrate programming with sums and products, we've implemented
representations for medication dosages along with the associated
formatting function/method in various languages.  For brevity, we
list only the functionality for dosages, omitting the surrounding
medication record. The [full code](https://github.com/skogsbaer/sum-product/tree/main/code) is available.

### Kotlin

Kotlin offers sealed interfaces and "data classes" corresponding to
Java's records.  Kotlin does not offer pattern matching, but its
flow-sensitive type system allows convenient access to the attributes
of a summand.
The compiler statically checks that a `when` covers
all possible cases.

```kotlin
sealed interface Dosage {
    fun format(): String =
        when(this) {
            is Tablet ->
                "$morning-$midday-$evening"
            is Infusion ->
                speed.toString() + "ml/min for " + duration + "h"
        }
    data class Tablet(val morning: Int, val midday: Int, val evening: Int) : Dosage {}

    data class Infusion(val speed: Double, val duration: Int) : Dosage {}
}
```

### C#

In C#, we use records to encode products. For sums, we have
to resort to inheritance.

```csharp
public record Dosage {
    public record Tablet(int morning, int midday, int evening) : Dosage();
    public record Infusion(double speed, int duration) : Dosage();

    public string format() {
        return this switch {
            Tablet t => t.morning + "-" + t.midday + "-" + t.evening,
            Infusion i => i.speed + "ml/min for " + i.duration + "h",
            _ => throw new ApplicationException("unexpected dosage: " + this)
        };
    }

    // private constructor can prevent derived cases from being defined elsewhere
    private Dosage() {}
}
```

The compiler cannot check that
`Tablet` and `Infusion` are the only possible subtypes
of `Dosage`, so the
`switch` statement in `format` requires a default case `_`.
The
[official proposal](https://github.com/dotnet/csharplang/blob/18a527bcc1f0bdaf542d8b9a189c50068615b439/proposals/TypeUnions.md)
for
adding union types to C# would allow us to omit the default
case.

### Racket/Teaching Languages

The Racket system has many languages.  The code here is written in the
[DeinProgramm](https://www.deinprogr€amm.de/) [teaching
languages](https://docs.racket-lang.org/deinprogramm/index.html).
These have records for products, allow declaring sums as "mixed data",
and support pattern matching. There is no static checking that
the `match` covers all possible cases.

```scheme
#lang deinprogramm/sdp
(define-record tablet
  make-tablet
  (tablet-morning natural)
  (tablet-midday natural)
  (tablet-evening natural))

(define-record infusion
  make-infusion
  (infusion-speed rational)
  (infusion-duration natural))

(define dosage
  (signature (mixed tablet infusion)))

(: format-dosage (dosage -> string))
(define format-dosage
  (lambda (dosage)
    (match dosage
      ((make-tablet morning midday evening)
       (string-append (number->string morning) "-"
                      (number->string midday) "-"
                      (number->string evening)))
      ((make-infusion speed duration)
       (string-append
        (number->string speed) "ml/min for "
        (number->string duration) "h")))))
```

### Clojure

Clojure offers records for products.  Sums do not need to be
explicitly declared. There is no static checking that
the `cond` covers all possible cases.

```clojure
(defrecord Tablet [morning midday evening])
(defrecord Infusion [speed duration])

(defn format-dosage
  [dosage]
  (cond
    (instance? Tablet dosage)
      (str (:morning dosage) "-" (:midday dosage) "-" (:evening dosage))
    (instance? Infusion dosage)
      (str (:speed dosage) "ml/min for " (:duration dosage) "h")))
```

### Scala

Scala, a strongly typed language, has direct support for
algebraic data types, called enumerations.  The following is Scala 3
code. The compiler statically checks that a `match` covers
all possible cases.

```scala
enum Dosage {
  case Tablet(morning: Int, midday: Int, evening: Int)
  case Infusion(speed: Double, duration: Int)

  def format =
    this match {
      case Tablet(morning, midday, evening) =>
        morning + midday + evening
      case Infusion(speed, duration) =>
        speed + "ml/min for " + duration + "h"
    }
}
```

### F#

F# is another strongly typed language with algebraic data types and
pattern matching. The compiler statically checks that a `match`
covers all possible cases.

```fsharp
type Dosage
  = Tablet of int * int * int
  | Infusion of double * double

let formatDosage(dosage: Dosage): string =
	match dosage with
	| Tablet (morning, midday, evening) ->
	  string morning + "-" + string midday + "-" + string evening
	| Infusion (speed, duration) ->
	  string speed + "ml/min for " + string duration + "h"
```

### Swift

Swift was inspired by strongly typed functional
languages. It offers algebraic data types in the form of "enums" as well
as pattern matching.  The compiler statically checks that a `switch`
covers all possible cases.

```swift
enum Dosage {
    case Tablet(Int, Int, Int)
    case Infusion(Double, Int)
}

extension Dosage {
    func format() -> String {
        return switch self {
        case let .Tablet(morning, midday, evening):
            morning.formatted() + "-" + midday.formatted() + "-" + evening.formatted()
        case let .Infusion(speed, duration):
            speed.formatted() + "ml/min for " + duration.formatted() + "h"
        }
    }
}
```

### Rust

Rust - being in many ways inspired by Haskell - has direct support for
both algebraic data types and pattern matching. The compiler
statically checks that `match` covers all possible cases.

```rust
enum Dosage {
    Tablet { morning: i32, midday: i32, evening: i32 },
    Infusion { speed: f32, duration: i32 }
}

fn format_dosage(dosage: Dosage) -> String {
    match dosage {
        Dosage::Tablet { morning, midday, evening } =>
            format!("{morning}-{midday}-{evening}"),
        Dosage::Infusion { speed, duration } =>
            format!("{speed} ml/min for {duration}h")
    }
}
```

### Typescript

Typescript's type system has "undiscriminated unions" via the `|`
operator.  It's up to the programmer to include a tag in the participants
of a union to distinguish them. In the following example,
the compiler can check that the `switch` covers all possible cases.

```typescript
type Dosage = {
    kind: "tablet",
    morning: number,
    midday: number,
    evening: number
} | {
    kind: "infusion",
    speed: number,
    duration: number
}

function formatDosage(dosage: Dosage) {
    let d: string;
    switch (dosage.kind) {
        case "tablet":
            d = m.dosage.morning + "-" + m.dosage.midday + "-" + m.dosage.evening
            break
        case "infusion":
            d = m.dosage.speed + " ml/min for" + m.dosage.duration + "h"
            break
    }
    return m.drugName + ": " + d
}
```

## Terminology

Why are these constructs called sums and products?  One simple
illustration uses the number of values a sum or product type has.
Consider the following Java enumerations:

```java
enum T2 {
    A, B
}
enum T3 {
    X, Y, Z
}
```

(Of course, a Java `enum` is also a limited form of sum type.)

`T2` has two values and `T3` has three. Here's a product of these two
types:

```java
record P(T2 t2, T3 t3) {}
```

This type has six values - the product of 2 and 3.  Now consider a
sum:

```java
sealed interface S{}
record RT2(T2 t2) implements S {}
record RT3(T3 t3) implements S {}
```

This has 2+3=5 values.  So sums correspond to sums of numbers and
products to products of numbers.

Another way to look at these two constructs would be from a
set-theoretic perspective: products are basically [cartesian
products](https://en.wikipedia.org/wiki/Cartesian_product) and sums are
set unions.  As the programming-language constructs for sums in
Haskell or Java ensure that the participants in a sum are
distinguishable from each other, they are also called *disjoint* or
*discriminated unions*.

## Discussion

Sums and products are important building blocks of data models, allowing architects
to create ergonomic, powerful software, durable architectures, and maintainable
code. Despite the fundamental role these concepts play, it's unfortunate that
universally accepted terms for sums and products are still lacking within the
programming and architecture community.

For an extensive introduction to systematic data modeling with sums
and products (using *design recipes*), check out Felleisen and colleagues'
classical book [How to Design Programs](https://htdp.org/) and the
German book [Schreibe Dein
Programm!](https://www.deinprogramm.de/sdp/), both freely available
online.

Sums and products are also covered in the iSAQB Advanced curriculi on
[Functional Architecture (FUNAR)](https://www.isaqb.org/certifications/cpsa-certifications/cpsa-advanced-level/funar-functional-software-architecture/) and [Domain-Specific Languages (DSL)](https://www.isaqb.org/certifications/cpsa-certifications/cpsa-advanced-level/dsl/).


