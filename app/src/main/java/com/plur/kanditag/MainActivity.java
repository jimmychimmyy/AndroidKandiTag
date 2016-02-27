package com.plur.kanditag;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements ColorPickerDialog.ColorPickerListener {

    private static final String APP_KEY = "eyJzaWduYXR1cmUiOiJ1V1BwKzhQMmkxOWIwSTJQUXExYmJISVNRb3FwNTl1QXVuUUlVS1hzaXRNeWwyQzI5M29yZEJna0FZVGdjdGVpcmhjbjd3ck1XWUtuTEJ0eHVpMHpvMGsrSUNja0JRSmIzTXNCQU81bjNtbTg5T2FObURMbmc1YnZCU2d5R05pVmpIMVEwUzg1Zjlac2syb29VTjRESkdBeTFnNXJmaXZQeXBVYmhBQ3kyRGM9IiwiYXBwSWQiOjE0MzgsInZhbGlkVW50aWwiOjE2OTQ1LCJhcHBVVVVJRCI6IjJEMDlBQkZDLUEzMDQtNDAwOC1BRDIxLTAyRjgyRjNDRUEzQiJ9";

    // Enabling (1/2) - Enable the P2P Services
    private void enableKit() {

        final StatusResult result = P2PKitClient.isP2PServicesAvailable(this);
        if (result.getStatusCode() == StatusResult.SUCCESS) {
            P2PKitClient client = P2PKitClient.getInstance(this);
            logToView("enabling P2PKit");
            client.enableP2PKit(mStatusCallback, APP_KEY);
            mWantToEnable = false;
        } else {
            mWantToEnable = true;
            logToView("Cannot start P2PKit, status code: " + result.getStatusCode());
            StatusResultHandling.showAlertDialogForStatusError(this, result);
        }
    }

    // Enabling (2/2) - Handle the status callbacks with the P2P Services
    private final P2PKitStatusCallback mStatusCallback = new P2PKitStatusCallback() {

        @Override
        public void onEnabled() {
            logToView("Successfully enabled P2P Services, with node id: " + P2PKitClient.getInstance(MainActivity.this).getNodeId().toString());

            mP2pSwitch.setEnabled(true);
            mGeoSwitch.setEnabled(true);

            if (mShouldStartServices) {
                mShouldStartServices = false;

                startP2pDiscovery();
            }
        }

        @Override
        public void onSuspended() {
            logToView("P2P Services suspended");

            mGeoSwitch.setEnabled(false);
            mP2pSwitch.setEnabled(false);
        }

        @Override
        public void onError(StatusResult statusResult) {
            logToView("Error in P2P Services with status: " + statusResult.getStatusCode());
            StatusResultHandling.showAlertDialogForStatusError(MainActivity.this, statusResult);
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
            P2PKitClient.getInstance(this).getDiscoveryServices().setP2pDiscoveryInfo(getColorBytes(mCurrentColor));
        } catch (InfoTooLongException e) {
            logToView("P2pListener | The discovery info is too long");
        }
        P2PKitClient.getInstance(this).getDiscoveryServices().addP2pListener(mP2pDiscoveryListener);
    }

    // Listener of P2P discovery events
    private final P2PListener mP2pDiscoveryListener = new P2PListener() {

        @Override
        public void onP2PStateChanged(final int state) {
            logToView("P2pListener | State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            byte[] colorBytes = peer.getDiscoveryInfo();
            if (colorBytes != null && colorBytes.length == 3) {
                logToView("P2pListener | Peer discovered: " + peer.getNodeId() + " with color: " + getHexRepresentation(colorBytes));
            } else {
                logToView("P2pListener | Peer discovered: " + peer.getNodeId() + " without color");
            }
        }

        @Override
        public void onPeerLost(final Peer peer) {
            logToView("P2pListener | Peer lost: " + peer.getNodeId());
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            byte[] colorBytes = peer.getDiscoveryInfo();
            if (colorBytes != null && colorBytes.length == 3) {
                logToView("P2pListener | Peer updated: " + peer.getNodeId() + " with new color: " + getHexRepresentation(colorBytes));
            }
        }
    };

    private void stopP2pDiscovery() {
        P2PKitClient.getInstance(this).getDiscoveryServices().removeP2pListener(mP2pDiscoveryListener);
        logToView("P2pListener removed");
    }

    private void startGeoDiscovery() {
        P2PKitClient.getInstance(this).getMessageServices().addMessageListener(mMessageListener);

        P2PKitClient.getInstance(this).getDiscoveryServices().addGeoListener(mGeoDiscoveryListener);
    }

    private final GeoListener mGeoDiscoveryListener = new GeoListener() {

        @Override
        public void onGeoStateChanged(final int state) {
            logToView("GeoListener | State changed: " + state);
        }

        @Override
        public void onPeerDiscovered(final UUID nodeId) {
            logToView("GeoListener | Peer discovered: " + nodeId);

            // sending a message to the peer
            try {
                P2PKitClient.getInstance(MainActivity.this).getMessageServices().sendMessage(nodeId, "SimpleChatMessage", "From Android: Hello GEO!".getBytes());
            } catch (MessageTooLargeException e) {
                logToView("GeoListener | " + e.getMessage());
            }
        }

        @Override
        public void onPeerLost(final UUID nodeId) {
            logToView("GeoListener | Peer lost: " + nodeId);
        }
    };

    private final MessageListener mMessageListener = new MessageListener() {

        @Override
        public void onMessageStateChanged(final int state) {
            logToView("MessageListener | State changed: " + state);
        }

        @Override
        public void onMessageReceived(final long timestamp, final UUID origin, final String type, final byte[] message) {
            logToView("MessageListener | Message received: From=" + origin + " type=" + type + " message=" + new String(message));
        }
    };

    private void stopGeoDiscovery() {
        P2PKitClient.getInstance(this).getMessageServices().removeMessageListener(mMessageListener);
        logToView("MessageListener removed");

        P2PKitClient.getInstance(this).getDiscoveryServices().removeGeoListener(mGeoDiscoveryListener);
        logToView("GeoListener removed");
    }

    private boolean mShouldStartServices;
    private boolean mWantToEnable = false;

    private int mCurrentColor = -65536;

    private TextView mLogView;
    private Switch mP2pSwitch;
    private Switch mGeoSwitch;

    @Override
    public void onColorPicked(int colorCode) {
        mCurrentColor = colorCode;

        if (mShouldStartServices) {
            enableKit();
        } else if (P2PKitClient.getInstance(this).isEnabled()) {
            try {
                byte[] colorBytes = getColorBytes(mCurrentColor);
                P2PKitClient.getInstance(this).getDiscoveryServices().setP2pDiscoveryInfo(colorBytes);
            } catch (InfoTooLongException e) {
                logToView("P2pListener | The discovery info is too long");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();

        mShouldStartServices = true;
        showColorPickerDialog();
    }

    @Override
    public void onResume() {
        super.onResume();

        // When to user comes back from playstore after installing p2p services, try to enable p2pkit again
        if(mWantToEnable && !P2PKitClient.getInstance(this).isEnabled()) {
            enableKit();
        }
    }

    private void setupUI() {
        mLogView = (TextView) findViewById(R.id.textView);

        findViewById(R.id.clearTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLogs();
            }
        });

        findViewById(R.id.changeColorTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });

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

    private void logToView(String message) {
        CharSequence currentTime = DateFormat.format("hh:mm:ss - ", System.currentTimeMillis());
        mLogView.setText(currentTime + message + "\n" + mLogView.getText());
    }

    private void clearLogs() {
        mLogView.setText("");
    }

    private String getHexRepresentation(byte[] colorBytes) {
        int colorCode = Color.rgb(colorBytes[0] & 0xFF, colorBytes[1] & 0xFF, colorBytes[2] & 0xFF);
        return String.format("#%06X", (0xFFFFFF & colorCode));
    }

    private byte[] getColorBytes(int color) {
        return new byte[] {(byte) Color.red(color), (byte) Color.green(color), (byte) Color.blue(color)};
    }

    private void showColorPickerDialog() {
        ColorPickerDialog dialog = ColorPickerDialog.newInstance(mCurrentColor);
        dialog.show(getFragmentManager(), "ColorPicker");
    }
}
