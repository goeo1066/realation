package com.github.goeo1066.realation.core;

public interface ThrowableFunction<T, U, R, E extends Throwable> {
    R apply(T t, U u) throws E;
}
