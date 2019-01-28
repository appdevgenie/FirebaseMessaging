package com.appdevgenie.firebasemessaging.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appdevgenie.firebasemessaging.Models.User;
import com.appdevgenie.firebasemessaging.R;
import com.appdevgenie.firebasemessaging.UserListActivity;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private static final String TAG = "EmployeesAdapter";

    private ArrayList<User> mUsers;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        private TextView token;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvUserName);
            token = itemView.findViewById(R.id.tvUserToken);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: selected employee: " + mUsers.get(getAdapterPosition()));

                    //open a dialog for inserting a message into the database
                    ((UserListActivity)mContext).openMessageDialog(mUsers.get(getAdapterPosition()).getUser_id());
                }
            });*/
        }
    }

    public UserAdapter(Context context, ArrayList<User> users) {
        mUsers = users;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the custom layout
        View view = inflater.inflate(R.layout.listitem_user, parent, false);

        //return a new holder instance
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) throws NullPointerException{
        holder.name.setText(mUsers.get(position).getName());
        holder.token.setText(mUsers.get(position).getUser_id());
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}

