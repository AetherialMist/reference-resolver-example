package com.github.aetherialmist.dummy.resolver;

import com.github.aetherialmist.loader.Resolver;

public class DummyResolver implements Resolver<DummyReferenceValue> {

    @Override
    public DummyReferenceValue resolve(String reference) {
        return new DummyReferenceValue(reference);
    }

}
