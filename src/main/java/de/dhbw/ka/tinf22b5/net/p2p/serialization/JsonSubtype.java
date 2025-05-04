package de.dhbw.ka.tinf22b5.net.p2p.serialization;

public @interface JsonSubtype {
    Class<?> clazz();
    String name();
}