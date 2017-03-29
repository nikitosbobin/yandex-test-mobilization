package com.nikit.bobin.wordstranslate;

import com.nikit.bobin.wordstranslate.translating.models.SynonymGroup;

import org.junit.Test;

import static junit.framework.Assert.assertSame;

public class SynonymGroup_Tests {
    @Test(expected = NullPointerException.class)
    public void should_fail_when_mean_null() {
        new SynonymGroup(null, new String[0]);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_synonyms_null() {
        new SynonymGroup(new String[0], null);
    }

    @Test
    public void should_correctly_init_fields() {
        String[] mean = new String[5];
        String[] synonyms = new String[10];

        SynonymGroup synonymGroup = new SynonymGroup(mean, synonyms);

        assertSame(mean, synonymGroup.getMean());
        assertSame(synonyms, synonymGroup.getSynonyms());
    }
}
