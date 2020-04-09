package com.example.kristian.ideacreator;

import java.io.Serializable;

/**
 * Created by Kristian on 23/05/2016.
 */
public class IdeaObject implements Serializable{
    private long id;
    private Category ctg;
    private String text;
    private byte obscurity;
    public IdeaObject(long id, Category ctg, String text, byte obscurity) {
        this.id = id;
        this.ctg = ctg;
        this.text = text;
        this.obscurity = obscurity;
    }

    public IdeaObject() {
    }

    public byte getObscurity() {
        return obscurity;
    }

    public void setObscurity(byte obscurity) {
        this.obscurity = obscurity;
    }

    @Override
    public String toString() {
        return getText();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getCtg() {
        return ctg;
    }

    public void setCtg(Category ctg) {
        this.ctg = ctg;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
