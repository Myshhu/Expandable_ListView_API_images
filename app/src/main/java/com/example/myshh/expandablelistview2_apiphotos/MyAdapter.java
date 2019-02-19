package com.example.myshh.expandablelistview2_apiphotos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class MyAdapter extends BaseExpandableListAdapter {

    private Context context;
    private JSONArray jsonArray;
    private List<Integer> titles;

    public MyAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public int getGroupCount() {
        //System.out.println("Get group count");
        if(this.jsonArray != null) {
            try {
                Set<Integer> uniqueSet = new HashSet<>();
                titles = new ArrayList<>();
                for (int i = 0; i < this.jsonArray.length(); i++) {
                    uniqueSet.add(Integer.parseInt(this.jsonArray.getJSONObject(i).get("albumId").toString()));
                }
                System.out.println("Group count is: " + uniqueSet.size());
                titles.addAll(uniqueSet);
                Collections.sort(titles);
                return uniqueSet.size();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        System.out.println("Get children count");
        try {
            int count = 0;
            for(int i = 0; i < this.jsonArray.length(); i++) {
                int temp = Integer.parseInt(this.jsonArray.getJSONObject(i).get("albumId").toString());
                //System.out.println("Temp is: " + temp + " == " + groupPosition + " groupPosition");
                if(temp == groupPosition + 1) {
                    count++;
                }
            }
            System.out.println(groupPosition + "Children count is: " + count);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        System.out.println("Try get group");
        try {
            System.out.println("Get group: " + this.jsonArray.get(groupPosition));
            return titles.get(groupPosition);
            //return this.jsonArray.get(groupPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        System.out.println("Try get child with childPosition: " + childPosition);
        List<JSONObject> photosInAlbum = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (Integer.parseInt(jsonArray.getJSONObject(i).get("albumId").toString()) == groupPosition + 1) {
                    photosInAlbum.add(jsonArray.getJSONObject(i));
                }
            }
            return photosInAlbum.get(childPosition);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        /*try {
            System.out.println("Get child: " + this.jsonArray.getJSONObject(groupPosition));
            return this.jsonArray.getJSONObject(groupPosition * 10 + childPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        //System.out.println("Get group view: " + getGroup(groupPosition));
        //String listTitle = Integer.toString(groupPosition);
        String listTitle = titles.get(groupPosition).toString();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTV = convertView.findViewById(R.id.listTitle);
        listTitleTV.setText(listTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        System.out.println("Get child view: " + getChild(groupPosition, childPosition));
        String stringAlbumID = null;
        String stringID = null;
        String stringTitle = null;
        String stringUrl = null;
        String stringThumbnailUrl = null;
        try {
            //Get text values from JSON
            stringAlbumID = ((JSONObject) getChild(groupPosition, childPosition)).getString("albumId");
            stringID = ((JSONObject) getChild(groupPosition, childPosition)).getString("id");
            stringTitle = ((JSONObject) getChild(groupPosition, childPosition)).getString("title");
            stringUrl = ((JSONObject) getChild(groupPosition, childPosition)).getString("url");
            stringThumbnailUrl = ((JSONObject) getChild(groupPosition, childPosition)).getString("thumbnailUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        //Setting text values
        TextView albumId = convertView.findViewById(R.id.albumId);
        albumId.setText(stringAlbumID);
        TextView id = convertView.findViewById(R.id.id);
        id.setText(stringID);
        TextView title = convertView.findViewById(R.id.title);
        title.setText(stringTitle);
        TextView url = convertView.findViewById(R.id.url);
        url.setText(stringUrl);
        TextView tnUrl = convertView.findViewById(R.id.thumbnailUrl);
        tnUrl.setText(stringThumbnailUrl);

        //Setting image
        ImageView imageView = convertView.findViewById(R.id.image);
        new Thread(() -> {
            try {
                //Load image from URL
                Bitmap bitmap = BitmapFactory.decodeStream(
                        (InputStream)new URL(((JSONObject) getChild(groupPosition, childPosition)).
                                getString("url")).getContent());
                //Set image to imageView
                ((MainActivity)context).runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
