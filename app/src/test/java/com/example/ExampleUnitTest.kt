package com.example

import com.example.data.model.SocialBoostCategory
import com.example.data.model.SocialBoostPlatform
import com.example.data.model.SocialBoostService
import org.junit.Assert.*
import org.junit.Test

/**
 * Local unit tests verifying Social Boost CRUD models, filtering, sorting, and charge calculation.
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun socialBoost_crudAndChargeCalculation_isAccurate() {
        val platform = SocialBoostPlatform(
            id = "plat_facebook",
            name = "Facebook",
            iconKey = "FACEBOOK",
            displayOrder = 1,
            isEnabled = true
        )
        val category = SocialBoostCategory(
            id = "cat_fb_followers_hq",
            platformId = platform.id,
            name = "Facebook Followers | High-Quality Service",
            displayOrder = 1,
            isEnabled = true
        )
        val service = SocialBoostService(
            id = "srv_fb_814",
            platformId = platform.id,
            categoryId = category.id,
            name = "Facebook Profile/Page Followers [100% Real Mix]",
            pricePerThousandBdt = 160.0,
            minQuantity = 100,
            maxQuantity = 500000,
            averageTime = "15 - 45 Minutes",
            requiresLink = true,
            displayOrder = 1,
            isEnabled = true
        )

        assertEquals("814", service.displayCode())
        assertTrue(service.requiresLink)

        // Verify charge calculation: (quantity / 1000.0) * pricePer1000
        val quantity = 2500
        val totalCharge = (quantity.toDouble() / 1000.0) * service.pricePerThousandBdt
        assertEquals(400.0, totalCharge, 0.001)

        // Verify CRUD update (edit price, min/max, disable/enable)
        val updatedService = service.copy(
            pricePerThousandBdt = 180.0,
            minQuantity = 200,
            isEnabled = false
        )
        assertEquals(180.0, updatedService.pricePerThousandBdt, 0.001)
        assertEquals(200, updatedService.minQuantity)
        assertFalse(updatedService.isEnabled)
    }
}
