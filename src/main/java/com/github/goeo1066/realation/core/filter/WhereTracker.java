package com.github.goeo1066.realation.core.filter;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class WhereTracker {
    private Object value;

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> zuper, @Origin final Method method, @AllArguments Object[] args) throws Exception {
        System.out.println("intercepted");
        return zuper.call();
    }
}
