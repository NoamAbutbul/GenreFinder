package com.noama.GenreIdentificationServer.controllers;

import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

/**
 *  WelcomeView. מחלקה המגדירה את דף הפתיחה במערכת
 *  By noamabutbul | 08/02/2023 15:34
 */

@PageTitle(Titles.WELCOME_PAGE_TITLE)
@Route(Routes.WELCOME_PAGE)
public class WelcomeView extends VerticalLayout
{    
    private H1 title; // כותרת הדף
    private H3 secondTitle; // כותרת משנה
    private HtmlComponent paragraph; // הסבר על שירותי המערכת

    private Button btnLogin; // כפתור להתחברות
    private Anchor signupLink; // לינק לדף ההרשמה

    public WelcomeView()
    {        
        title = new H1(Titles.WELCOME_H1_TITLE);
        title.getStyle().set("color", "blue");
        title.getStyle().set("padding", "0");
        title.getStyle().set("margin", "0");
        
        secondTitle = new H3(Titles.WELCOME_H3_TITLE);
        
        paragraph = new HtmlComponent("p");
        paragraph.getElement().setProperty("innerHTML", Titles.WELCOME_EXPLANATION);
        
        
        btnLogin = new Button(Titles.LOGIN_BUTTON_TITLE);
        btnLogin.addClickListener(event -> routeToLoginPage());
        
        signupLink = new Anchor(Routes.SIGNUP_PAGE, Titles.REGISTER_TITLE);
       
//        Image image = new Image("src/main/java/com/noama/GenreIdentificationServer/GenreFinder.png", "My Streamed Image");
//        
//        
//        image.setWidth("200px");  // Set the width of the image
//        image.setHeight("200px"); // Set the height of the image
        
        add(title, secondTitle, paragraph, btnLogin, signupLink);
        
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        

    }

    /**
     * פעולה המנווטת לדף ההתחברות
     */
    private void routeToLoginPage()
    {
        UI.getCurrent().getPage().setLocation(Routes.LOGIN_PAGE);
    }
    
    
}
