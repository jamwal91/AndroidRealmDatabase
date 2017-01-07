package com.mahesh.realm.androidrealmdatabase.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mahesh.realm.androidrealmdatabase.R;
import com.mahesh.realm.androidrealmdatabase.adatpers.PersonAdapter;
import com.mahesh.realm.androidrealmdatabase.application.RealmApplication;
import com.mahesh.realm.androidrealmdatabase.definition.AppConstants;
import com.mahesh.realm.androidrealmdatabase.model.Person;
import com.mahesh.realm.androidrealmdatabase.swipe.util.Attributes;

import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private FloatingActionButton fab;
    private RecyclerView personData;

    private RealmResults<Person> person;
    private PersonAdapter adapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findIds();
        setListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        showProgressDialog();
        person = RealmApplication.getInstance().getRealm().where(Person.class).findAll();
        adapter = new PersonAdapter(this, person);
        adapter.setMode(Attributes.Mode.Single);
        personData.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        personData.setItemAnimator(new DefaultItemAnimator());
        personData.setAdapter(adapter);
        hideProgressDialog();
    }

    private void setListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddUserDetailsActivity.class).putExtra(AppConstants.USER_DETAIL_REQUEST, AppConstants.ADD_DETAIL));
            }
        });
    }

    private void findIds() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        personData = (RecyclerView) findViewById(R.id.rv_person_data);
    }

    public void updateDetails(int position) {
        startActivity(new Intent(MainActivity.this, AddUserDetailsActivity.class)
                .putExtra(AppConstants.USER_DETAIL_REQUEST, AppConstants.EDIT_DETAIL)
                .putExtra(AppConstants.USER_DETAIL, person.get(position)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealmApplication.getInstance().getRealm().close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //TODO: Reset your views
                    Log.i("DJ Application", "onClose()");
                    return false;
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false; //do the default
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    //NOTE: doing anything here is optional, onNewIntent is the important bit
                    if (s.length() > 1) { //2 chars or more
                        person = searchPerson(s);
                        adapter.notifyDatasetChanged();
                    }

                    if (s.length() == 0) {
                        updateList();
                    }
                    return false;
                }

            });
        }

        return true;
    }

    public RealmResults<Person> searchPerson(String name) {
        RealmResults<Person> results = RealmApplication.getInstance().getRealm().where(Person.class).equalTo("name", name).findAll();

        RealmApplication.getInstance().getRealm().beginTransaction();
        RealmApplication.getInstance().getRealm().commitTransaction();

        return results;
    }


    private void updateList() {
        person = RealmApplication.getInstance().getRealm().where(Person.class).findAll();
        adapter.notifyDatasetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings Clicked!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
