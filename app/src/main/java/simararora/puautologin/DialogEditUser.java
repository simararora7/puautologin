package simararora.puautologin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Simar Arora on 2/9/2015.
 */
public class DialogEditUser extends DialogFragment implements View.OnClickListener{

    private EditText username, password;
    private Button edit, cancel;
    private String user, pass, oldUser;
    private EditDialogCommunicator communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        oldUser = bundle.getString("username");
        pass = bundle.getString("password");
        View view = inflater.inflate(R.layout.dialog_edit_user, null);
        username = (EditText) view.findViewById(R.id.etUsername);
        password = (EditText) view.findViewById(R.id.etPassword);
        username.setText(oldUser);
        password.setText(pass);
        edit = (Button) view.findViewById(R.id.bEdit);
        cancel = (Button) view.findViewById(R.id.bCancel);
        edit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        return view;
    }

    public void initialiseCommunicator(DialogOptions dialogOptions){
        communicator = (EditDialogCommunicator) dialogOptions;
    }

    public static DialogEditUser newInstance(String username, String password, DialogOptions dialogOptions){
        DialogEditUser dialog = new DialogEditUser();
        dialog.initialiseCommunicator(dialogOptions);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bEdit:
                edit();
                break;
            case R.id.bCancel:
                communicator.onEditAbort();
                dismiss();
                break;
        }

    }

    private void edit() {
        user = username.getText().toString();
        if(user.isEmpty()){
            username.setError("Username can't be left empty.");
            return;
        }
        pass = password.getText().toString();
        if(pass.isEmpty()){
            password.setError("Password can't be left empty");
            return;
        }
        UserDatabase userDatabase = new UserDatabase(getActivity());
        userDatabase.open();
        userDatabase.editUser(oldUser, user, pass);
        userDatabase.close();
        communicator.onEditSuccessful(oldUser, user);
        dismiss();
    }

    interface EditDialogCommunicator{
        public void onEditSuccessful(String oldUsername, String newUsername);
        public void onEditAbort();
    }
}
