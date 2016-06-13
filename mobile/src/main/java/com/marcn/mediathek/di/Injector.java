package com.marcn.mediathek.di;

/**
 * Created by marcneumann on 25.05.16.
 */
public interface Injector<T> {
    void injectWith(T component);
}
