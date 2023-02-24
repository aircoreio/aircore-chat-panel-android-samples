package io.aircore.panel.chatviewsample;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.graphics.Color;

import java.util.UUID;
import io.aircore.panel.chatviewsample.R;
import io.aircore.panel.chat.config.ChatPanelConfiguration;
import io.aircore.panel.chat.config.ChatPanelStrings;
import io.aircore.panel.chat.config.CollapsedStateOptions;
import io.aircore.panel.chat.config.ExpandedStateOptions;
import io.aircore.panel.chat.view.ChatPanelTheme;
import io.aircore.panel.chat.view.ChatPanelView;
import io.aircore.panel.chat.view.MessageStyle;
import io.aircore.panel.chat.view.PanelColorsHelper;
import io.aircore.panel.common.Client;
import io.aircore.panel.common.ClientListener;
import io.aircore.panel.common.theme.PanelColors;
import io.aircore.panel.common.theme.PanelIconography;

public class MainActivity extends AppCompatActivity {
    
    public static final String PUBLISHABLE_API_KEY = "YOUR_PUBLISHABLE_API_KEY_HERE";
    
    private Client client;

    private final String userId = UUID.randomUUID().toString();
    private final String displayName = "Han Solo";
    private final String avatarUrl = "https://picsum.photos/seed/" + userId + "/300/300";

    private final String channelId = "sample-app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = createClient();
        client.connect(channelId);

        setContentView(R.layout.chat_panel_activity);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ChatPanelView chatPanelView = findViewById(R.id.chat_panel_view);

//        ChatPanelTheme customTheme = new ChatPanelTheme(
//                customColors(),
//                customIncomingMessageStyle(),
//                customOutgoingMessageStyle(),
//                customIcons()
//        );

//        ChatPanelConfiguration customConfiguration = customConfiguration();

        chatPanelView.connect(
                client, 
                channelId
//                customTheme, 
//                customConfiguration
        );
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        client.disconnect(channelId);
        client.destroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Although out of the scope of this Sample, in order for your call state to survive Configuration
        // Changes like device rotation, consider moving your Client instance to a ViewModel
        // or a Service
    }

    private Client createClient() {
        Client client = Client.createWithPublishableApiKey(
                getApplication(),
                getPublishableApiKey(),
                userId
        );
        client.setUserDisplayName(displayName);
        client.setUserAvatarUrl(avatarUrl);
        client.addListener(new ClientListener() {
            @Override
            public void onSessionAuthTokenNearingExpiry() {
            }

            @Override
            public void onSessionAuthTokenInvalid() {
            }

            @Override
            public void onSessionAuthTokenMismatch() {
            }

            @Override
            public void onLocalUserJoined(@NonNull String channelId) {
            }

            @Override
            public void onLocalUserLeft(@NonNull String channelId) {
            }

            @Override
            public void onError(@NonNull Exception e) {
            }
        });

        return client;
    }
    
    private PanelIconography customIcons() {
        return new PanelIconography(
                R.drawable.common_ic_collapse, // collapse
                R.drawable.common_ic_expand, // expand
                R.drawable.common_ic_share, // share
                R.drawable.common_ic_overflow, // overflowMenu
                R.drawable.common_ic_headphones, // join
                R.drawable.common_ic_close,  // leave
                R.drawable.common_ic_mic_on, // micEnabled
                R.drawable.common_ic_mic_off // micDisabled
        );
    }
    
    private PanelColors customColors() {
        return PanelColorsHelper.fromColorInt(
                getColor(R.color.green_500), // primary
                getColor(R.color.white), // primaryContrast
                getColor(R.color.error), // danger
                getColor(R.color.white), // dangerContrast
                getColor(R.color.white), // background
                getColor(R.color.black), // text
                getColor(android.R.color.darker_gray), // subtext
                getColor(R.color.border) // border
        );
    }

    private MessageStyle customIncomingMessageStyle() {
        return new MessageStyle(
                getColor(R.color.white), // backgroundColor
                getColor(R.color.black), // backgroundColorContrast
                getColor(R.color.border), // borderColor
                getColor(R.color.green_500) // userNameColor
        );
    }

    private MessageStyle customOutgoingMessageStyle() {
        return new MessageStyle(
                getColor(R.color.green_500), // backgroundColor
                getColor(R.color.white), // backgroundColorContrast
                getColor(R.color.green_200), // borderColor
                getColor(R.color.white) // userNameColor
        );
    }

    private ChatPanelConfiguration customConfiguration() {
        return new ChatPanelConfiguration(
                "My Panel Title", // panelTitle
                "My Panel Subtitle", // panelSubtitle
                customStrings(), // strings
                new CollapsedStateOptions.Bar( // collapsedStateOptions
                        6, // maxAvatars
                        "Collapsed Title", // panelTitle
                        "Connect", // joinButtonText
                        "Connecting...", // joiningButtonText
                        "Disconnect" // leaveButtonText
                ),
                new ExpandedStateOptions( // expandedStateOptions
                        "Expanded Title", // panelTitle
                        "Expanded Title", // panelSubtitle,
                        new ExpandedStateOptions.MessageOptions(
                                ExpandedStateOptions.MessageOptions.HorizontalAlignment.Left.INSTANCE,
                                true,
                                true
                        ), // incomingMessageOptions
                        new ExpandedStateOptions.MessageOptions(
                                ExpandedStateOptions.MessageOptions.HorizontalAlignment.Right.INSTANCE,
                                false,
                                false
                        ), // outgoingMessageOptions
                        6, // maxAvatars
                        "Enter", // joinButtonText
                        "Entering", // joiningButtonText
                        "Leave" // leaveButtonText
                ));
    }

    private ChatPanelStrings customStrings() {
        return new ChatPanelStrings(
                "Join", // joinButton
                "Joining...", // joiningButton
                "Leave", // leaveButton
                "Retry", // retryButton
                "No messages yet", // emptyChatTitle
                "Be the first one to say something", // emptyChatJoinedSubtitle
                "Tap \"Join\" and be the first one to say something", // emptyChatNotJoinedSubtitle
                "Send a message", // composerPlaceholder
                "\"Join\" & start messaging", // joinButtonTooltip
                "Active", // usersActiveLabel
                "Something went wrong..." // genericErrorLabel
        );
    }

    private static String getPublishableApiKey() {
        if (BuildConfig.PUBLISHABLE_API_KEY == null || BuildConfig.PUBLISHABLE_API_KEY.isEmpty()) {
            return PUBLISHABLE_API_KEY;
        } else {
            return BuildConfig.PUBLISHABLE_API_KEY;
        }
    }
}