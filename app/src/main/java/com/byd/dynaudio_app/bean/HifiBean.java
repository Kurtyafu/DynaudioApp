package com.byd.dynaudio_app.bean;

import android.graphics.drawable.Drawable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;


public class HifiBean extends BaseObservable {
    private String cardTitleText;
    private String cardContent1Text;
    private String cardContent2Text;
    private String cardContent3Text;
    private Drawable cardTitleImg;
    private Drawable cardContent1Img;
    private Drawable cardContent2Img;
    private Drawable cardContent3Img;

    public HifiBean() {
    }

    public HifiBean(String cardTitleText, String cardContent1Text, String cardContent2Text, String cardContent3Text, Drawable cardTitleImg, Drawable cardContent1Img, Drawable cardContent2Img, Drawable cardContent3Img) {
        this.cardTitleText = cardTitleText;
        this.cardContent1Text = cardContent1Text;
        this.cardContent2Text = cardContent2Text;
        this.cardContent3Text = cardContent3Text;
        this.cardTitleImg = cardTitleImg;
        this.cardContent1Img = cardContent1Img;
        this.cardContent2Img = cardContent2Img;
        this.cardContent3Img = cardContent3Img;
    }

    @Bindable
    public String getCardTitleText() {
        return cardTitleText;
    }

    public void setCardTitleText(String cardTitleText) {
        this.cardTitleText = cardTitleText;

    }

    @Bindable
    public String getCardContent1Text() {
        return cardContent1Text;
    }

    public void setCardContent1Text(String cardContent1Text) {
        this.cardContent1Text = cardContent1Text;

    }

    @Bindable
    public String getCardContent2Text() {
        return cardContent2Text;
    }

    public void setCardContent2Text(String cardContent2Text) {
        this.cardContent2Text = cardContent2Text;

    }

    @Bindable
    public String getCardContent3Text() {
        return cardContent3Text;
    }

    public void setCardContent3Text(String cardContent3Text) {
        this.cardContent3Text = cardContent3Text;

    }

    @Bindable
    public Drawable getCardTitleImg() {
        return cardTitleImg;
    }

    public void setCardTitleImg(Drawable cardTitleImg) {
        this.cardTitleImg = cardTitleImg;

    }

    @Bindable
    public Drawable getCardContent1Img() {
        return cardContent1Img;
    }

    public void setCardContent1Img(Drawable cardContent1Img) {
        this.cardContent1Img = cardContent1Img;

    }

    @Bindable
    public Drawable getCardContent2Img() {
        return cardContent2Img;
    }

    public void setCardContent2Img(Drawable cardContent2Img) {
        this.cardContent2Img = cardContent2Img;

    }

    @Bindable
    public Drawable getCardContent3Img() {
        return cardContent3Img;
    }

    public void setCardContent3Img(Drawable cardContent3Img) {
        this.cardContent3Img = cardContent3Img;

    }
}
