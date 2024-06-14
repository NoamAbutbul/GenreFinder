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
 *  SignupView. מחלקה המגדירה את דף ההרשמה למערכת
 *  By noamabutbul | 16/02/2023 11:52
 */

@PageTitle(Titles.SIGNUP_PAGE_TITLE)
@Route(Routes.SIGNUP_PAGE)
public class SignupView extends VerticalLayout
{
    private ValidationService validationService; // שירותי האימות
    
    private H1 title; // כותרת הדף
    private TextField username; // שדה לכתיבת שם משתמש
    private PasswordField password; // שדה לכתיבת סיסמא
    private PasswordField confirmPassword; // שדה לאימות סיסמא
    private Button btnSubmit; // כפתור להרשמה
    private Anchor loginLink; // לינק לדף ההתחברות
    
    
    public SignupView(ValidationService validationService)
    {
        this.validationService = validationService;
        
        title = new H1(Titles.SIGNUP_H1_TITLE);
        
        username = new TextField("username:");
        password = new PasswordField("password:");
        confirmPassword = new PasswordField("confirm password:");
        
        btnSubmit = new Button("Submit");
        btnSubmit.addClickListener(event -> submit(username.getValue(), password.getValue(), confirmPassword.getValue()));
        
        loginLink = new Anchor(Routes.LOGIN_PAGE, Titles.LOGIN_TITLE);
        
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        add(title, username, password, confirmPassword, btnSubmit, loginLink);
    }

    
    /**
     * פעולה הרושמת את המשתמש למערכת
     * @param username שם המשתמש החדש
     * @param password הסיסמא
     * @param confirmPassword אימות הסיסמא
     */
    private void submit(String username, String password, String confirmPassword)
    {
        // check if passwords are same
        if (!password.equals(confirmPassword))
            Notification.show(Alerts.PASSWORD_NOT_MATCH, 4000, Notification.Position.MIDDLE);
        
        // check if usernamed not used
        else if (!validationService.validUserSignup(username, password))
            Notification.show(Alerts.USERNAME_ALREADY_USED, 4000, Notification.Position.MIDDLE);
        
        // register user
        else if (validationService.registerUser(username, password))
        {
            Notification.show(Alerts.REGISTERED_SUCCESSFULLY, 4000, Notification.Position.MIDDLE);
            Cookies.putCookie(Cookies.USER_OK, username);
            routToMainPage();
        }
        
        // register didn't work
        else
            Notification.show(Alerts.ERROR, 4000, Notification.Position.MIDDLE);
    }

    /**
     * פעולה המנווטת לדף הראשי
     */
    private void routToMainPage()
    {
        UI.getCurrent().getPage().setLocation(Routes.MAIN_PAGE);
    }
}
