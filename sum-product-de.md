# Datenmodellierung mit Summen und Produkten

*Autoren: Mike Sperber und Stefan Wehr*

Datenmodellierung ist oft ein unterschätzter Aspekt der Softwarearchitektur,
spielt jedoch eine entscheidende Rolle, um nicht nur funktionale, sondern auch
Nutzbarkeits- und Wartungsziele zu erreichen. Schlechte Datenmodelle und schlecht integrierte
Datenmodelle können die Architekturarbeit erheblich behindern. Daher sollte die
Datenmodellierung – insbesondere die des zentralen Informationskerns eines Projekts –
als grundlegende Verantwortung von Softwarearchitekt:innen angesehen werden.

Dies ist besonders relevant für die
[iSAQB Foundation](https://www.isaqb.org/de/zertifizierungen/zertifizierungen-uebersicht/cpsa-foundation-level/)-Schulungen,
da dort kürzlich ein neues
[Lernziel](https://github.com/isaqb-org/curriculum-foundation/pull/475)
zur Datenmodellierung hinzugefügt wurde.

Dieser Artikel untersucht zwei grundlegende Werkzeuge für gute Datenmodelle:
**Summen** und **Produkte**. Diese Konzepte sind unter verschiedenen Namen bekannt,
abhängig vom Kontext, der Community und der Programmiersprache. Produkte sind auch
bekannt als *Records*, *Structs*, *Datenklassen* (*data class*), *Tupel* oder *Und-Daten*,
während Summen als *discriminated Union*, *disjoint Union*, *Union*,
*gemischte Daten* oder *Oder-Daten* bezeichnet werden.

Summen und Produkte haben ihre Wurzeln in algebraischen Datentypen, die aus funktionalen
Programmiersprachen wie Haskell oder OCaml gut bekannt sind. Das zugrunde liegende Konzept ist jedoch
unabhängig von einer bestimmten Programmiersprache. In vielen Jahren Erfahrung in der
Softwarearchitektur und -entwicklung haben wir dieses Konzept als wertvolles Werkzeug für alle Arten
von Datenmodellierungsaufgaben in verschiedenen Programmiersprachen und Kontexten genutzt.

Dieser Artikel erklärt das einfache Konzept von Summen und Produkten. Darüber hinaus zeigt er, wie ein
einfaches aber real-world Szenario mithilfe von Summen und Produkten in modernen Programmiersprachen
(Java, Python, Haskell, Kotlin, C#, Racket, Clojure, Scala, F#, Swift, Rust, Typescript) kodiert werden
kann.


## Szenario

Unser Szenario basiert auf langjähriger Erfahrung mit einem großen, kommerziellen
[Softwaresystem](https://functional-architecture.org/events/funarch-2023/#a-software-architecture-based-on-coarse-grained-self-adjusting-computations),
welches ein Gesundheitsinformationssystem für Krankenhäuser bereitstellt.
Natürlich haben wir das Szenario stark vereinfacht.

Ein Gesundheitsinformationssystem stellt u.a. Informationen über die Medikation eines Patienten bereit.
Für unser Beispiel besteht eine Medikation aus dem Namen eines Medikaments und seiner
Dosierung. Es gibt zwei verschiedene Arten von Dosierungen, abhängig davon, ob das Medikament oral über
Tabletten oder intravenös über eine Infusion verabreicht wird.

Die Dosierung für Tabletten gibt die Anzahl der Tabletten an, die morgens, mittags und abends eingenommen
werden sollen. Beispiel: *1-0-2* bedeutet, dass morgens eine Tablette, mittags keine Tablette
und abends zwei Tabletten genommen werden sollen.

Die Dosierung für Infusionen gibt an, wie schnell die Infusion fließt (in Millilitern pro
Minute) und wie lange die Infusion laufen soll (in Stunden). Beispiel: *1,5ml/min für
2h* bedeutet, dass die Infusion 2 Stunden lang mit einer Geschwindigkeit von 1,5ml pro Minute laufen soll.


## Produkte und Summen

Die Daten haben folgende Struktur:

* *Medikation* besteht aus dem Namen des Medikaments und der Dosierung.
* *Dosierung* ist entweder eine Dosierung von Tabletten oder von Infusionen.
* *Dosierung von Tabletten* besteht aus der Anzahl der Tabletten, die morgens, mittags und abends
  eingenommen werden sollen.
* *Dosierung von Infusionen* besteht aus der Geschwindigkeit (ml/min) und der Dauer (h).

Hier tauchen einige wiederkehrende Wörter auf: Insbesondere wird in der Beschreibung der Medikation und
der beiden Dosierungsarten "besteht aus" und das Wort "und" verwendet, während in der
Beschreibung der Dosierung das Wort "oder" vorkommt. Unterschiedliche Formulierungen sind möglich (z. B.
"hat folgende Eigenschaften" bzw. "ist eine der folgenden Alternative"), aber es werden
immer zwei Arten von Daten beschrieben, die grundlegend verschieden sind: die eine Art sind
"und-Daten", die andere "oder-Daten".
Wie oben erwähnt, tragen diese beiden Konzepte verschiedene Namen, aber die gebräuchlichsten
sind *Produkte* (für "und-Daten") und *Summen* (für "oder-Daten").

* Ein **Produkt** hat mehrere feste Attribute.
* Eine **Summe** hat mehrere verschiedene Alternativen.


## Code für Summen und Produkte

Hier ist die Übersetzung der Datenbeschreibungen in Java-Code:

```java
public record Medication(String drugName, Dosage dosage) {}

public sealed interface Dosage {
    record Tablet(int morning, int midday, int evening) implements Dosage {}
    record Infusion(double speed, int duration) implements Dosage {}
}
```


Java unterstützt Produkte durch [Record-Klassen](https://openjdk.org/jeps/395)
und Summen durch
[sealed Interfaces](https://openjdk.org/jeps/409) und Klassen, die diese Interfaces implementieren.
(Hinweis:
der Code ist nicht "klassisches Java", sondern verwendet ziemlich neue Features. Mehr dazu später.)

Programmiersprachen unterscheiden sich darin, wie sie Summen und Produkte unterstützen.
Hier zum Beispiel Python:

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

Produkte werden ähnlich wie in Java kodiert, nur dass Record-Klassen in Python
[Dataclasses](https://docs.python.org/3/library/dataclasses.html) genannt werden.
Summen hingegen werden nicht durch Interfaces realisiert, sondern über eine separate
Definition von `Dosage` mittels des `|`Operators, welcher "oder" bedeutet.

Die Daten in diesem Beispiel passen zu einem Muster, das in der Datenmodellierung sehr verbreitet ist:
Die Daten haben entweder diese Ausprägung *oder* jene Ausprägung, und je nach Ausprägung haben
die Daten die Attribute A1  *und* A2 oder B1 *und* B2.
In unserer Terminologie nennen wir dies eine **Summe von Produkten**. (Mehr als zwei Ausprägungen
und Attribute sind natürlich möglich.)

Typisierte funktionale Sprachen verfügen über ein Feature, das direkt einer solchen "Summe von Produkten" entspricht,
so genannte *algebraische Datentypen*. Hier ist Code in der funktionalen Sprache Haskell:

```haskell
data Dosage
  = TabletDosage { morning :: Int, midday :: Int, evening :: Int }
  | InfusionDosage { speed :: Double, duration :: Int }

data Medication = Medication { drugName :: String, dosage :: Dosage }
```

`Dosage` ist die Summe der Produkte `TableDosage` (mit den Attributen `morning`, `midday` und `evening`) und
`InfusionDosage` (mit den Attributen `speed` und `duration`).
`Medication` kann als eine Summe mit nur einer Alternative angesehen werden, nämlich das Produkt `Medication`
(mit den Attributen `drugName` und `dosage`).

## Probleme mit Summen und Produkten

Obwohl die Unterscheidung zwischen Summen und Produkten in aller Regel ziemlich klar ist,
geht die Implementierung häufig schief.

Ein Grund dafür ist, dass einige populäre Sprachen, Mechanismen und Formalismen keine direkte Unterstützung für
Summen bieten.

Ein Beispiel ist SQL. Eine
Tabelle/Relation hat eine feste Anzahl von Spalten, und wir müssen die Informationen über Dosierungen
irgendwie in ein festes Format überführen. Eine Möglichkeit wäre, Spalten für alle möglichen Attribute zu
erstellen, zum Beispiel so:


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

Die Spalte `dosageKind` ist ein *Tag*, das angibt, um welchen Fall der Summe es sich handelt.
Wenn das Tag 1 ist,
soll die Zeile eine Tablettendosierung darstellen, und `morning`, `midday` und
`evening` sind ungleich `NULL`, aber `speed` und `duration` sind `NULL`. Umgekehrt für die
Dosierung einer Infusion.

Wir haben also eine ziemlich indirekte *Kodierung* einer Summe als Produkt, mit Hilfe von *nullable Typen*. Diese
Kodierung birgt erhebliche Risiken der Fehlanwendung: Was passiert, wenn `dosageKind` den Wert 1 hat,
aber `morning = null` ist und `speed = 5`?
Jeder, der schon einmal mit realen SQL-Datenbanken gearbeitet hat, kennt diese Art von
Inkonsistenzen und die daraus resultierenden Architekturprobleme.

Tabellen in einer relationalen Datenbank sind nur eine externe Darstellung der Daten. Aus den
genannten Gründen sollte eine Anwendung
zwischen einem "richtigen" Datenmodell in der Software selbst und dessen relationaler Kodierung konvertieren.
Das kann entweder durch expliziten Code oder durch sorgfältige Verwendung von *Data Transfer
Objects* geschiehen.

JSON hat ein ähnliches Problem. Zwar haben JSON-Objekte kein festes Format, aber trotzdem
gibt es keinen nativen Mechanismus für Summen. Stattdessen würden wir typischerweise explizite
Tags verwenden, um Summen zu kodieren:


```json
{ "drugName": "Paracetamol",
  "dosageKind": "tablet",
  "morning": 1,
  "midday": 0,
  "evening": 2
}
```

Dieses Beispiel unterstreicht erneut die Notwendigkeit, zwischen dem *Datenmodell* in der Software und den *Kodierungen*
in einer Datenbank oder in Serialisierungsformaten zu unterscheiden und bei Bedarf eine Anti-Korruptionsschicht
zwischen diesen beiden Formen zu verwenden.

## Summen, Produkte und das Open/Closed Prinzip

Betrachten wir jetzt Funktionen oder Methoden, die auf einer Summe von Produkten arbeiten.
Zum Beispiel eine Funktion zum Formatieren einer Dosierung.
Solche Funktionen müssen in der Regel die verschiedenen Arten von Dosierung unterscheiden.
In Java gibt es grundsätzlich zwei Wege, um eine solche Unterscheidung zu realisieren. Der *objektorientierte
Ansatz* verwendet polymorphe Methoden, um unterschiedlichen Code für verschiedene Arten von Dosierungen auszuführen.

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

Modernes Java bietet jedoch auch einen *funktionalen Ansatz* mittels Pattern Matching.
(Pattern Matching in Java ist stark beeinflusst von funktionalen Sprachen mit algebraischen Datentypen,
siehe [JEP 394](https://openjdk.org/jeps/394), [JEP 440](https://openjdk.org/jeps/440),
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

Der funktionale Ansatz hat den Vorteil, dass die gesamte Logik für das Formatieren an derselben Stelle ist,
was für bessere Lesbarkeit des Codes sorgt. Zudem können neue Operationen (z. B. Serialisierung/Deserialisierung)
hinzugefügt werden, ohne bestehenden Klassen und Interfaces zu ändern. Ein Nachteil ist, dass das Hinzufügen neuer
Arten von Dosierungen mühsam ist: alle relevanten `switch`-Ausdrücke müssen um die neue Alternative
erweitern werden. (Falls Sie mit dem Visitor-Pattern vertraut sind, werden Sie feststellen, dass dieses Pattern
ähnliche Eigenschaften hat: das Hinzufügen neuer Operationen ist einfach, neue Alternativen hinzuzufügen ist schwierig.)

Der objektorientierte Ansatz über polymorphe Methoden dreht Vor- und Nachteile um. Es ist
einfach, neue Arten von Dosierungen hinzuzufügen: man muss lediglich eine neue Klasse erstellen, die `Dosage` implementiert,
ohne den bestehenden Code zu ändern. Aber das Hinzufügen neuer Operationen ist mühsam, da es eine neue Methode im `Dosage`-Interface
erfordert, was Änderungen an allen implementierenden Klassen erfordert.

Das bekannte [Open/Closed Prinzip](https://public.isaqb.org/glossary/glossary-en.html#term-open-close-principle)
besagt, dass Software zur Berücksichtigung neuer Anforderungen idealerweise nur erweitert und nicht modifiziert werden sollte.
Code, der nach dem von uns als funktional bezeichneten Ansatz (oder mit dem Visitor-Pattern) geschrieben ist, ermöglicht
Offenheit für neue Operationen, während der objektorientierte Ansatz Offenheit für neue Alternativen ermöglicht.

Durch die Integration von Summen und Produkten mit Pattern Matching in nicht-funktionale Sprachen,
werden die Vorteile des funktionalen Ansatz' auch in diesen Sprachen nutzbar. Dies gilt insbesondere
für *Kombinator-Modelle*, ein Thema für einen anderen Beitrag.

Natürlich wäre es schön, wenn sowohl das Hinzufügen neuer Fälle als auch neuer Funktionen gleichermaßen dem Open/Closed-Prinzip
entsprechen würde. Diese Problem ist als [Expression Problem](https://en.wikipedia.org/wiki/Expression_problem)
bekannt.

Der Formatierungscode in Python kann ebenfalls über Pattern Matching implementiert werden. Der statische Typchecker
[pyright](https://github.com/microsoft/pyright) überprüft dabei statisch, dass
das `match` alle möglichen Fälle abdeckt.

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

## Summen und Produkte in verschiedenen Programmiersprachen

Um das Programmieren mit Summen und Produkten zu veranschaulichen, haben wir Darstellungen für
Medikamentendosierungen zusammen mit der zugehörigen Formatierungsfunktion bzw. -methode
in verschiedenen  Programmiersprachen implementiert. Um den Code kurz zu halten, zeigen wir nur
die Funktionalität für Dosierungen. Der [vollständige Code](https://github.com/skogsbaer/sum-product/tree/main/code)
ist verfügbar.

### Kotlin

Kotlin bietet sealed Interfaces und "Datenklassen", die den Records in Java entsprechen. Kotlin bietet
kein Pattern Matching, aber sein flusssensitives Typsystem ermöglicht typsicheren Zugriff auf die
Attribute der Alternativen einer Summe.
Der Compiler überprüft dabei statisch, dass ein `when` alle möglichen Fälle abdeckt.


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

In C# benutzen wir Records um Produkte zu kodieren. Summen haben keine
direkte Entsprechung in C#, wir benutzen daher Vererbung.

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

Der Compiler kann nicht überprüfen, dass `Tablet` und `Infusion` die einzigen möglichen Subtypen von
`Dosage` sind, daher erfordert die `switch`-Anweisung in `format` einen Default-Fall `_`.
Der [offizielle Vorschlag](https://github.com/dotnet/csharplang/blob/18a527bcc1f0bdaf542d8b9a189c50068615b439/proposals/TypeUnions.md)
zur Einführung von Unions in C# würde es uns ermöglichen, den Default-Fall wegzulassen.

### Racket/Lehrsprachen

Das Racket-Ökosystem enthält mehrere Sprachen. Der hier gezeigte Code ist in den
[DeinProgramm](https://www.deinprogramm.de/) [Lehrsprachen](https://docs.racket-lang.org/deinprogramm/index.html)
geschrieben. Diese unterstützen Records für Produkte, ermöglichen die Deklaration von Summen als "gemischte Daten" und
ermöglichen Pattern Matching. Es gibt jedoch keine statische Überprüfung, ob das `match` alle Fälle abdeckt.


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

Clojure unterstützt Records für Produkte. Summen müssen nicht explizit deklariert werden. Es gibt keine
statische Überprüfung, dass `cond` alle möglichen Fälle abdeckt.

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

Scala ist eine statisch getypte Sprache mit direkter Unterstützung für algebraische Datentypen,
sogenannte *Enumerations*. Der folgende Code benutzt Version 3 von Scala. Der Compiler überprüft
dabeistatisch,  dass ein `match` alle möglichen Fälle abdeckt.

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

F# ist auch eine statisch getypte Sprache mit algebraischen Datentypen und Pattern Matching.
Der Compiler überprüft statisch, dass ein `match` alle möglichen Fälle abdeckt.

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

Swift ist deutlich von statisch getypten funktionalen Sprachen beeinflusst.
Es biete algebraische Datentypen
in Form von "enums" sowie Pattern Matching. Der Compiler überprüft statisch, dass ein `switch`
alle möglichen Fälle abdeckt.

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

Rust – in vielerlei Hinsicht von Haskell inspiriert – bietet direkte Unterstützung für
algebraische Datentypen und Pattern Matching. Der Compiler überprüft statisch,
dass `match` alle möglichen Fälle abdeckt.


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

Das Typsystem von Typescript bietet "undiscriminated unions" mittels des `|`-Operators. Die
Programmierer:in muss dabei explizit einen Tag zu den Teilen der der Union hinzufügen. Im folgenden
Beispiel kann der Compiler überprüfen, dass der `switch` alle möglichen Fälle abdeckt.

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

Warum werden die gezeigten Konstrukte als Summen und Produkte bezeichnet? Eine einfache
Veranschaulichung verwendet die Anzahl der Werte, die ein Summen- oder Produkttyp hat.
Betrachten wir die folgenden Java-Enumerationen:

```java
enum T2 {
    A, B
}
enum T3 {
    X, Y, Z
}
```

(Natürlich ist ein Java `enum` auch eine eingeschränkte Form eines Summen-Typs.)

`T2` hat zwei Werte und `T3` hat drei. Hier ist ein Produkt dieser beiden Typen:

```java
record P(T2 t2, T3 t3) {}
```

Dieser Typ hat sechs Werte – das Produkt von 2 und 3. Bei Summen ist es anders:

```java
sealed interface S{}
record RT2(T2 t2) implements S {}
record RT3(T3 t3) implements S {}
```

Diese Summe hat 2+3=5 Werte.

Eine andere Möglichkeit, diese beiden Konstrukte zu betrachten, ist die
mengen-theoretischen Perspektive: Produkte sind im Wesentlichen
[kartesische Produkte](https://en.wikipedia.org/wiki/Cartesian_product) und Summen sind
Mengen-Vereinigungen. Da die Programmiersprachenkonstrukte für Summen in Haskell oder Java
sicherstellen, dass die Teilnehmer in einer Summe voneinander unterscheidbar sind, werden sie auch
als disjunkte Vereinigungen ("disjoint union" oder "discriminated union") bezeichnet.

## Discussion

Summen und Produkte sind wichtige Bausteine von Datenmodellen, die es Architekt:innen ermöglichen,
ergonomische, leistungsstarke Software, langlebige Architekturen und wartbaren Code zu erstellen.
Trotz der grundlegenden Rolle, die diese Konzepte spielen, fehlen in der
Programmier- und Architektur-Community leider immer noch allgemein akzeptierte Begriffe
für Summen und Produkte.

Für eine ausführliche Einführung in systematisches Datenmodellieren mit Summen und Produkten
(unter Verwendung von *Entwurfsrezepten*), empfehlen wir das klassische Buch von Felleisen und Co
[How to Design Programs](https://htdp.org) sowie das deutsche Buch
[Schreibe Dein Programm!](https://www.deinprogramm.de/sdp), beide frei online verfügbar.

Summen und Produkte werden auch in den iSAQB-Advanced-Curricula zu
[Funktionaler Architektur (FUNAR)](https://www.isaqb.org/certifications/cpsa-certifications/cpsa-advanced-level/funar-functional-software-architecture)
und
[Domänenspezifischen Sprachen (DSL)](https://www.isaqb.org/certifications/cpsa-certifications/cpsa-advanced-level/dsl)
behandelt.


