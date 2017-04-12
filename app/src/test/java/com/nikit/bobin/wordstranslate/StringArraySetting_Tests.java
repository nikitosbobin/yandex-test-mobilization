package com.nikit.bobin.wordstranslate;

import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.storage.StringArraySetting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StringArraySetting_Tests {
    private final String key = "setting";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        sharedPreferences = mock(SharedPreferences.class);
        editor = mock(SharedPreferences.Editor.class);

        when(sharedPreferences.edit()).thenReturn(editor);
        addBooleanToSharedPreferences(key, ""); // default
    }

    @Test
    public void should_correctly_init_value_when_value_not_set_yet() {
        StringArraySetting setting = new StringArraySetting(sharedPreferences, key, new String[0]);

        verify(sharedPreferences).getString(key, "");
        verify(editor).putString(key, "");
        verify(editor).apply();
        assertArrayEquals(new String[0], setting.getValue());
    }

    @Test
    public void should_correctly_init_value_when_value_already_set() {
        addBooleanToSharedPreferences(key, "a b");
        StringArraySetting setting = new StringArraySetting(sharedPreferences, key, new String[0]);

        verify(sharedPreferences).getString(key, "");
        verify(editor).putString(key, "a b");
        verify(editor).apply();
        assertArrayEquals(new String[]{"a", "b"}, setting.getValue());
    }

    @Test
    public void setValue_should_save_new_value() {
        addBooleanToSharedPreferences(key, "a b");
        StringArraySetting setting = new StringArraySetting(sharedPreferences, key, new String[0]);
        reset(editor);

        setting.setValue(new String[] {"c", "d"});

        verify(editor).putString(key, "c d");
        verify(editor).apply();
        assertArrayEquals(new String[]{"c", "d"}, setting.getValue());
    }

    @Test
    public void setValue_should_not_save_when_value_null() {
        addBooleanToSharedPreferences(key, "a b");
        StringArraySetting setting = new StringArraySetting(sharedPreferences, key, new String[0]);
        reset(editor);

        setting.setValue(null);

        verify(editor, never()).putString(anyString(), anyString());
        verify(editor, never()).apply();
    }

    @Test
    public void setValue_should_not_save_when_value_already_saved() {
        addBooleanToSharedPreferences(key, "a b");
        StringArraySetting setting = new StringArraySetting(sharedPreferences, key, new String[0]);
        reset(editor);

        setting.setValue(new String[] {"a", "b"});

        verify(editor).putString(key, "a b"); // because arrays now overrides object.equals method
        verify(editor).apply();
    }

    private void addBooleanToSharedPreferences(String key, String value) {
        when(sharedPreferences.getString(key, "")).thenReturn(value);
    }
}
