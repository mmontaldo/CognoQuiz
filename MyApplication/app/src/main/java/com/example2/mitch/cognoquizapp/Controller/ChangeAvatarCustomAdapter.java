package com.example2.mitch.cognoquizapp.Controller;

/**
 * Created by Mitch on 8/20/2015.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example2.mitch.cognoquizapp.R;
import com.example2.mitch.cognoquizapp.View.PlayerProfileActivity;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


/**
 * Created by Mitch on 4/2/2015.
 * CustomAdapter class to be used by child food list and goal list
 */
public class ChangeAvatarCustomAdapter extends BaseAdapter {

    String [] result;
    Context context;
    int [] imageId;
    int [] smallImageId;
    private static LayoutInflater inflater=null;

    public ChangeAvatarCustomAdapter(Activity mainActivity, String[] charNameList, int[] charImages, int[] smallImages) {

        result = charNameList;
        context = mainActivity;
        imageId = charImages;
        smallImageId = smallImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        final View rowView;

        rowView = inflater.inflate(R.layout.avatar_grid_element, null);
        holder.tv=(TextView) rowView.findViewById(R.id.avatarTxt);
        holder.img=(ImageView) rowView.findViewById(R.id.avatarImg);

        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle(result[position])
                        .setMessage("Choose this character?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updateAvatar(position);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.guy)
                        .setIcon(smallImageId[position])
                        .show();

            }
        });

        return rowView;
    }

    public void updateAvatar(int i) {
        //Query to get user information
        ParseUser currentUser = ParseUser.getCurrentUser();

        String avatarTemp = "";

        if (result[i].toLowerCase().equals("gemini twins")){
            avatarTemp = "R.drawable.gemini";
        } else {
            avatarTemp = "R.drawable." + result[i].toLowerCase();
        }

        final String avatar = avatarTemp;

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject u, com.parse.ParseException e) {
                if (u == null) {
                    Log.d("score", "The getFirst request failed.");
                } else {
                    u.put("avatar_name", avatar);
                    u.saveInBackground();
                }
            }
        });

        context.startActivity(new Intent(context, PlayerProfileActivity.class));
    }

}