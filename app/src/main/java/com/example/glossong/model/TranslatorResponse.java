package com.example.glossong.model;

public class TranslatorResponse {
    private YandexTranslation[] translations;

    public YandexTranslation[] getTranslations() {
        return this.translations;
    }
    public void setTranslations(YandexTranslation[] translations) {
        this.translations = translations;
    }
}
