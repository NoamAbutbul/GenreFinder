package com.noama.GenreIdentificationServer.controllers;

import com.noama.GenreIdentificationServer.services.ValidationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 *  LoginView. מחלקה המגדירה את דף כניסת המשתמשים
 *  By noamabutbul | 16/02/2023 10:55
 */

@PageTitle(Titles.LOGIN_PAGE_TITLE)
@Route(Routes.LOGIN_PAGE)
public class LoginView extends VerticalLayout
{
    private ValidationService validationService; // שירותי האימות
    
    private H1 title; // כותרת הדף
    private TextField username; // שדה לכתיבת שם משתמש
    private PasswordField password; // שדה לכתיבת סיסמא
    private Button btnLogin; // כפתור התחברות
    private Anchor signupLink; // לינק לדף ההרשמה
   
    
    public LoginView(ValidationService validationService)
    {
        this.validationService = validationService;
        
        title = new H1(Titles.LOGIN_H1_TITLE);
        username = new TextField("Username:");
        password = new PasswordField("Password:");
        
        btnLogin = new Button(Titles.LOGIN_BUTTON_TITLE);
        btnLogin.addClickListener(click -> doLogin(username.getValue(), password.getValue()));
        
        signupLink = new Anchor(Routes.SIGNUP_PAGE, Titles.REGISTER_TITLE);
        
        add(title, username, password, btnLogin, signupLink);
        
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }
    
    /**
     * פעולה הממחברת את המשתמש למערכת
     * @param username
     * @param password 
     */
    private void doLogin(String username, String password)
    {
        if(validationService.validUserLogin(username, password))
        {
            Notification.show(Alerts.LOGIN_SUCCESS + " Hello " + username, 4000, Notification.Position.MIDDLE);
            
            Cookies.putCookie(Cookies.USER_OK, username);
            
            if (validationService.isManager(username))
            {
                Cookies.putCookie(Cookies.MANAGER, username);
            }
           
            routeToMainPage();
        }
        else
            Notification.show(Alerts.LOGIN_NOT_SUCCESS, 4000, Notification.Position.MIDDLE);
    }
    
    /**
     * פעולה המנטוות לדף הראשי
     */
    private void routeToMainPage()
    {
        UI.getCurrent().navigate(Routes.MAIN_PAGE);
    }
   
    
  
}
