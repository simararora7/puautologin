package simararora.puautologin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Simar Arora on 2/10/2015.
 *
 */
public class DialogChangePassword extends DialogFragment implements View.OnClickListener, ChangePasswordTask.ChangePasswordTaskCompletionCommunicator{
    private EditText password, confirmPassword;
    private Button cancel, confirm;
    private CommunicatorChangePassword communicator;
    private ProgressDialog progressDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        communicator = (CommunicatorChangePassword) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_password, null);
        password = (EditText) view.findViewById(R.id.etPassword);
        confirmPassword = (EditText) view.findViewById(R.id.etConfirmPassword);
        cancel = (Button) view.findViewById(R.id.bCancel);
        confirm = (Button) view.findViewById(R.id.bConfirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Changing Password");
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bCancel:
                communicator.onChangePasswordAbort();
                dismiss();
                break;
            case R.id.bConfirm:
                changePassword();
                break;
        }
    }

    private void changePassword() {
        progressDialog.show();
        new ChangePasswordTask(this).execute();
    }

    @Override
    public void onSuccess() {
        progressDialog.dismiss();
        communicator.onSuccessfulChangePassword();
        dismiss();
    }

    @Override
    public void onFailure() {
        progressDialog.dismiss();
        communicator.onChangePasswordAbort();
        dismiss();
    }

    interface CommunicatorChangePassword{
        public void onChangePasswordAbort();
        public void onSuccessfulChangePassword();
    }
}
