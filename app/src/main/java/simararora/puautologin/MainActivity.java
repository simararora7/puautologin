package simararora.puautologin;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener, DialogAddUser.SendMessageToMainActivity, DialogOptions.OptionsDialogCommunicator {

    private UserListAdapter adapter;
    private ArrayList<String> users;
    protected TextView noUserAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));

        findViewById(R.id.bLogin).setOnClickListener(this);
        findViewById(R.id.bLogout).setOnClickListener(this);

        UserDatabase userDatabase = new UserDatabase(this);
        userDatabase.open();
        ListView listOfUsers = (ListView) findViewById(R.id.listUsers);
        users = userDatabase.getAllUsers();
        adapter = new UserListAdapter();
        listOfUsers.setAdapter(adapter);
        userDatabase.close();
        listOfUsers.setOnItemClickListener(this);
        noUserAdded = (TextView) findViewById(R.id.etNoUserAdded);
        if (users.isEmpty())
            noUserAdded.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_user) {
            new DialogAddUser().show(getSupportFragmentManager(), "DialogAddUser");
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DialogOptions.newInstance(users.get(position)).show(getSupportFragmentManager(), "Options");
    }

    @Override
    public void dialogToActivity(String user) {
        users.add(user);
        //adapter.add(user);
        adapter.notifyDataSetChanged();
        noUserAdded.setVisibility(View.GONE);
    }

    @Override
    public void onDeleteUserConformation(String username) {
        users.remove(username);
        if (username.isEmpty()) {
            Functions.disable(this);
        }
        //adapter.remove(username);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditUserConformation(String oldUsername, String newUsername) {
        users.remove(oldUsername);
        //adapter.remove(oldUsername);
        users.add(newUsername);
        //adapter.add(newUsername);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Edit Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetDefaultUser(String username) {
        Toast.makeText(this, "Default User Changed", Toast.LENGTH_SHORT).show();
    }

    private class UserListAdapter extends BaseAdapter{

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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.user_row, parent, false);
            ImageView defaultImageView = (ImageView) convertView.findViewById(R.id.ivDefault);
            TextView user = (TextView) convertView.findViewById(R.id.tvUser);
            user.setText(users.get(position));
            return null;
        }
    }
}