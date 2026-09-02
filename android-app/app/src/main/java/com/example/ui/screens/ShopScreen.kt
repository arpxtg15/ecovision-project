package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EcoProduct
import com.example.data.model.EcoShopData
import com.example.ui.theme.EcoAmberWarm
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenPrimaryDark
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.EcoLightGreenAccent
import com.example.ui.theme.EcoMintBorder
import com.example.ui.theme.EcoMintBorderDark
import com.example.ui.theme.EcoMintContainer
import com.example.ui.theme.EcoMintContainerLow
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.EcoTealAccent
import com.example.ui.theme.EcoTextPrimary
import com.example.ui.theme.EcoTextSecondary

@Composable
fun ShopScreen(
    availableCredits: Int,
    selectedCategory: String,
    searchQuery: String,
    selectedProduct: EcoProduct?,
    showDetailDialog: Boolean,
    showOrderSuccessDialog: Boolean,
    lastOrderedProduct: EcoProduct?,
    purchasedProductIds: Set<String>,
    feedbackMessage: String?,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onProductSelected: (EcoProduct) -> Unit,
    onDismissDialog: () -> Unit,
    onDismissSuccessDialog: () -> Unit,
    onRedeemProduct: (EcoProduct) -> Unit,
    onPurchaseProduct: (EcoProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredProducts = EcoShopData.products.filter { product ->
        val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.subtitle.contains(searchQuery, ignoreCase = true) ||
                product.category.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("shop_screen_scroll"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Header & Eco Points Balance Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Eco Shop & Rewards",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Redeem your recycling points for sustainable essentials & verified trees",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Balance Card
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = EcoMintContainer,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shop_eco_points_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Toll,
                                        contentDescription = null,
                                        tint = EcoGreenPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Your Eco Credits",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenPrimaryDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$availableCredits pts",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EcoGreenPrimaryDark
                                )
                                Text(
                                    text = "Earned by recycling & completing daily challenges",
                                    fontSize = 11.sp,
                                    color = EcoTextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(EcoGreenPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎁", fontSize = 22.sp)
                            }
                        }
                    }
                }
            }

            // 2. Feedback Snackbar/Banner
            if (feedbackMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EcoMintContainerLow,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = feedbackMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EcoGreenPrimaryDark,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // 3. Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Search zero-waste products...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoGreenPrimary,
                        unfocusedBorderColor = EcoMintBorder,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .testTag("shop_search_input")
                )
            }

            // 4. Category Pills Horizontal Scroll
            item {
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EcoShopData.categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) EcoGreenPrimary else EcoMintContainer,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) EcoGreenPrimaryDark else EcoMintBorder
                            ),
                            modifier = Modifier
                                .clickable { onCategorySelected(category) }
                                .testTag("shop_category_$category")
                        ) {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) LightCreamGreen else EcoGreenPrimaryDark,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // 5. Section Title & Count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curated Eco Goods (${filteredProducts.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 6. Product Cards
            items(filteredProducts) { product ->
                val isPurchased = purchasedProductIds.contains(product.id)
                ProductCard(
                    product = product,
                    isPurchased = isPurchased,
                    canAffordCredits = availableCredits >= product.ecoCreditsPrice,
                    onClick = { onProductSelected(product) },
                    onRedeem = { onRedeemProduct(product) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }
        }
    }

    // Product Detail & Action Dialog
    if (showDetailDialog && selectedProduct != null) {
        ProductDetailDialog(
            product = selectedProduct,
            availableCredits = availableCredits,
            isPurchased = purchasedProductIds.contains(selectedProduct.id),
            onDismiss = onDismissDialog,
            onRedeem = { onRedeemProduct(selectedProduct) },
            onPurchase = { onPurchaseProduct(selectedProduct) }
        )
    }

    // Order Success Celebratory Dialog
    if (showOrderSuccessDialog && lastOrderedProduct != null) {
        OrderSuccessDialog(
            product = lastOrderedProduct,
            onDismiss = onDismissSuccessDialog
        )
    }
}

@Composable
private fun ProductCard(
    product: EcoProduct,
    isPurchased: Boolean,
    canAffordCredits: Boolean,
    onClick: () -> Unit,
    onRedeem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("shop_product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, EcoMintBorder.copy(alpha = 0.85f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Product Emoji Icon Box
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(EcoMintContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = product.iconEmoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EcoMintContainerLow,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Text(
                            text = product.sustainabilityBadge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimaryDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = product.subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Rating & Reviews
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = EcoAmberWarm,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${product.rating} (${product.reviewsCount})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Impact Statement Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EcoMintContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = product.impactStatement,
                        fontSize = 11.sp,
                        color = EcoGreenPrimaryDark,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${product.ecoCreditsPrice} pts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EcoGreenPrimaryDark
                    )
                    Text(
                        text = "or $${String.format(java.util.Locale.US, "%.2f", product.priceUsd)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPurchased) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcoMintContainer,
                            border = BorderStroke(1.dp, EcoMintBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = EcoGreenPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Claimed",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onRedeem,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canAffordCredits) EcoGreenPrimary else EcoMintContainer,
                                contentColor = if (canAffordCredits) LightCreamGreen else EcoGreenPrimaryDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("redeem_button_${product.id}")
                        ) {
                            Text(
                                text = if (canAffordCredits) "Redeem" else "View Details",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailDialog(
    product: EcoProduct,
    availableCredits: Int,
    isPurchased: Boolean,
    onDismiss: () -> Unit,
    onRedeem: () -> Unit,
    onPurchase: () -> Unit
) {
    val canAfford = availableCredits >= product.ecoCreditsPrice
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("product_detail_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = product.iconEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = product.category,
                        fontSize = 11.sp,
                        color = EcoGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Sustainability Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoMintContainer,
                    border = BorderStroke(1.dp, EcoMintBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WorkspacePremium,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = product.sustainabilityBadge,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimaryDark
                            )
                            Text(
                                text = product.impactStatement,
                                fontSize = 11.sp,
                                color = EcoTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Product Overview",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Key Eco Highlights:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                product.features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feature,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Material: ${product.material}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = EcoGreenPrimaryDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Points summary
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EcoMintContainerLow,
                    border = BorderStroke(1.dp, EcoMintBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cost: ${product.ecoCreditsPrice} Credits",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimaryDark
                        )
                        Text(
                            text = "Balance: $availableCredits pts",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (canAfford) EcoGreenPrimary else Color(0xFFE53935)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onRedeem,
                enabled = canAfford && !isPurchased,
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_redeem_button")
            ) {
                Text(
                    text = if (isPurchased) "Claimed" else "Redeem Points",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightCreamGreen
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onPurchase,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, EcoGreenPrimary),
                modifier = Modifier.testTag("dialog_direct_order_button")
            ) {
                Text(
                    text = "Direct Order ($${String.format(java.util.Locale.US, "%.2f", product.priceUsd)})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary
                )
            }
        }
    )
}

@Composable
private fun OrderSuccessDialog(
    product: EcoProduct,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("order_success_dialog"),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(EcoMintContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎉", fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Order Confirmed!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EcoGreenPrimaryDark
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Thank you for supporting sustainable living!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EcoMintContainerLow,
                    border = BorderStroke(1.dp, EcoMintBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "${product.iconEmoji} ${product.name}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🌱 Projected Impact:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text(
                            text = product.impactStatement,
                            fontSize = 11.sp,
                            color = EcoGreenPrimaryDark
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_success_ok_button")
            ) {
                Text("Great!", fontWeight = FontWeight.Bold, color = LightCreamGreen)
            }
        }
    )
}
