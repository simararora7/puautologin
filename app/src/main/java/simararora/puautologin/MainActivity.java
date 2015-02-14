package simararora.puautologin;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));

        findViewById(R.id.bLogin).setOnClickListener(this);
        findViewById(R.id.bLogout).setOnClickListener(this);

        UserDatabase userDatabase = new UserDatabase(this);
        userDatabase.open();
        listOfUsers = (ListView) findViewById(R.id.listUsers);
        users = userDatabase.getAllUsers();
        UserListAdapter adapter = new UserListAdapter();
        listOfUsers.setAdapter(adapter);
        userDatabase.close();
        noUserAdded = (TextView) findViewById(R.id.etNoUserAdded);
        selectDefaultUser = (TextView) findViewById(R.id.etSelectDefaultUser);
        if (users.isEmpty()) {
            noUserAdded.setVisibility(View.VISIBLE);
            selectDefaultUser.setVisibility(View.GONE);
        }
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogin:
                new LoginTask(MainActivity.this).execute();
                break;
            case R.id.bLogout:
                new LogoutTask(MainActivity.this).execute();
                break;
        }
    }


    public void onSuccessfulAddUser(String user) {
        users.add(user);
        listOfUsers.setAdapter(new UserListAdapter());
        noUserAdded.setVisibility(View.GONE);
        selectDefaultUser.setVisibility(View.VISIBLE);
    }

    public void onDeleteUserConformation(String username) {
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

    public void onEditUserConformation(String oldUsername, String newUsername) {
        users.remove(oldUsername);
        users.add(newUsername);
        Functions.setActiveUser(this, newUsername);
        listOfUsers.setAdapter(new UserListAdapter());
        Toast.makeText(this, "Edit Successful", Toast.LENGTH_SHORT).show();
    }

    private void showAddDialog() {
        View view;
        final EditText username, password;
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("Add User");
        view = getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        username = (EditText) view.findViewById(R.id.etUsername);
        password = (EditText) view.findViewById(R.id.etPassword);
        builder.setView(view);
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
        builder.setCancelable(false);
        builder.create().show();
    }

    private void showOptionsDialog(final String username) {
        View view = getLayoutInflater().inflate(R.layout.dialog_options, null);
        ListView optionsList = (ListView) view.findViewById(R.id.lvOptions);
        final AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        optionsList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{
                "Edit", "Delete", "Change Password"
        }));
        builder.setView(view);
        final AlertDialog dialog = builder.create();
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
                    case 2:
                        Toast.makeText(MainActivity.this, "Functionality yet to be added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

    private void showEditDialog(final String oldUser) {
        View view;
        final EditText username, password;
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("Edit User");
        view = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        username = (EditText) view.findViewById(R.id.etUsername);
        password = (EditText) view.findViewById(R.id.etPassword);
        username.setText(oldUser);
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
        builder.setCancelable(false);
        builder.create().show();
    }

    private void showAboutDialog() {
//        final MaterialDialog dialog = new MaterialDialog(this);
//        dialog.setTitle("About");
//        dialog.setMessage("This application is created by Simarpreet Singh Arora, CSE, 3rd Year, UIET.");
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.setNegativeButton("Back", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setPositiveButton("OK", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("About");
        builder.setMessage("This application is created by Simarpreet Singh Arora, CSE, 3rd Year, UIET.");
        builder.setCancelable(true);
        builder.setPositiveButton("Back", null);
        builder.create().show();
    }

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

    private void openPortal() {
        Uri uri = Uri.parse("https://securelogin.arubanetworks.com/cgi-bin/login?cmd=login");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Web Browser Found", Toast.LENGTH_SHORT).show();
        }

    }

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


    private class UserListAdapter extends BaseAdapter {

        private ArrayList<CheckBox> checkBoxes;
        private String activeUser;
        private int flag;

        public UserListAdapter() {
            checkBoxes = new ArrayList<>();
            activeUser = Functions.getActiveUserName(MainActivity.this);
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

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