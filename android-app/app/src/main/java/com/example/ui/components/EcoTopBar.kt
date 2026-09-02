package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkText
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SoftPaleGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoTopBar(
    title: String,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LightCreamGreen,
        border = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.8f))
    ) {
        TopAppBar(
            modifier = modifier.testTag("eco_top_bar"),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = LightCreamGreen,
                titleContentColor = DarkText
            ),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // EcoVision Official Logo Thumbnail
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.2.dp, PrimaryGreen.copy(alpha = 0.5f)),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .testTag("ecovision_topbar_logo")
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ecovision_official_logo_1787984664845),
                            contentDescription = "EcoVision Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (title == "EcoVision") {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = PrimaryGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Eco")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = DarkText,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Vision")
                                }
                            },
                            fontSize = 20.sp,
                            letterSpacing = 0.2.sp
                        )
                    } else {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            letterSpacing = 0.2.sp,
                            color = DarkText
                        )
                    }
                }
            },
            actions = {
                // Info / Team Button
                IconButton(
                    onClick = onAboutClick,
                    modifier = Modifier.testTag("about_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "About EcoVision & Team",
                        tint = PrimaryGreen
                    )
                }
            }
        )
    }
}

