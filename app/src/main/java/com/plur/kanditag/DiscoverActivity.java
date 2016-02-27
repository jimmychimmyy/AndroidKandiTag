package com.plur.kanditag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.skyfishjy.library.RippleBackground;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jim on 2/24/16.
 */
public class DiscoverActivity extends Activity {

    private ImageView toSettings, toFriends, toBadges;

    private RippleBackground rippleBackground;
    private boolean isRippling;

    // TODO need android equivalent of DLGraphScene

    Map<Object, Object> nearbyPeers, peerNodes;
    ByteBuffer discoveryInfo;
    private int nextNodeIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        setupXML();

        initMaps();

    }

    /* initialize maps for nodes */
    private void initMaps() {
        // set next node index to 0
        nextNodeIndex = 0;

        // init maps
        nearbyPeers = new Map<Object, Object>() {
            @Override
            public void clear() {

            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @NonNull
            @Override
            public Set<Entry<Object, Object>> entrySet() {
                return null;
            }

            @Override
            public Object get(Object key) {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @NonNull
            @Override
            public Set<Object> keySet() {
                return null;
            }

            @Override
            public Object put(Object key, Object value) {
                return null;
            }

            @Override
            public void putAll(Map<?, ?> map) {

            }

            @Override
            public Object remove(Object key) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public Collection<Object> values() {
                return null;
            }
        };

        peerNodes = new Map<Object, Object>() {
            @Override
            public void clear() {

            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @NonNull
            @Override
            public Set<Entry<Object, Object>> entrySet() {
                return null;
            }

            @Override
            public Object get(Object key) {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @NonNull
            @Override
            public Set<Object> keySet() {
                return null;
            }

            @Override
            public Object put(Object key, Object value) {
                return null;
            }

            @Override
            public void putAll(Map<?, ?> map) {

            }

            @Override
            public Object remove(Object key) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public Collection<Object> values() {
                return null;
            }
        };

    }

    /* initialize xml */
    private void setupXML() {
        isRippling = false;
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        View view = (View) findViewById(R.id.image);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRippling) {
                    rippleBackground.stopRippleAnimation();
                    isRippling = false;
                } else {
                    rippleBackground.startRippleAnimation();
                    isRippling = true;
                }
            }
        });

        toSettings = (ImageView) findViewById(R.id.toSettings);
        toSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverActivity.this, SettingsActivity.class);
                startActivity(intent);
                // TODO need to implement transition animations
            }
        });

        toFriends = (ImageView) findViewById(R.id.toFriends);
        toFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });

        toBadges = (ImageView) findViewById(R.id.toBadges);
        toBadges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverActivity.this, BadgesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        rippleBackground.stopRippleAnimation();

    }
}
