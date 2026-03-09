package com.example.up_mobileappv2.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.up_mobileappv2.domain.model.Product

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            // Изображение товара (заглушка, пока нет фото)
            AsyncImage(
                model = "https://via.placeholder.com/80",
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${product.cost} ₽",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}