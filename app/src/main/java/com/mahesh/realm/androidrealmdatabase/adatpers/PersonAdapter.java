package com.mahesh.realm.androidrealmdatabase.adatpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mahesh.realm.androidrealmdatabase.R;
import com.mahesh.realm.androidrealmdatabase.activities.AddUserDetailsActivity;
import com.mahesh.realm.androidrealmdatabase.activities.MainActivity;
import com.mahesh.realm.androidrealmdatabase.application.RealmApplication;
import com.mahesh.realm.androidrealmdatabase.model.Person;
import com.mahesh.realm.androidrealmdatabase.swipe.SwipeLayout;
import com.mahesh.realm.androidrealmdatabase.swipe.adapters.RecyclerSwipeAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by root on 30/12/16.
 */

@SuppressWarnings("ALL")
public class PersonAdapter extends RecyclerSwipeAdapter<PersonAdapter.ViewHolder> {

    private RealmResults<Person> personData;
    private Context context;
    private Realm realm;

    private long lastClickTime = 0L;

    public PersonAdapter(Context context, Realm realm, RealmResults<Person> personData) {
        this.context = context;
        this.personData = personData;
        this.realm = realm;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person_details, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(personData.get(position));
        mItemManger.bind(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return personData.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SwipeLayout swipeLayout;

        private TextView tv_name;
        private TextView tv_age;
        private TextView tv_desig;
        private TextView tv_loc;
        private ImageView iv_more;

        private ImageView iv_close;
        private ImageView iv_delete;
        private ImageView iv_edit;

        ViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_age = (TextView) itemView.findViewById(R.id.tv_age);
            tv_desig = (TextView) itemView.findViewById(R.id.tv_designation);
            tv_loc = (TextView) itemView.findViewById(R.id.tv_location);

            iv_more = (ImageView) itemView.findViewById(R.id.iv_more);

            iv_close = (ImageView) itemView.findViewById(R.id.iv_close);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);

            iv_more.setOnClickListener(this);
            iv_close.setOnClickListener(this);
            iv_delete.setOnClickListener(this);
            iv_edit.setOnClickListener(this);

            swipeLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    // return true to stop swipe or vice versa
                    return true;
                }
            });
        }

        private void bind(Person person) {
            tv_name.setText(context.getString(R.string.txt_name, person.getName()));
            tv_age.setText(context.getString(R.string.txt_age, String.valueOf(person.getAge())));
            tv_desig.setText(context.getString(R.string.txt_designation, person.getDesignation()));
            tv_loc.setText(context.getString(R.string.txt_location, person.getLocation()));
        }

        @Override
        public void onClick(View view) {
            if (System.currentTimeMillis() - lastClickTime > 1000) {
                lastClickTime = System.currentTimeMillis();
                switch (view.getId()) {
                    case R.id.iv_more:
                        swipeLayout.open(true, true);
                        break;
                    case R.id.iv_close:
                        swipeLayout.close(true, true);
                        break;
                    case R.id.iv_delete:
                        deleteData();
                        break;
                    case R.id.iv_edit:
                        swipeLayout.close(true, true);
                        ((MainActivity) context).updateDetails(getAdapterPosition());
                        break;
                    default:
                        break;
                }
            }
        }

        private void deleteData() {
            realm.beginTransaction();
            personData.get(getAdapterPosition()).deleteFromRealm();
            realm.commitTransaction();
            swipeLayout.close(true, true);
            notifyItemRemoved(getAdapterPosition());
        }
    }
}
