package io.aircore.panel.chatsample

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.aircore.panel.chat.ChatPanel
import io.aircore.panel.chat.config.ChatPanelConfiguration
import io.aircore.panel.chat.config.ChatPanelStrings
import io.aircore.panel.chat.config.CollapsedStateOptions
import io.aircore.panel.chat.config.ExpandedStateOptions
import io.aircore.panel.chat.theme.ChatPanelTheme
import io.aircore.panel.chat.theme.MessageStyle
import io.aircore.panel.common.Client
import io.aircore.panel.common.ClientListener
import io.aircore.panel.common.theme.PanelColors
import io.aircore.panel.common.theme.PanelIconography
import io.aircore.panel.common.theme.PanelsTheme
import io.aircore.panel.chatsample.ui.theme.Green500
import io.aircore.panel.chatsample.ui.theme.MyAppTheme
import java.util.UUID

class MainActivity : ComponentActivity() {

    // For information and best practices on creating and using a Publishable API Key,
    // please refer to docs (https://docs.airtime.com/key-concepts#apps-and-api-keys).
    private val publishableApiKey =
        BuildConfig.PUBLISHABLE_API_KEY ?: "YOUR_PUBLISHABLE_API_KEY_HERE"

    private val userId = UUID.randomUUID().toString()
    private val displayName = "Han Solo"
    private val avatarUrl = "https://picsum.photos/seed/$userId/300/300"

    private val channelId = "sample-app"

    // Option 1 : Use a Publishable API Key directly from the developer console
    private val client by lazy {
        Client.createWithPublishableApiKey(
            application = application,
            key = publishableApiKey,
            userId = userId
        ).apply {
            // Choose a name and profile picture that will be used to show the user on the Panel
            userDisplayName = displayName
            userAvatarUrl = avatarUrl

            // Register event handlers for various events that are of interest for your host app
            addListener(object : ClientListener {
                override fun onSessionAuthTokenInvalid() {
                    //Request the server for a new token
                }
            })
        }
    }

    // Option 2 :Use a session auth token provided by your server by communication with Aircore's
    // provisioning service using the Secret API key
    // private val client = Client.createWithSessionAuthToken(application, authToken, userId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        client.connect(channelId)

        setContent {
            MyAppTheme {
                PanelsTheme(
//                     colors = remember { customColors() },
//                     icons = remember { customIcons() },
                ) {
                    ChatPanelTheme(
//                     incomingMessageStyle = remember { customIncomingMessageStyle() },
//                     outgoingMessageStyle = remember { customOutgoingMessageStyle() }
                    ) {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                // In Collapsed state the ChatPanel will fill its parent.
                                Surface(
                                    elevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                ) {
                                    ChatPanel(
                                        client = client,
                                        channelId = channelId,
                                        // configuration = remember { customConfiguration() }
                                    )
                                }
                            }
                        ) {
                            Box(Modifier.padding(it))
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.disconnect(channelId)
        client.destroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Although out of the scope of this Sample, in order for your call state to survive Configuration
        // Changes like device rotation, consider moving your Client instance to a ViewModel
        // or a Service
    }

    // Customization Examples

    private fun customIcons(): PanelIconography {
        return PanelIconography(
            collapse = R.drawable.common_ic_collapse,
            expand = R.drawable.common_ic_expand,
            share = R.drawable.common_ic_share,
            overflowMenu = R.drawable.common_ic_overflow,
            join = R.drawable.common_ic_headphones,
            leave = R.drawable.common_ic_close
        )
    }

    private fun customColors(): PanelColors {
        return PanelColors(
            primary = Green500,
            primaryContrast = Color.White,
            danger = Color.Red,
            dangerContrast = Color.White,
            background = Color.White,
            text = Color.Black,
            subtext = Color.Black.copy(alpha = 0.7f),
            border = Color.Black.copy(alpha = 0.1f)
        )
    }

    private fun customConfiguration(): ChatPanelConfiguration {
        return ChatPanelConfiguration(
            panelTitle = "My Panel Title",
            panelSubtitle = "My Panel Subtitle",
            strings = customStrings(),
            collapsedStateOptions = CollapsedStateOptions.Bar(
                maxAvatars = 3,
                panelTitle = "Collapsed Title",
                joinButtonText = "Connect",
                joiningButtonText = "Connecting...",
                leaveButtonText = "Disconnect"
            ),
            expandedStateOptions = ExpandedStateOptions(
                panelTitle = "Expanded Title",
                panelSubtitle = "Expanded Subtitle",
                joinButtonText = "Enter",
                joiningButtonText = "Entering",
                leaveButtonText = "Leave",
                incomingMessage = ExpandedStateOptions.MessageOptions(
                    horizontalAlignment = ExpandedStateOptions.MessageOptions.HorizontalAlignment.Left,
                    showAvatar = true,
                    showUserName = true
                ),
                outgoingMessage = ExpandedStateOptions.MessageOptions(
                    horizontalAlignment = ExpandedStateOptions.MessageOptions.HorizontalAlignment.Right,
                    showAvatar = false,
                    showUserName = false
                )
            )
        )
    }

    private fun customStrings(): ChatPanelStrings {
        return ChatPanelStrings(
            joinButton = "Join",
            joiningButton = "Joining...",
            leaveButton = "Leave",
            retryButton = "Retry",
            emptyChatTitle = "No messages yet",
            emptyChatJoinedSubtitle = "Be the first one to say something",
            emptyChatNotJoinedSubtitle = "Tap \"Join\" and be the first one to say something",
            composerPlaceholder = "Send a message",
            joinButtonTooltip = "\"Join\" & start messaging",
            usersActiveLabel = "Active",
            genericErrorLabel = "Something went wrong..."
        )
    }

    private fun customIncomingMessageStyle(): MessageStyle {
        return MessageStyle(
            backgroundColor = Color.LightGray,
            backgroundContrastColor = Color.Black,
            borderColor = Color.DarkGray,
            userNameColor = Color.Black
        )
    }

    private fun customOutgoingMessageStyle(): MessageStyle {
        return MessageStyle(
            backgroundColor = Color.Black,
            backgroundContrastColor = Color.White,
            borderColor = Color.DarkGray,
            userNameColor = Color.White
        )
    }
}

