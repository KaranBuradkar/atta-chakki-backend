package com.attachakki.components.personalization;

import com.attachakki.entity.type.Keyboard;
import com.attachakki.entity.type.LanguageType;
import com.attachakki.entity.type.Theme;

public class PersonalizationRequestDto {
    private LanguageType language;
    private Theme theme;
    private Keyboard keyboard;

    public PersonalizationRequestDto() {}

    public LanguageType getLanguage() {
        return language;
    }

    public void setLanguage(LanguageType language) {
        this.language = language;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }
}
