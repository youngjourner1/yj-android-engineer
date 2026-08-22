package com.yj.webcam.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yj.webcam.core.theme.YJErrorRed
import com.yj.webcam.core.theme.YJSuccessGreen
import com.yj.webcam.core.theme.YJWarningYellow
import com.yj.webcam.domain.model.ConnectionStatus

@Composable
fun StatusBadge(
    status: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (status) {
        ConnectionStatus.DISCONNECTED -> YJErrorRed to "OFFLINE"
        ConnectionStatus.PAIRING -> YJWarningYellow to "PAIRING"
        ConnectionStatus.CONNECTING -> YJWarningYellow to "CONNECTING"
        ConnectionStatus.CONNECTED -> YJSuccessGreen to "READY"
        ConnectionStatus.STREAMING -> YJSuccessGreen to "STREAMING LIVE"
        ConnectionStatus.RECONNECTING -> YJWarningYellow to "RECONNECTING"
        ConnectionStatus.ERROR -> YJErrorRed to "ERROR"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = color,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
