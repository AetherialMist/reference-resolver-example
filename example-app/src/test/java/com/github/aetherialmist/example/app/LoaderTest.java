package com.github.aetherialmist.example.app;

import com.github.aetherialmist.dummy.resolver.DummyReferenceValue;
import com.github.aetherialmist.loader.Loader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoaderTest {


    @Test
    void testLoader() {
        DummyReferenceValue value = Loader.resolveReference("dummy", DummyReferenceValue.class, LoaderTest.class.getClassLoader());

        assertThat(value).isNotNull();
        assertThat(value.getValue()).isEqualTo("dummy");
    }

    @Test
    void testLoaderWithThreadClassLoader() {
        DummyReferenceValue value = Loader.resolveReference("dummy", DummyReferenceValue.class);

        assertThat(value).isNotNull();
        assertThat(value.getValue()).isEqualTo("dummy");
    }

    @Test
    void testLoaderWillTypeFromRunningApp() {
        SillyReferenceValue value = Loader.resolveReference("silly", SillyReferenceValue.class, LoaderTest.class.getClassLoader());
        value = Loader.resolveReference("silly", SillyReferenceValue.class, LoaderTest.class.getClassLoader());

        assertThat(value).isNotNull();
    }

}
