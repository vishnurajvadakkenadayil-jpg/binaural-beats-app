package com.vishnuraj.binauralbeats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BrandMark() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.ic_brand_logo),
            contentDescription = "Binaural Beats",
            modifier = Modifier.size(38.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFFF8F8F5))) { append("Binaural") }
                withStyle(SpanStyle(color = Color(0xFFC8FF58))) { append("Beats") }
            },
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.7).sp,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}
