package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ar.ArObjectDetectionService
import com.example.data.model.Product3RSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EcoVision", appName)
  }

  @Test
  fun `verify 3R database seed items have complete reduce reuse recycle details`() {
    val products = Product3RSeedData.initialProducts
    assertTrue("Should have at least 20 items in 3R seed catalog", products.size >= 20)

    for (product in products) {
      assertNotNull("Product ID should not be null", product.id)
      assertTrue("Product ${product.name} must have non-empty name", product.name.isNotBlank())
      assertTrue("Product ${product.name} must have 3R classification", product.threeRClassification.isNotBlank())
      assertTrue("Product ${product.name} must have Reduce details", product.howToReduce.isNotBlank())
      assertTrue("Product ${product.name} must have Reuse details", product.howToReuseUpcycle.isNotBlank())
      assertTrue("Product ${product.name} must have Recycle details", product.howToRecycle.isNotBlank())
      assertTrue("Product ${product.name} must have physical dimensions", product.dimensionsMm.isNotBlank())
      assertTrue("Product ${product.name} must have valid density", product.densityGcm3 > 0f)
    }
  }

  @Test
  fun `verify AR service lockTarget3RProduct creates valid 3R marker`() {
    val arService = ArObjectDetectionService.getInstance()
    val marker = arService.lockTarget3RProduct(
      name = "PET Water Bottle (500ml)",
      category = "Plastics (PET #1)",
      resinCode = "Resin #1 (PET)",
      dimensionsMm = "205 × 65 × 65 mm",
      densityGcm3 = 1.38f,
      threeRClassification = "RECYCLE",
      reduceTip = "Carry stainless steel flask",
      reuseTip = "Self-watering planters",
      recycleTip = "Blue recycling bin",
      productId = "pet_bottle_500ml"
    )

    assertEquals("PET Water Bottle (500ml)", marker.label)
    assertEquals("RECYCLE", marker.threeRClassification)
    assertEquals("pet_bottle_500ml", marker.threeRProductId)
    assertEquals(0.48f, marker.distanceMeters)
  }
}
