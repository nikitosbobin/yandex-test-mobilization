package com.nikit.bobin.wordstranslate;

import android.content.SharedPreferences;

import com.nikit.bobin.wordstranslate.core.Strings;
import com.nikit.bobin.wordstranslate.storage.BooleanSetting;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// This case cover Setting<Boolean> as extension
public class BooleanSetting_Tests {
    private final String key = "setting";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Before
    public void setUp() {
        sharedPreferences = mock(SharedPreferences.class);
        editor = mock(SharedPreferences.Editor.class);

        when(sharedPreferences.edit()).thenReturn(editor);
        addBooleanToSharedPreferences(key, true); // default
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_sharedPreferences_null() {
        new BooleanSetting(null, key, false);
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_key_null() {
        new BooleanSetting(sharedPreferences, null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_when_key_empty() {
        new BooleanSetting(sharedPreferences, Strings.empty, false);
    }

    @Test
    public void should_correctly_init_value_when_value_not_set_yet() {
        BooleanSetting setting = new BooleanSetting(sharedPreferences, key, true);

        verify(sharedPreferences).getBoolean(key, true);
        verify(editor).putBoolean(key, true);
        verify(editor).apply();
        assertTrue(setting.getValue());
    }

    @Test
    public void should_correctly_init_value_when_value_already_set() {
        addBooleanToSharedPreferences(key, false);
        BooleanSetting setting = new BooleanSetting(sharedPreferences, key, true);

        verify(sharedPreferences).getBoolean(key, true);
        verify(editor).putBoolean(key, false);
        verify(editor).apply();
        assertFalse(setting.getValue());
    }

    @Test
    public void setValue_should_save_new_value() {
        addBooleanToSharedPreferences(key, false);
        BooleanSetting setting = new BooleanSetting(sharedPreferences, key, true);
        reset(editor);

        setting.setValue(true);

        verify(editor).putBoolean(key, true);
        verify(editor).apply();
        assertTrue(setting.getValue());
    }

    @Test
    public void setValue_should_not_save_when_value_null() {
        addBooleanToSharedPreferences(key, false);
        BooleanSetting setting = new BooleanSetting(sharedPreferences, key, true);
        reset(editor);

        setting.setValue(null);

        verify(editor, never()).putBoolean(any(String.class), any(Boolean.class));
        verify(editor, never()).apply();
    }

    @Test
    public void setValue_should_not_save_when_value_already_saved() {
        addBooleanToSharedPreferences(key, false);
        BooleanSetting setting = new BooleanSetting(sharedPreferences, key, true);
        reset(editor);

        setting.setValue(false);

        verify(editor, never()).putBoolean(any(String.class), any(Boolean.class));
        verify(editor, never()).apply();
    }

    private void addBooleanToSharedPreferences(String key, Boolean value) {
        when(sharedPreferences.getBoolean(key, true)).thenReturn(value);
    }
}
