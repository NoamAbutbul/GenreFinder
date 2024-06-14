package com.noama.GenreIdentificationServer.controllers;

import com.noama.GenreIdentificationServer.services.MongoService;
import com.noama.GenreIdentificationServer.model.Song;
import com.noama.GenreIdentificationServer.model.User;
import com.noama.GenreIdentificationServer.services.UserTypeService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.List;

/**
 *  UsersView. מחלקה המגדירה את דף ניהול המשתמשים במערכת
 *  By noamabutbul | 09/02/2023 00:36
 */

@PageTitle(Titles.USER_PAGE_TITLE)
@Route(Routes.USERS_PAGE)
public class UsersView extends VerticalLayout
{
    private MongoService mongoService; // שירותי המונגו
    
    private AppLayoutNavbar navbar; // סרגל הניווט
    private H1 title; // כותרת הדף
    private Grid<User> grid; // גריד למשתמשים
    private Grid<Song> songsHistoryGrid; // גדיר להיסטוריית השירים
    
    private List<Song> histortySongs; // רשימת הסיטוריית השירים
    
    
    private TextField filter; // שדה לחיפוש משתמש
    private Button btnAdd; // כפתור להוספת משתמש
    private Button btnUpdate; // כפתור לעדכון משתמש
    private Button btnDelete; // כפתור למחיקת משתמש
    private Button btnClearUserForm; // כפתור לניקוי טופס המשתמש
    
    // טופס המשתמש
    private TextField username; // שם המשתמש
    private TextField password; // סיסמא
    private ComboBox<String> type; // סוג המשתמש
    
    private boolean isManager; // משתנה השומר האם המשתמש הוא מנהל

    public UsersView(MongoService mongoService)
    {        
        isManager = false;
        
        if (!checkAccess())
           return;
        
        this.mongoService = mongoService;
        
        navbar = new AppLayoutNavbar(isManager);
        
        title = new H1(Titles.USER_H1_TITLE);
        title.getStyle().set("color", "green");
        title.getStyle().set("padding", "0");
        title.getStyle().set("margin", "0");
        
        HorizontalLayout operationBar = new HorizontalLayout();
        filter = new TextField();
        filter.setPlaceholder("Filter by name");
        filter.addKeyPressListener(event -> search(filter.getValue(), event.getKey()));


        btnAdd = new Button("Add User");
        btnAdd.addClickListener(event -> addUserToDB());
        
        btnUpdate = new Button("Update User");
        btnUpdate.addClickListener(event -> updateUserInDB());
        btnUpdate.setEnabled(false);

        btnDelete = new Button("Delete User");
        btnDelete.addClickListener(event -> deleteUserFromDB());
        btnDelete.setEnabled(false);
        
        operationBar.add(filter, btnAdd, btnUpdate, btnDelete);


        
        HorizontalLayout content = new HorizontalLayout();

        grid = new Grid<>(User.class, false); // TODO -> try to show list<Song> in grid or make another grid down
        
        
        grid.addColumn(User::getUsername).setHeader("Username").setSortable(true);
        grid.addColumn(User::getPassword).setHeader("Password").setSortable(true);
        grid.addColumn(User::getTypeInString).setHeader("Type User").setSortable(true);
        
        grid.setAllRowsVisible(true);
        
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        
        List<User> users = mongoService.getAllUsers();
        grid.setItems(users);
        
        grid.addItemClickListener(event ->
        {
            User user = event.getItem();
            
            System.out.println("selected user = " + user);
            
            fillUserForm(user);
        });
        
        content.setWidthFull();
        content.expand(grid);
        content.add(grid, createUserForm());
        
        
        VerticalLayout gridForSongsHistoryLayout = new VerticalLayout();
        
        songsHistoryGrid = new Grid<>(Song.class, false);
       
        
        songsHistoryGrid.addColumn(Song::getName).setHeader("Name").setSortable(true);
        songsHistoryGrid.addColumn(Song::getGenre).setHeader("Genre").setSortable(true);
        
        songsHistoryGrid.setAllRowsVisible(true);
        
        songsHistoryGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        
        histortySongs = new ArrayList<>();
        
        gridForSongsHistoryLayout.add(songsHistoryGrid);
        
        add(navbar, title, operationBar, content, gridForSongsHistoryLayout);
    }
    
    /**
     * פעולה היוצרת את טופס המשתמש
     * @return פאנל טופס המשתמש
     */
    private VerticalLayout createUserForm()
    {
        VerticalLayout userForm = new VerticalLayout();
        
        H3 title = new H3(Titles.USER_FORM_H3_TITLE);
        userForm.setWidth("300px");

        username = new TextField("Username");
        password = new TextField("Password");
        type = new ComboBox<>("User Type");
        type.setItems(UserTypeService.allTypesInString());

       
        btnClearUserForm = new Button("Clear Form");
        btnClearUserForm.addClickListener(event -> clearUserForm());

        userForm.add(title, username, password, type, btnClearUserForm);
        userForm.expand(username);

        return userForm;
    }

    /**
     * פעולה הממלאת את טופס המשתמש
     * @param user המשתמש שממנו נמלא את המידע
     */
    private void fillUserForm(User user)
    {
        username.setValue(user.getUsername());
        password.setValue(user.getPassword());
        type.setValue(UserTypeService.convertTypeToString(user.getType()));

        username.setReadOnly(true);
        
        btnDelete.setEnabled(true);
        btnUpdate.setEnabled(true);
        
        btnAdd.setEnabled(false);
        
        histortySongs = mongoService.getSongsHistory(user.getUsername());
                
        songsHistoryGrid.setItems(histortySongs);
    }

    /**
     * פעולה המנקה את טופס המשתמש
     */
    private void clearUserForm()
    {
        username.clear();
        username.setReadOnly(false);
        
        type.clear();
        
        password.clear();
        
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnAdd.setEnabled(true);
        
        histortySongs.clear();
        songsHistoryGrid.setItems(histortySongs);
    }

    /**
     * פעולה המוסיפה את המשתמש אל מסד הנתונים
     */
    private void addUserToDB()
    {
        ArrayList<Song> songsHis = new ArrayList<>();
        
        User user = new User(username.getValue(),
                password.getValue(),
                UserTypeService.convertStringToType(type.getValue()),
                new ArrayList<>());
        
        user.setSongs(songsHis);

        
        if (mongoService.addUser(user))
        {
            Notification.show(Alerts.USER_ADDED_SUCCESSFULLY, 4000, Notification.Position.MIDDLE);
            refreshGrid();
            clearUserForm();
        }
        else
        {
            Notification.show(Alerts.USER_NOT_ADDED, 4000, Notification.Position.MIDDLE);
            System.out.println(">>>> " + "User NOT Added!");
        }
    }

    
    /**
     * פעולה המעדכנת את המשתמש במסד הנתונים
     */
    private void updateUserInDB() 
    {
       
        if (mongoService.updateUser(username.getValue(), password.getValue(), UserTypeService.convertStringToType(type.getValue()), histortySongs))
        {
            Notification.show(Alerts.USER_UPDATED_SUCCESSFULLY, 4000, Notification.Position.MIDDLE);
            refreshGrid();
        }
        else
        {
            Notification.show(Alerts.USER_NOT_UPDATED, 4000, Notification.Position.MIDDLE);
            System.out.println(">>>> " + "User NOT updated!");
        }
    }

    /**
     * פעולה המוחקת את המשתמש ממסד הנתונים
     */
    private void deleteUserFromDB()
    {
        if (mongoService.deleteUser(username.getValue()))
        {
            Notification.show(Alerts.USER_DELETED_SUCCESSFULLY, 4000, Notification.Position.MIDDLE);
            refreshGrid();
            clearUserForm();
        }
        else
        {
            Notification.show(Alerts.USER_NOT_DELETED, 4000, Notification.Position.MIDDLE);
            System.out.println(">>>> " + "User NOT deleted!");
        }
    }
    
    /**
     * פעולה המרעננת את גריד המשתמשים
     */
    private void refreshGrid()
    {
        grid.setItems(mongoService.getAllUsers());
    }
    
    /**
     * פעולה המחפשת משתמש במסד הנתונים
     * @param searchStr שם המשתמש
     * @param key המקש במקלדת
     */
    private void search(String searchStr, Key key)
    {
        ArrayList<User> usersFilter = (ArrayList<User>) mongoService.findByUsernameStartsWith(searchStr);
         
        if (usersFilter.isEmpty() && key == Key.ENTER)
            Notification.show(Alerts.NOT_FOUND, 4000, Notification.Position.MIDDLE);
        
        grid.setItems(usersFilter);
    }
    
    
    /**
     * פעולה הבודקת את הגישה לדף
     * @return אמת במידה ולמשתמש יש גישה אחרת שקר
     */
    private boolean checkAccess()
    {
        String userLogedin = Cookies.getCookie(Cookies.USER_OK);        
        String isManager = Cookies.getCookie(Cookies.MANAGER);
        
        if (userLogedin == null)
        {
            UI.getCurrent().getPage().setLocation(Routes.LOGIN_PAGE);
            return false;
        }
        
        if (isManager == null)
        {
            Notification.show(Alerts.ACCESS_DENIED, 8000, Notification.Position.MIDDLE);
            UI.getCurrent().getPage().setLocation(Routes.MAIN_PAGE);
            this.isManager = false;
            return false;
        }
        else
            this.isManager = true;
        
        return true;
    }
}
