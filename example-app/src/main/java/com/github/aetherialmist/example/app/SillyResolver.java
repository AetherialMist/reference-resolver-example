package com.github.aetherialmist.example.app;

import com.github.aetherialmist.loader.Resolver;

public class SillyResolver implements Resolver<SillyReferenceValue> {

    @Override
    public SillyReferenceValue resolve(String reference) {
        return new SillyReferenceValue();
    }

}
