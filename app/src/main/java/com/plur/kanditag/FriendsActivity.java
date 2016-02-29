package com.plur.kanditag;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Jim on 2/25/16.
 */
public class FriendsActivity extends Activity {

    private ListView mListView;
    private ArrayAdapter<Person> users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initXml();
    }

    private void initXml() {
        mListView = (ListView) findViewById(R.id.listview);
    }
}
