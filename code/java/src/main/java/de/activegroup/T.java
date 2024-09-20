package de.activegroup;

enum T2 {
    A, B
}
enum T3 {
    X, Y, Z
}

record P(T2 t2, T3 t3) {}

sealed interface S{}
record RT2(T2 t2) implements S {}
record RT3(T3 t3) implements S {}
