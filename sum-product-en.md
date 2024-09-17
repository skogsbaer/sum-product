# Data modelling with sum and products

This article gives an example for data modelling with sum and product types.
Sum and product types have their roots in algebraic data types, which
are well known from functional programming languages like Haskell
or OCaml. However, the concept underlying sum and product types are
independent from a particular programming languages. In many
years of experience in software architecture and development,
we have used this concept as a valuable tool for all kind of
data modelling tasks.

This article explains the simple concept of sum and product
types. Further, it shows how to encode a simplified
real-world scenario using sum
and product types in Python, Typescript, Kotlin, Java, C#, Haskell,
and Rust.

## Terminology

* **Product types** model data that has several, fixed attributes.
  They are also known as **records**, **structs**, **data classes**,
  **tuples**, and **and data**. A simple example for a product type
  would be a type for rectangles, with attributes *width* and *height*.
* **Sum types** model data that has several, distinct alternatives.
  They are also known as
  **discriminated union**, **disjoint union**, **union**,
  **mixed data**, and **or data**.
  A simple example for a sum type would be a type for geometric
  shapes with alternatives *rectangle* and *circle*.

## Scenario

The following scenario is based on long-standing experience with
a large, commercial software system that implements a health
information system to be used by hospitals. Clearly, we have
greatly simplified the scenario and omitted many details.

A health information system should provide information about
the medication of a patient. In our simplified scenario,
a medication consists of the name of a drug and its dosage.
There are two different kinds of dosages, depending on whether
the drug is aministered orally via tablets or intravenously
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

## Modelling using sum and products

In the scenario just explained, we find the following entities
that we need to model.

* *Medication*, consisting of the drug name and the dosage.
* *Dosage*, being either the dosage of tablets or of infusions.
* *Dosage of tablets*, consisting of the amount of tablets to
  be taken at morning, midday, and evening.
* *Dosage of infusion*, consisting of the speed (ml/min)
  and the duration (h).

*Medication*, *dosage of tablets*, and *dosage of infusion*
are product types because they consist of several, fixed attributes.
On the other hand, *dosage* has two alternatives, so it is
a sum type.

We next show how to realize this data model based on sum and
product types in various programming languages. You may
skip those programming languages you are not interested in.
But make sure to read the discussion section at the very end.

## Python

## Typescript

## Kotlin

## Java

## C#

## Haskell

## Rust

## Discussion

TODO:

* Related work, other approaches
* Discussion of advantages/disadvantages
