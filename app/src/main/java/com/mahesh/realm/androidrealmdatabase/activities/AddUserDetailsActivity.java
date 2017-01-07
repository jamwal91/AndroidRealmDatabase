package com.mahesh.realm.androidrealmdatabase.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mahesh.realm.androidrealmdatabase.R;
import com.mahesh.realm.androidrealmdatabase.application.RealmApplication;
import com.mahesh.realm.androidrealmdatabase.definition.AppConstants;
import com.mahesh.realm.androidrealmdatabase.model.Person;

import io.realm.Realm;
import io.realm.Realm.Transaction;

public class AddUserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name, et_age, et_location, et_designation;

    private Button btn_save;

    private Boolean isEditable = false;
    private Person person;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_details);

        setupActionBar();
        findIds();
        getIntentData();
        setListeners();
    }

    private void getIntentData() {
        if (getIntent().getExtras() != null) {
            switch (getIntent().getExtras().getInt(AppConstants.USER_DETAIL_REQUEST)) {
                case AppConstants.ADD_DETAIL:
                    break;
                case AppConstants.EDIT_DETAIL:
                    setData((Person) getIntent().getParcelableExtra(AppConstants.USER_DETAIL));
                    break;
                default:
                    break;
            }
        }
    }

    private void setListeners() {
        btn_save.setOnClickListener(this);
    }

    private void setData(Person person) {
        this.person = person;
        et_name.setText(person.getName().trim());
        et_age.setText(String.valueOf(person.getAge()));
        et_location.setText(person.getLocation().trim());
        et_designation.setText(person.getDesignation().trim());

        et_name.setSelection(person.getName().length());
        btn_save.setText(getString(R.string.btn_val_edit));

        isEditable = true;
    }

    private void findIds() {
        et_name = (EditText) findViewById(R.id.et_enter_name);
        et_age = (EditText) findViewById(R.id.et_enter_age);
        et_location = (EditText) findViewById(R.id.et_enter_location);
        et_designation = (EditText) findViewById(R.id.et_enter_designation);

        btn_save = (Button) findViewById(R.id.btn_save);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                if (validateFields()) {
                    if (isEditable)
                        updateData();
                    else
                        saveData();
                } else {
                    Toast.makeText(this, "Please fill all fields...!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void saveData() {
        showProgressDialog();
        RealmApplication.getInstance().getRealm().executeTransactionAsync(new Transaction() {
            @Override
            public void execute(Realm realm) {

                Person person = realm.createObject(Person.class, System.currentTimeMillis());
                person.setName(et_name.getText().toString().trim());
                person.setAge(Integer.parseInt(et_age.getText().toString().trim()));
                person.setDesignation(et_designation.getText().toString().trim());
                person.setLocation(et_location.getText().toString().trim());
            }
        }, new Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                hideProgressDialog();
                Toast.makeText(AddUserDetailsActivity.this, "onSuccess()", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                hideProgressDialog();
                error.printStackTrace();
                Toast.makeText(AddUserDetailsActivity.this, "onError(Throwable error)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData() {
        showProgressDialog();
        RealmApplication.getInstance().getRealm().executeTransactionAsync(new Transaction() {
            @Override
            public void execute(Realm realm) {
                person.setName(et_name.getText().toString().trim());
                person.setAge(Integer.parseInt(et_age.getText().toString().trim()));
                person.setDesignation(et_designation.getText().toString().trim());
                person.setLocation(et_location.getText().toString().trim());
                realm.copyToRealmOrUpdate(person);
            }
        }, new Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                hideProgressDialog();
                Toast.makeText(AddUserDetailsActivity.this, "onSuccess()", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                hideProgressDialog();
                error.printStackTrace();
                Toast.makeText(AddUserDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean validateFields() {
        Boolean status = true;

        if ((et_name.getText().length() <= 0)) {
            status = false;
        }

        if ((et_age.getText().length() <= 0)) {
            status = false;
        }

        if ((et_location.getText().length() <= 0)) {
            status = false;
        }

        if ((et_designation.getText().length() <= 0)) {
            status = false;
        }

        return status;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null || (progressDialog != null ? progressDialog.isShowing() : false)) {
            progressDialog.dismiss();
        }
    }
}
