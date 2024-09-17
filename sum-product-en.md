# Data Modeling, Sums and Products

Date Modeling is an underappreciated aspect of software architecture:
Good data models can contribute greatly to meeting functional, but
also usabilility and maintainability requirements.  Conversely, poor
data models as well as poorly integrated data models can greatly
hinder architecture work.  Consequently, data modeling - at least of
the core information of a project - is squarely within the purview of
software architecture.

This is specifically relevant for the iSAQB Foundation training, which
has just gained a new [learning goal on data
modeling](https://github.com/isaqb-org/curriculum-foundation/pull/475).

This article examines the two basic tools for good data models: sums
and products.  These concepts are known under a variety of names,
depending on context, community, and programming languages: Products
also known as **records**, **structs**, **data classes**, **tuples**,
or **and data**, and sums are known as **discriminated union**,
**disjoint union**, **union**, **mixed data**, and **or data**.

We'll examine the concepts using a real-world scenario, and also look
at support for sums and products in modern programming languages.

This article gives an example for data modelling with sum and product types.
Sum and product types have their roots in algebraic data types, which
are well known from functional programming languages like Haskell
or OCaml. However, the concept underlying sum and product types are
independent from a particular programming languages. In many
years of experience in software architecture and development,
we have used this concept as a valuable tool for all kind of
data modeling tasks.

This article explains the simple concept of sum and product
types. Further, it shows how to encode a simplified
real-world scenario using sum
and product types in Python, Typescript, Kotlin, Java, C#, Haskell,
and Rust.

## Scenario

Our scenario is based on long-standing experience with a
large, commercial software system that implements a health information
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

You can see a few recurring words here: Specically, the description of
medication and the two different dosages uses *consists of* and the
word *and*, whereas the description of dosage uses *or*.  Different
wordings are possible ("has attributes" for the former and "is one of
the following" for the latter, for instance), but the two kinds of
data are fundamentally different - on is "and data", the other is "or
data".  As stated above, these two concepts go by different names, but
the most common ones are *products* (for "and data") and *sums* (for
"or data").

* A **products** has several, fixed attributes.
* A **sum** has several, distinct alternatives.

## Products, Sums, and Code

Here's a direct translation of the data descriptions into Java code:

```java
public record Medication(String drugName, Dosage dosage) {}

public sealed interface Dosage {
    record Tablet(int morning, int midday, int evening)
            implements Dosage {}
    record Infusion(double speed, int duration)
            implements Dosage {}
}
```

As you can see, Java supports products through (record) classes with
attributes, and sums through the combination of interfaces and classes
implementing them.  (You might have noticed that this code is not
"classic Java" and uses fairly recent features - we'll get to that.)

Programming languages differ in how they support sums and products.
Here is Python, for example:

```python
from typing import Union
from dataclasses import dataclass
import string

@dataclass
class TabletDosage:
    morning: int
    midday: int
    evening: int

@dataclass
class InfusionDosage:
    speed: float
    duration: int

Dosage = TabletDosage | InfusionDosage

@dataclass
class Medication:
    drugName: string
    dosage: Dosage
```

As you can see, products are similar to Java, only that "record
classes" are called "data classes" in Python.  Sums however are not
via interface implementation (as there are no interfaces in Python),
but via a separate definition of `Dosage` where `|` denotes "or".

The data in this example fits a pattern extremely common in data
modeling: Is it this *or* that *or* this other case, and depending on
which case it is, it will have attributes this *and* that.  In our
terminology, this is a "sum of products".  (Medication is a - also
common - trivial case where there is only one case.) 

Typed functional languages have a feature that directly implements
this sum-of-products pattern - so-called *algebraic data types*.  Here
is code in the functional language Haskell:

```haskell
data Dosage
  = TabletDosage { morning :: Int, midday :: Int, evening :: Int }
  | InfusionDosage { speed :: Double, duration :: Int }
 
data Medication = Medication { drugName :: String, dosage :: Dosage }
```

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
non-null integers denoting its attributes.  Presumably, `speed` and
`duration` *would* be null.  Conversely for the other case.

This is an *encoding* of a sum as a product, with the help of nullable
types, and quite indirect at that.  (In a way, nullable types are a
degenerate sum of some data type and null.)  As it is arbitrary, it
poses significant risks of being misused: What if `dosageKind` is 1,
but `morning` is null, and `speed` is 5?  Everyone who has been around
real-world SQL databases has seen rows like that, and the ensuing
messiness and architecture problems.

Of course, tables in a relational databases are just an *external
representation* of the data, and an application is free to convert
between a "proper" data model in the software itself and its
relational encoding.  And it well should, be it via explicit code or
via careful use of "DTO objects".

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

This, too emphasizes the need to separate between the *data model* in
the software and *encodings* in a database or serialization format,
and use an anti-corruption layer between them as necessary.

## Sums and Products and the Open/Closed Principle

Consider writing functions or methods that operate on a sum of
products, such as one that just formats the dosage for human readers.
These functions usually have to branch on the specific case of dosage.
In Java, there are fundamentally two ways of doing this branching.
Classic Java uses polymorphic method dispatch, like so:

```java
public sealed interface Dosage {
    String format();

    record Tablet(int morning, int midday, int evening)
            implements Dosage {
        @Override
        public String format() {
            return morning + "-" + midday + "-" + evening;
        }

    }
    record Infusion(double speed, int duration)
            implements Dosage {
        @Override
        public String format() {
            return speed + "ml/min for " + duration;
        }

    }
}	
```

However, modern Java also offers *pattern matching* to do this,
imported from functional languages with algebraic data types:

```java
public sealed interface Dosage {
    default String format() {
        return switch (this) {
            case Tablet(int morning, int midday, int evening) ->
                    morning + "-" + midday + "-" + evening;
            case Infusion(double speed, int duration) ->
                    speed + "ml/min for " + duration;
        };
    }
}
```

This way of writing the method has the advantage that everything in
one place, and easy to reason about its behavior.  The
"object-oriented method" has the advantage that the sum is more easily
*extensible*: To add another kind of dosage, one merely has to add a
new class implementing *Dosage* and implement the `format` method
without touching the existing code.  This serves the familar [Open/Closed
Principle](https://public.isaqb.org/glossary/glossary-en.html#term-open-close-principle),
which states that software - in the face of new requirements - should
ideally only require extension, not modification.

Clearly, the "object-oriented method" allows easy extension by new
cases, whereas the "functional method" requires modification in that
case.  New cases are not the only way by which software grows,
however: What about new functions or methods?  Here, the tradeoffs
reverse: The "functional method" allows easily adding new functions,
whereas the "object-oriented method" requires modifying an interface
or class.

The designers of functional languages and those who recently imported
record/data classes and pattern matching into object-oriented
languages felt that the tradeoffs of the "functional method" are worth
considering.  This is particularly the case with *combinator models*,
a subject for another post.

## Sums and Products in Various Languages

### Typescript

### Kotlin

### C#

### Rust

### Racket/Teaching Languages

## Discussion

Sums and products are the bricks and mortar of data models.
Architects can use them to realize their potential for ergonomic,
powerful software, long-lived architecture and maintainable code.
Given the importance of these basic concepts, it is unfortunate that
there are no universally established terms for sums and products in
the programming and architecture community.

For an extensive introduction to systematic data modeling with sums
and products (using *design recipes*), check out Felleisen et al's
classic [How to Design Programs](https://htdp.org/) and the
German-language [Schreibe Dein
Programm!](https://www.deinprogramm.de/sdp/), both freely available
online.

TODO:

* Discussion of advantages/disadvantages
* explain why these words


