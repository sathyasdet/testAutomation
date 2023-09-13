package com.test.pages;

import com.test.configs.ConfigFileReader;

import static com.test.pages.HomePage.acceptAllCookiesButton;

public class Navigate extends UtilPage{

    private static Navigate navigate;

    public static Navigate getInstance() {
        return (navigate == null) ? new Navigate() : navigate;
    }

    public void toCazooWebSite() {
        driver.get(ConfigFileReader.getInstance().getApplicationUrl());
        if(findElement(acceptAllCookiesButton).isDisplayed())
            findElement(acceptAllCookiesButton).click();
    }

    public void toCazooSiteWithoutCookies() {
        driver.get(ConfigFileReader.getInstance().getApplicationUrl());
    }

}
