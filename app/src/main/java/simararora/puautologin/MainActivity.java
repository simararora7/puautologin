package simararora.puautologin;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private ArrayList<String> users;
    private TextView noUserAdded, selectDefaultUser;
    private ListView listOfUsers;
    private boolean doubleBackToExitPressedOnce;
    private LinearLayout listViewLayout;
    private Button loginButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Toolbar
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));

        //Initialise login and logout buttons
        loginButton = (Button) findViewById(R.id.bLogin);
        loginButton.setOnClickListener(this);
        logoutButton = (Button) findViewById(R.id.bLogout);
        logoutButton.setOnClickListener(this);

        //Get users from database
        UserDatabase userDatabase = new UserDatabase(this);
        userDatabase.open();
        users = userDatabase.getAllUsers();
        userDatabase.close();

        //Initialise list and its adapter
        listOfUsers = (ListView) findViewById(R.id.listUsers);
        UserListAdapter adapter = new UserListAdapter();
        listOfUsers.setAdapter(adapter);

        //Check if there are no users and display appropriate message
        noUserAdded = (TextView) findViewById(R.id.etNoUserAdded);
        selectDefaultUser = (TextView) findViewById(R.id.etSelectDefaultUser);
        if (users.isEmpty()) {
            noUserAdded.setVisibility(View.VISIBLE);
            selectDefaultUser.setVisibility(View.GONE);
        }

        //Initialise Floating Button
        ImageButton addUser = (ImageButton) findViewById(R.id.bAdd);
        addUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        addUser.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Add User", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        loginButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left));
        loginButton.setVisibility(View.VISIBLE);

        logoutButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right));
        logoutButton.setVisibility(View.VISIBLE);

        listViewLayout = (LinearLayout) findViewById(R.id.llListLayout);
        listViewLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        //listViewLayout.setVisibility(View.VISIBLE);
        addUser.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left));
    }

    /**
     * @param v Handles Button Clicks
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogin:
                //Start Login Task
                new LoginTask(MainActivity.this, false).execute();
                break;
            case R.id.bLogout:
                //Start Logout Task
                new LogoutTask(MainActivity.this).execute();
                break;
        }
    }

    /**
     * Shows Add User Dialog
     * Takes input from User
     * Add information to database
     */
    private void showAddDialog() {
        View view;
        final EditText username, password;
        final CheckBox showPassword;
        //Create Dialog Builder
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("Add User");

        //Inflate view for Dialog
        view = getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        username = (EditText) view.findViewById(R.id.etUsername);
        password = (EditText) view.findViewById(R.id.etPassword);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        showPassword = (CheckBox) view.findViewById(R.id.cbShowPassword);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = showPassword.isChecked();
                if (checked) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setSelection(password.length());
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setSelection(password.length());
                }
            }
        });
        builder.setView(view);

        //Set Buttons for Dialog
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user, pass;
                user = username.getText().toString();
                pass = password.getText().toString();
                if (user.isEmpty()) {
                    username.setError("Username can't be left empty.");
                    return;
                }
                if (pass.isEmpty()) {
                    password.setError("Password can't be left empty.");
                    return;
                }
                UserDatabase userDatabase = new UserDatabase(MainActivity.this);
                userDatabase.open();
                if (userDatabase.getAllUsers().isEmpty()) {
                    Functions.initialise(MainActivity.this);
                    Functions.setActiveUser(MainActivity.this, user);
                }
                userDatabase.addUser(user, pass);
                userDatabase.close();
                Toast.makeText(MainActivity.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                MainActivity.this.onSuccessfulAddUser(user);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //Show Dialog
        AlertDialogPro dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }

    /**
     * @param user Updates the list after user has been added successfully
     */
    private void onSuccessfulAddUser(String user) {
        users.add(user);
        listOfUsers.setAdapter(new UserListAdapter());
        noUserAdded.setVisibility(View.GONE);
        selectDefaultUser.setVisibility(View.VISIBLE);
    }

    /**
     * @param username Shows options dialog
     *                 Edit
     *                 Delete
     *                 Change Password
     */
    private void showOptionsDialog(final String username) {
        View view = getLayoutInflater().inflate(R.layout.dialog_options, null);
        ListView optionsList = (ListView) view.findViewById(R.id.lvOptions);
        final AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        optionsList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{
                "Edit", "Delete"
        }));
        builder.setView(view);
        final AlertDialogPro dialog = builder.create();
        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        dialog.dismiss();
                        showEditDialog(username);
                        break;
                    case 1:
                        UserDatabase userDatabase = new UserDatabase(MainActivity.this);
                        userDatabase.open();
                        userDatabase.deleteUser(username);
                        userDatabase.close();
                        MainActivity.this.onDeleteUserConformation(username);
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }

    /**
     * @param username Updates the list after user has been deleted successfully
     */
    private void onDeleteUserConformation(String username) {
        users.remove(username);
        if (users.isEmpty()) {
            Functions.disable(this);
            noUserAdded.setVisibility(View.VISIBLE);
            selectDefaultUser.setVisibility(View.GONE);
        } else {
            if (Functions.getActiveUserName(this).equals(username))
                Functions.setActiveUser(this, users.get(0));
        }
        listOfUsers.setAdapter(new UserListAdapter());
        Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param oldUser Shows Edit Dialog
     */
    private void showEditDialog(final String oldUser) {
        View view;
        final EditText username, password;
        final CheckBox showPassword;
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("Edit User");
        view = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        username = (EditText) view.findViewById(R.id.etUsername);
        password = (EditText) view.findViewById(R.id.etPassword);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        username.setText(oldUser);
        showPassword = (CheckBox) view.findViewById(R.id.cbShowPassword);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = showPassword.isChecked();
                if (checked) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setSelection(password.length());
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setSelection(password.length());
                }
            }
        });
        builder.setView(view);
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = username.getText().toString();
                if (newUsername.isEmpty()) {
                    return;
                }
                UserDatabase userDatabase = new UserDatabase(MainActivity.this);
                userDatabase.open();
                String pwd = password.getText().toString();
                if (pwd.isEmpty()) {
                    pwd = userDatabase.getPasswordFromUserName(oldUser);
                }
                userDatabase.editUser(oldUser, newUsername, pwd);
                userDatabase.close();
                MainActivity.this.onEditUserConformation(oldUser, newUsername);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialogPro dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }

    /**
     * @param oldUsername
     * @param newUsername Updates list after user has been edited successfully
     */
    private void onEditUserConformation(String oldUsername, String newUsername) {
        int x = users.indexOf(oldUsername);
        users.remove(oldUsername);
        users.add(x, newUsername);
        if (Functions.getActiveUserName(this).equals(oldUsername))
            Functions.setActiveUser(this, newUsername);
        listOfUsers.setAdapter(new UserListAdapter());
        Toast.makeText(this, "Edit Successful", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param menu
     * @return Creates the menu for this activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * @param item
     * @return Handles Menu Item Clicks
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog();
                break;
            case R.id.action_feedback:
                sendFeedback();
                break;
            case R.id.action_open_portal:
                openPortal();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows About Dialog
     */
    private void showAboutDialog() {
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("About");
        builder.setMessage("This application is developed by Simarpreet Singh Arora, CSE, 3rd Year, UIET.\nIcons Designed By Naveen Singh, CSE, 3rd Year, UIET.");
        builder.setCancelable(true);
        builder.setPositiveButton("Back", null);
        AlertDialogPro dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }

    /**
     * Opens Email App for sending feedback
     */
    private void sendFeedback() {
        String[] email = new String[]{"simarpreetsingharora@gmail.com"};
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, email);
        emailIntent.setType("plain/text");
        try {
            startActivityForResult(emailIntent, 0);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Email App Found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens Browser for option portal site
     */
    private void openPortal() {
        Uri uri = Uri.parse("https://securelogin.arubanetworks.com/cgi-bin/login?cmd=login");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Web Browser Found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles Back Button Press
     * Press Back Again to Exit
     */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK Again to Exit", Toast.LENGTH_SHORT)
                .show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    /**
     * Custom Adapter for list of Users
     */
    private class UserListAdapter extends BaseAdapter {

        private ArrayList<CheckBox> checkBoxes;
        private String activeUser;
        private int flag;

        public UserListAdapter() {
            checkBoxes = new ArrayList<>();
            activeUser = Functions.getActiveUserName(MainActivity.this);
        }

        /**
         * @return number of users
         */
        @Override
        public int getCount() {
            return users.size();
        }

        /**
         * @param position
         * @return user at the position
         */
        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        /**
         * @param position
         * @return id = position
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Creates view for single list item
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final String currentUser = users.get(position);
            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.user_row, parent, false);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cbActiveUser);
            TextView user = (TextView) convertView.findViewById(R.id.tvUser);
            user.setText(currentUser);
            if (activeUser.equals(currentUser))
                checkBox.setChecked(true);
            checkBoxes.add(checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        flag = position;
                        for (int i = 0; i < checkBoxes.size(); i++) {
                            if (i != position)
                                checkBoxes.get(i).setChecked(false);
                        }
                        checkBox.setChecked(true);
                        Functions.setActiveUser(MainActivity.this, users.get(position));
                    } else {
                        if (position == flag)
                            buttonView.setChecked(true);
                    }
                }
            });
            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.llRow);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(true);
                }
            });
            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showOptionsDialog(currentUser);
                    return true;
                }
            });
            return convertView;
        }
    }
}