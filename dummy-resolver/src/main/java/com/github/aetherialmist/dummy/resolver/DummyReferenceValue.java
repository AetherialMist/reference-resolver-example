package com.github.aetherialmist.dummy.resolver;

import com.github.aetherialmist.loader.ReferenceValue;

public class DummyReferenceValue implements ReferenceValue {

        private final String value;

        public DummyReferenceValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
}
