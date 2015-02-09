package simararora.puautologin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Simar Arora on 2/9/2015.
 *
 */
public class DialogOptions extends DialogFragment implements AdapterView.OnItemClickListener, DialogEditUser.EditDialogCommunicator{

    private String username, password;
    private ListView listview;
    private OptionsDialogCommunicator communicator;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        communicator = (OptionsDialogCommunicator) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        username = getArguments().getString("username", "");
        getDialog().setTitle(username);
        View view = inflater.inflate(R.layout.dialog_options, null);
        listview = (ListView) view.findViewById(R.id.lvOptions);
        listview.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[]{
                "Set Default", "Edit", "Delete"
        }));
        listview.setOnItemClickListener(this);
        return view;
    }

    public static DialogOptions newInstance(String username){
        DialogOptions dialog = new DialogOptions();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                setDefault();
                break;
            case 1:
                editUser();
                break;
            case 2:
                deleteUser();
                break;
        }
    }

    private void setDefault() {
        Functions.setActiveUser(getActivity(), username);
        communicator.onSetDefaultUser(username);
        dismiss();
    }

    private void editUser() {
        UserDatabase userDatabase = new UserDatabase(getActivity());
        userDatabase.open();
        password = userDatabase.getPasswordFromUserName(username);
        userDatabase.close();
        DialogEditUser.newInstance(username, password, this).show(getActivity().getSupportFragmentManager(), "EditDialog");
    }

    private void deleteUser() {
        UserDatabase userDatabase = new UserDatabase(getActivity());
        userDatabase.open();
        userDatabase.deleteUser(username);
        userDatabase.close();
        communicator.onDeleteUserConformation(username);
        dismiss();
    }

    @Override
    public void onEditSuccessful(String oldUsername, String newUsername) {
        communicator.onEditUserConformation(oldUsername, newUsername);
        dismiss();
    }

    @Override
    public void onEditAbort() {
        dismiss();
    }

    interface OptionsDialogCommunicator{
        public void onDeleteUserConformation(String username);
        public void onEditUserConformation(String oldUsername, String newUsername);
        public void onSetDefaultUser(String username);
    }
}
