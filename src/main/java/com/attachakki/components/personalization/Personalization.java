package com.attachakki.components.personalization;

import com.attachakki.entity.UserDetails;
import com.attachakki.entity.type.Keyboard;
import com.attachakki.entity.type.LanguageType;
import com.attachakki.entity.type.Theme;
import jakarta.persistence.*;

@Entity
@Table(name = "personalization")
public class Personalization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_detail_id", nullable = false, unique = true)
    private UserDetails userDetail;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "language")
    private LanguageType language = LanguageType.MARATHI;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "theme")
    private Theme theme = Theme.LIGHT;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "keyboard")
    private Keyboard keyboard = Keyboard.APP;

    public Personalization() {}
    public Personalization(UserDetails userDetail) {
        this.userDetail = userDetail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDetails getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetails userDetail) {
        this.userDetail = userDetail;
    }

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