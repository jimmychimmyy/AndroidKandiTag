package com.plur.kanditag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.UUID;

import ch.uepaa.p2pkit.P2PKitClient;
import ch.uepaa.p2pkit.P2PKitStatusCallback;
import ch.uepaa.p2pkit.StatusResult;
import ch.uepaa.p2pkit.StatusResultHandling;
import ch.uepaa.p2pkit.discovery.GeoListener;
import ch.uepaa.p2pkit.discovery.InfoTooLongException;
import ch.uepaa.p2pkit.discovery.P2PListener;
import ch.uepaa.p2pkit.discovery.entity.Peer;
import ch.uepaa.p2pkit.internal.messaging.MessageTooLargeException;
import ch.uepaa.p2pkit.messaging.MessageListener;

/**
 * Created by Jim on 2/24/16.
 * DiscoverActivity is the starting point of the application
 */
public class DiscoverActivity extends Activity {

    private static final String TAG = "DiscoverActivity";

    /* Variables for P2PKit */
    private static final String PROXIMITY_APP_KEY = "eyJzaWduYXR1cmUiOiJ1V1BwKzhQMmkxOWIwSTJQUXExYmJISVNRb3FwNTl1QXVuUUlVS1hzaXRNeWwyQzI5M29yZEJna0FZVGdjdGVpcmhjbjd3ck1XWUtuTEJ0eHVpMHpvMGsrSUNja0JRSmIzTXNCQU81bjNtbTg5T2FObURMbmc1YnZCU2d5R05pVmpIMVEwUzg1Zjlac2syb29VTjRESkdBeTFnNXJmaXZQeXBVYmhBQ3kyRGM9IiwiYXBwSWQiOjE0MzgsInZhbGlkVW50aWwiOjE2OTQ1LCJhcHBVVVVJRCI6IjJEMDlBQkZDLUEzMDQtNDAwOC1BRDIxLTAyRjgyRjNDRUEzQiJ9";
    private boolean mShouldStartServices;
    private boolean mWantToEnable = false;
    private Switch mP2pSwitch;
    private Switch mGeoSwitch;

    private ImageView toSettings, toFriends, toBadges;

    private RippleBackground rippleBackground;
    private boolean isRippling;

    private ArrayList<Node> nodes;
    ArrayList<ArrayList<Boolean>> edges;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        initXml();
        initNodes();

    }

    @Override
    public void onResume() {
        super.onResume();

        // When to user comes back from playstore after installing p2p services, try to enable p2pkit again
        if(mWantToEnable && !P2PKitClient.getInstance(this).isEnabled()) {
            enableKit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        rippleBackground.stopRippleAnimation();
        try {
            disableKit();
        } catch (Exception e) {}
    }

    /* Call this method to download user image when discovered using p2p */
    private void downloadProfileImage() {

    }

    /* initialize maps for nodes */
    private void initNodes() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    /* TODO work on the GUI later

    public void draw() {
        int n = nodes.size();

        for (int i = 0; i < n; i++) {
            Node v = nodes.get(i);

            v.netForce.x = 0f;
            v.netForce.y = 0f;

            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                Node u = nodes.get(j);

                float rsq = (float) (Math.pow(v.x - u.x, 2) + Math.pow(v.y - u.y, 2));
                v.netForce.x += 200 * (v.x - u.x) / (rsq * n);
                v.netForce.y += 200 * (v.y - u.y) / (rsq * n);
            }

            for (int j = 0; j < n; j++) {
                if (!edges.get(i).get(j)) continue;
                Node u = nodes.get(j);

                v.netForce.x += 0.06 * (u.x - v.x);
                v.netForce.y += 0.06 * (u.y - v.y);
            }

            v.velocity.x = (v.velocity.x + v.netForce.x) * 0.75f;
            v.velocity.y = (v.velocity.y + v.netForce.y) * 0.75f;
        }

        for (Node v : nodes) {
            v.x += v.velocity.x;
            v.y += v.velocity.y;
        }

        // clear background
        //background(0);

        // draw lines
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!edges.get(i).get(j)) continue;
                line(nodes.get(i).x, nodes.get(i).y, nodes.get(j).x, nodes.get(j).y);
                strokeWeight(2);
                stroke(255);
            }
        }

        // draw nodes
        for (Node v : nodes) {
            ellipse(v.x, v.y, 20, 20);
        }
    }

    public void mousePressed() {
        ArrayList<Boolean> newEdge = new ArrayList<>();

        for (ArrayList<Boolean> edge : edges) {
            edge.add(false);
        }

        edges.add(newEdge);

        int n = edges.size();
        for (int i = 0; i < n; i++) {
            newEdge.add(false);
        }

        if (n > 1) {
            int a = (int) (Math.random() * (n - 1));
            edges.get(a).set(n - 1, true);
            newEdge.set(a, true);
        }

        Node v = new Node();
        v.x = mouseX;
        v.y = mouseY;
        nodes.add(v);
    }

     */

    /* initialize xml */
    private void initXml() {
        isRippling = false;
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        View view = (View) findViewById(R.id.image);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRippling) {
                    rippleBackground.stopRippleAnimation();
                    isRippling = false;
                    mShouldStartServices = false;
                } else {
                    rippleBackground.startRippleAnimation();
                    isRippling = true;
                    mShouldStartServices = true; // set this to true so proximity detecting can start
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

    // Enabling (1/2) - Enable the P2P Services
    private void enableKit() {

        final StatusResult result = P2PKitClient.isP2PServicesAvailable(this);
        if (result.getStatusCode() == StatusResult.SUCCESS) {
            P2PKitClient client = P2PKitClient.getInstance(this);
            Log.i(TAG, "enabling P2PKit");
            client.enableP2PKit(mStatusCallback, PROXIMITY_APP_KEY);
            mWantToEnable = false;
        } else {
            mWantToEnable = true;
            Log.i(TAG, "Cannot start P2PKit, status code: " + result.getStatusCode());
            StatusResultHandling.showAlertDialogForStatusError(this, result);
        }
    }

    // Enabling (2/2) - Handle the status callbacks with the P2P Services
    private final P2PKitStatusCallback mStatusCallback = new P2PKitStatusCallback() {

        @Override
        public void onEnabled() {
            Log.i(TAG, "Successfully enabled P2P Services, with node id: " + P2PKitClient.getInstance(DiscoverActivity.this).getNodeId().toString());

            mP2pSwitch.setEnabled(true);
            mGeoSwitch.setEnabled(true);

            if (mShouldStartServices) {
                mShouldStartServices = false;

                startP2pDiscovery();
            }
        }

        @Override
        public void onSuspended() {
            Log.i(TAG, "P2P Services suspended");

            mGeoSwitch.setEnabled(false);
            mP2pSwitch.setEnabled(false);
        }

        @Override
        public void onError(StatusResult statusResult) {
            Log.i(TAG, "Error in P2P Services with status: " + statusResult.getStatusCode());
            StatusResultHandling.showAlertDialogForStatusError(DiscoverActivity.this, statusResult);
        }
    };

    private void disableKit() {
        P2PKitClient client = P2PKitClient.getInstance(this);
        client.getDiscoveryServices().removeGeoListener(mGeoDiscoveryListener);
        client.getDiscoveryServices().removeP2pListener(mP2pDiscoveryListener);
        client.getMessageServices().removeMessageListener(mMessageListener);

        client.disableP2PKit();
    }

    private void startP2pDiscovery() {
        try {
            Log.i(TAG, "starting discovery");
            // TODO uncomment this
            //P2PKitClient.getInstance(this).getDiscoveryServices().setP2pDiscoveryInfo(getColorBytes(mCurrentColor));
        } catch (Exception e) { // changed from InfoTooLongException to Exception
            Log.i(TAG, "P2pListener | The discovery info is too long");
        }
        P2PKitClient.getInstance(this).getDiscoveryServices().addP2pListener(mP2pDiscoveryListener);
    }

    // Listener of P2P discovery events
    private final P2PListener mP2pDiscoveryListener = new P2PListener() {

        @Override
        public void onP2PStateChanged(final int state) {
            Log.i(TAG, "P2pListener | State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            byte[] colorBytes = peer.getDiscoveryInfo();
            if (colorBytes != null && colorBytes.length == 3) {
                Log.i(TAG, "P2pListener | Peer discovered: " + peer.getNodeId()); // + " with new color: " + getHexRepresentation(colorBytes)
            } else {
                Log.i(TAG, "P2pListener | Peer discovered: " + peer.getNodeId()); // + " without color"
            }
        }

        @Override
        public void onPeerLost(final Peer peer) {
            Log.i(TAG, "P2pListener | Peer lost: " + peer.getNodeId());
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            byte[] colorBytes = peer.getDiscoveryInfo();
            if (colorBytes != null && colorBytes.length == 3) {
                Log.i(TAG, "P2pListener | Peer updated: " + peer.getNodeId()); // + " with new color: " + getHexRepresentation(colorBytes)
            }
        }
    };

    private void stopP2pDiscovery() {
        P2PKitClient.getInstance(this).getDiscoveryServices().removeP2pListener(mP2pDiscoveryListener);
        Log.i(TAG, "P2pListener removed");

    }

    private void startGeoDiscovery() {
        P2PKitClient.getInstance(this).getMessageServices().addMessageListener(mMessageListener);

        P2PKitClient.getInstance(this).getDiscoveryServices().addGeoListener(mGeoDiscoveryListener);
    }

    private final GeoListener mGeoDiscoveryListener = new GeoListener() {

        @Override
        public void onGeoStateChanged(final int state) {
            Log.i(TAG, "GeoListener | State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final UUID nodeId) {
            Log.i(TAG, "GeoListener | Peer discovered: " + nodeId);

            // sending a message to the peer
            try {
                P2PKitClient.getInstance(DiscoverActivity.this).getMessageServices().sendMessage(nodeId, "SimpleChatMessage", "From Android: Hello GEO!".getBytes());
            } catch (MessageTooLargeException e) {
                Log.i(TAG, "GeoListener | " + e.getMessage());
            }
        }

        @Override
        public void onPeerLost(final UUID nodeId) {
            Log.i(TAG, "GeoListener | Peer lost: " + nodeId);
        }
    };

    private final MessageListener mMessageListener = new MessageListener() {

        @Override
        public void onMessageStateChanged(final int state) {
            Log.i(TAG, "MessageListener | State changed: " + state);
        }

        @Override
        public void onMessageReceived(final long timestamp, final UUID origin, final String type, final byte[] message) {
            Log.i(TAG, "MessageListener | Message received: From=" + origin + " type=" + type + " message=" + new String(message));
        }
    };

    private void stopGeoDiscovery() {
        P2PKitClient.getInstance(this).getMessageServices().removeMessageListener(mMessageListener);
        Log.i(TAG, "MessageListener removed");

        P2PKitClient.getInstance(this).getDiscoveryServices().removeGeoListener(mGeoDiscoveryListener);
        Log.i(TAG, "GeoListener removed");
    }

    private int mCurrentColor = -65536;

    private void setupUI() {

        Switch kitSwitch = (Switch) findViewById(R.id.kitSwitch);
        kitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    enableKit();
                } else {
                    mP2pSwitch.setChecked(false);
                    mGeoSwitch.setChecked(false);

                    mWantToEnable = false;
                    disableKit();
                }
            }
        });

        mP2pSwitch = (Switch) findViewById(R.id.p2pSwitch);
        mP2pSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startP2pDiscovery();
                } else {
                    stopP2pDiscovery();
                }
            }
        });

        mGeoSwitch = (Switch) findViewById(R.id.geoSwitch);
        mGeoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startGeoDiscovery();
                } else {
                    stopGeoDiscovery();
                }
            }
        });
    }

}
