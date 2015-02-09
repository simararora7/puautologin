package simararora.puautologin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Simar Arora on 2/5/2015.
 *
 */
public class DialogAddUser extends DialogFragment implements View.OnClickListener{

    private EditText username, password;
    private Button add, cancel;
    private SendMessageToMainActivity messageToMainActivity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageToMainActivity = (SendMessageToMainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_user, null);
        username = (EditText) view.findViewById(R.id.etUsername);
        password = (EditText) view.findViewById(R.id.etPassword);
        cancel = (Button) view.findViewById(R.id.bCancel);
        add = (Button) view.findViewById(R.id.bAdd);
        cancel.setOnClickListener(this);
        add.setOnClickListener(this);
        //TODO Dialog gets cancelled
        getDialog().setTitle("Add User");
        getDialog().setCancelable(false);
        this.setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bCancel:
                dismiss();
                break;
            case R.id.bAdd:
                addUser();
                break;
        }
    }

    private void addUser() {
        String user, pass;
        user = username.getText().toString();
        pass = password.getText().toString();
        if(user.isEmpty()){
            username.setError("Username can't be left empty.");
            return;
        }
        if(pass.isEmpty()){
            password.setError("Password can't be left empty.");
            return;
        }
        UserDatabase userDatabase = new UserDatabase(getActivity());
        userDatabase.addUser(user, pass);
        if(userDatabase.getAllUsers().isEmpty()){
            Functions.initialise(getActivity());
            Functions.setActiveUser(getActivity(), user);
        }
        userDatabase.close();
        Toast.makeText(getActivity(), "User Added Successfully", Toast.LENGTH_SHORT).show();
        messageToMainActivity.dialogToActivity(user);
        dismiss();
    }

    interface SendMessageToMainActivity{
        public void dialogToActivity(String user);
    }
}
