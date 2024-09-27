---
title: Data Modeling with Sums and Products
author: Stefan Wehr and Mike Sperber
---

## A simple scenario

* **Medication** consists of the name of a drug \Alert{and} its dosage.
* **Dosage** is either a dosage for tablets \Alert{or} for infusions.
  * **Tablets**: amount for morning, \Alert{and} for midday,
    \Alert{and} for evening. Example: *1-0-2*
  * **Infusions**: how fast \Alert{and} how long flows the infusion.\NL
    Example: *1.5ml/min for 2h*

## Sums and products

* **Product**: several, fixed attributes.
  * record
  * struct
  * data classe
  * tuple
  * and data
* **Sum**: several, distinct alternatives.
  * discriminated union
  * disjoint union
  * union
  * mixed data
  * or data
* Origin: functional programming languages
* But supported in many programming languages:
  Java, Python, Haskell, Kotlin, Racket, Clojure, Scala, F#,
  Swift, Rust, Typescript, (C#)

## Example in Java

```java
public record Medication(String drugName, Dosage dosage) {}

public sealed interface Dosage {
    record Tablet(int morning, int midday, int evening) implements Dosage {}
    record Infusion(double speed, int duration) implements Dosage {}
}

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
}
```

## Classical OO

```java
public interface Dosage {
    String format();
}

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
```

## Open/Closed Principle

**Sums and products**

* Simple to add new operations
* Painful to add new kinds of dosages
* Similar tradeoffs as the visitor pattern

**Classical OO via polymorphic method dispatch**

* Simple to add new kinds of dosages
* Painful to add new operations

## Closing

* Very simple concept
* Often used implicitly
* Technologies like SQL and JSON miss support for sums,
  creating all kinds of problems
* Upcoming blog article with many more languages
* Draft: <https://github.com/skogsbaer/sum-product>
