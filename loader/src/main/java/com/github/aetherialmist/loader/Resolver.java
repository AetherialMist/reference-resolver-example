package com.github.aetherialmist.loader;

public interface Resolver<T extends ReferenceValue> {

    T resolve(String reference);

}
