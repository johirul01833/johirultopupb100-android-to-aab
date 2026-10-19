package com.example.data.local

import com.example.data.model.EpayRateTier
import com.example.data.model.OrderRecord
import com.example.data.model.PaymentAccount
import com.example.data.model.SavedUid
import com.example.data.model.ServiceCategory
import com.example.data.model.SupportChatMessage
import com.example.data.model.TopupPackage
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.TimeZone

class TopupRepository(private val dao: TopupDao) {

    val allOrders: Flow<List<OrderRecord>> = dao.getAllOrders()
    val allSupportMessages: Flow<List<SupportChatMessage>> = dao.getAllSupportMessages()

    suspend fun insertOrder(order: OrderRecord): Long = dao.insertOrder(order)
    suspend fun updateOrder(order: OrderRecord) = dao.updateOrder(order)
    suspend fun deleteOrder(order: OrderRecord) = dao.deleteOrder(order)
    suspend fun clearOrders() = dao.clearAllOrders()

    suspend fun getSavedUid(service: String): SavedUid? = dao.getSavedUid(service)
    suspend fun saveUid(service: String, uid: String, name: String = "") {
        dao.saveUid(SavedUid(serviceName = service, lastUsedUid = uid, lastUsedName = name))
    }
    suspend fun clearSavedUids() = dao.clearAllSavedUids()

    suspend fun insertSupportMessage(msg: SupportChatMessage) = dao.insertSupportMessage(msg)
    suspend fun insertSupportMessages(msgs: List<SupportChatMessage>) = dao.insertSupportMessages(msgs)
    suspend fun deleteSupportMessageById(id: String) = dao.deleteSupportMessageById(id)
    suspend fun deleteSupportMessagesByConversation(conversationId: String) =
        dao.deleteSupportMessagesByConversation(conversationId)

    companion object {
        const val WHATSAPP_NUMBER = "8801880467335"
        const val WHATSAPP_DISPLAY = "+880 1880-467335"
        const val TELEGRAM_USERNAME = "johirul0188"
        const val TELEGRAM_URL = "https://t.me/johirul0188"
        const val WORKING_HOURS_START = 9 // 9 AM
        const val WORKING_HOURS_END = 22 // 10 PM

        val PAYMENT_ACCOUNTS = listOf(
            PaymentAccount(
                method = "bKash Personal",
                number = "01733256802",
                type = "Personal (Send Money / ক্যাশ ইন)",
                instruction = "বিকাশ অ্যাপ থেকে Send Money করুন অথবা ক্যাশ ইন করুন। রেফারেন্সে আপনার নাম বা UID লিখুন।",
                colorHex = 0xFFD8226A
            ),
            PaymentAccount(
                method = "Nagad Personal",
                number = "01874-954130",
                type = "Personal (Send Money / ক্যাশ আউট)",
                instruction = "নগদ অ্যাপ থেকে Send Money করুন। পেমেন্টের পর TrxID বা স্ক্রিনশট সংরক্ষণ করুন।",
                colorHex = 0xFFF7941D
            )
        )

        val BASE_TIKTOK_PACKAGES = listOf(
            TopupPackage("tt_100", ServiceCategory.TIKTOK, "100 Coins", "Standard Pack", "100", 180.0, badge = "Starter"),
            TopupPackage("tt_200", ServiceCategory.TIKTOK, "200 Coins", "Standard Pack", "200", 360.0),
            TopupPackage("tt_300", ServiceCategory.TIKTOK, "300 Coins", "Popular Pack", "300", 540.0),
            TopupPackage("tt_400", ServiceCategory.TIKTOK, "400 Coins", "Value Pack", "400", 720.0),
            TopupPackage("tt_500", ServiceCategory.TIKTOK, "500 Coins", "Best Value", "500", 900.0, badge = "Hot", isPopular = true),
            TopupPackage("tt_600", ServiceCategory.TIKTOK, "600 Coins", "Standard Pack", "600", 1080.0),
            TopupPackage("tt_700", ServiceCategory.TIKTOK, "700 Coins", "Standard Pack", "700", 1260.0),
            TopupPackage("tt_800", ServiceCategory.TIKTOK, "800 Coins", "Standard Pack", "800", 1440.0),
            TopupPackage("tt_900", ServiceCategory.TIKTOK, "900 Coins", "Standard Pack", "900", 1620.0),
            TopupPackage("tt_1000", ServiceCategory.TIKTOK, "1,000 Coins", "Pro Pack", "1000", 1800.0, badge = "Most Popular", isPopular = true),
            TopupPackage("tt_2000", ServiceCategory.TIKTOK, "2,000 Coins", "VIP Bulk Pack", "2000", 3600.0, badge = "VIP"),
            TopupPackage("tt_5000", ServiceCategory.TIKTOK, "5,000 Coins", "Mega Streamer", "5000", 9000.0, badge = "Mega VIP")
        )

        val BASE_CHAMET_PACKAGES = listOf(
            TopupPackage("ch_15k", ServiceCategory.CHAMET, "15,000 Diamonds", "Starter Pack", "15K", 360.0),
            TopupPackage("ch_20k", ServiceCategory.CHAMET, "20,000 Diamonds", "Standard Pack", "20K", 480.0),
            TopupPackage("ch_25k", ServiceCategory.CHAMET, "25,000 Diamonds", "Popular Pack", "25K", 595.0, badge = "Hot"),
            TopupPackage("ch_30k", ServiceCategory.CHAMET, "30,000 Diamonds", "Value Pack", "30K", 730.0),
            TopupPackage("ch_40k", ServiceCategory.CHAMET, "40,000 Diamonds", "Super Pack", "40K", 940.0),
            TopupPackage("ch_50k", ServiceCategory.CHAMET, "50,000 Diamonds", "Best Seller", "50K", 1150.0, badge = "Best Value", isPopular = true),
            TopupPackage("ch_100k", ServiceCategory.CHAMET, "100,000 Diamonds", "VIP Pro", "100K", 2300.0, badge = "VIP", isPopular = true),
            TopupPackage("ch_200k", ServiceCategory.CHAMET, "200,000 Diamonds", "Master Streamer", "200K", 4550.0, badge = "Special VIP"),
            TopupPackage("ch_500k", ServiceCategory.CHAMET, "500,000 Diamonds", "Legend Streamer", "500K", 11350.0, badge = "Mega VIP")
        )

        val BASE_FREEFIRE_PACKAGES = listOf(
            TopupPackage("ff_115", ServiceCategory.FREEFIRE, "115 Diamonds", "UID Topup", "115", 85.0),
            TopupPackage("ff_240", ServiceCategory.FREEFIRE, "240 Diamonds", "UID Topup", "240", 170.0),
            TopupPackage("ff_355", ServiceCategory.FREEFIRE, "355 Diamonds", "UID Topup", "355", 255.0),
            TopupPackage("ff_610", ServiceCategory.FREEFIRE, "610 Diamonds", "UID Topup", "610", 420.0, badge = "Hot", isPopular = true),
            TopupPackage("ff_1240", ServiceCategory.FREEFIRE, "1,240 Diamonds", "UID Topup", "1240", 840.0, badge = "Best Value"),
            TopupPackage("ff_2530", ServiceCategory.FREEFIRE, "2,530 Diamonds", "UID Topup", "2530", 1680.0, badge = "VIP"),
            TopupPackage("ff_weekly", ServiceCategory.FREEFIRE, "Weekly Membership", "7 Days VIP Pass", "1 Week", 175.0, badge = "Instant"),
            TopupPackage("ff_monthly", ServiceCategory.FREEFIRE, "Monthly Membership", "30 Days VIP Pass", "1 Month", 850.0, badge = "Best Offer", isPopular = true),
            TopupPackage("ff_lvlup", ServiceCategory.FREEFIRE, "Level Up Pass", "UID Instant Bonus", "Level Pass", 180.0)
        )

        val TIKTOK_PACKAGES = BASE_TIKTOK_PACKAGES
        val CHAMET_PACKAGES = BASE_CHAMET_PACKAGES
        val FREEFIRE_PACKAGES = BASE_FREEFIRE_PACKAGES

        fun getTikTokPackages(
            customPrices: Map<String, Double> = emptyMap(),
            customPackages: List<TopupPackage> = emptyList(),
            deletedIds: Set<String> = emptySet()
        ): List<TopupPackage> {
            val base = BASE_TIKTOK_PACKAGES.filterNot { it.id in deletedIds }.map { pkg ->
                val custom = customPrices[pkg.id]
                val overridePkg = customPackages.find { it.id == pkg.id }
                when {
                    overridePkg != null -> overridePkg
                    custom != null && custom > 0 -> pkg.copy(priceBdt = custom)
                    else -> pkg
                }
            }
            val added = customPackages.filter {
                it.category == ServiceCategory.TIKTOK &&
                    it.id !in deletedIds &&
                    BASE_TIKTOK_PACKAGES.none { b -> b.id == it.id }
            }
            return base + added
        }

        fun getChametPackages(
            customPrices: Map<String, Double> = emptyMap(),
            customPackages: List<TopupPackage> = emptyList(),
            deletedIds: Set<String> = emptySet()
        ): List<TopupPackage> {
            val base = BASE_CHAMET_PACKAGES.filterNot { it.id in deletedIds }.map { pkg ->
                val custom = customPrices[pkg.id]
                val overridePkg = customPackages.find { it.id == pkg.id }
                when {
                    overridePkg != null -> overridePkg
                    custom != null && custom > 0 -> pkg.copy(priceBdt = custom)
                    else -> pkg
                }
            }
            val added = customPackages.filter {
                it.category == ServiceCategory.CHAMET &&
                    it.id !in deletedIds &&
                    BASE_CHAMET_PACKAGES.none { b -> b.id == it.id }
            }
            return base + added
        }

        fun getFreeFirePackages(
            customPrices: Map<String, Double> = emptyMap(),
            customPackages: List<TopupPackage> = emptyList(),
            deletedIds: Set<String> = emptySet()
        ): List<TopupPackage> {
            val base = BASE_FREEFIRE_PACKAGES.filterNot { it.id in deletedIds }.map { pkg ->
                val custom = customPrices[pkg.id]
                val overridePkg = customPackages.find { it.id == pkg.id }
                when {
                    overridePkg != null -> overridePkg
                    custom != null && custom > 0 -> pkg.copy(priceBdt = custom)
                    else -> pkg
                }
            }
            val added = customPackages.filter {
                it.category == ServiceCategory.FREEFIRE &&
                    it.id !in deletedIds &&
                    BASE_FREEFIRE_PACKAGES.none { b -> b.id == it.id }
            }
            return base + added
        }

        fun getAllPackages(
            customPrices: Map<String, Double> = emptyMap(),
            customPackages: List<TopupPackage> = emptyList(),
            deletedIds: Set<String> = emptySet()
        ): List<TopupPackage> {
            return getTikTokPackages(customPrices, customPackages, deletedIds) +
                getChametPackages(customPrices, customPackages, deletedIds) +
                getFreeFirePackages(customPrices, customPackages, deletedIds)
        }

        fun getEpayTiers(config: AdminConfig = AdminConfig()): List<EpayRateTier> {
            return listOf(
                EpayRateTier("9.70 USD Fix", 9.70, "Fixed ৳${String.format(java.util.Locale.US, "%,.0f", config.epayRate970Fixed)}", config.epayRate970Fixed),
                EpayRateTier("10.00 USD Fix", 10.00, "Fixed ৳${String.format(java.util.Locale.US, "%,.0f", config.epayRate10Fixed)}", config.epayRate10Fixed),
                EpayRateTier("19.40 USD", 19.40, "৳${String.format(java.util.Locale.US, "%.1f", config.epayBaseRate)} / USD", 19.40 * config.epayBaseRate),
                EpayRateTier("20.00 USD", 20.00, "৳${String.format(java.util.Locale.US, "%.1f", config.epayBaseRate)} / USD", 20.00 * config.epayBaseRate),
                EpayRateTier("30+ USD (Bulk)", 30.0, "৳${String.format(java.util.Locale.US, "%.1f", config.epayRate30Bulk)} / USD", 30.0 * config.epayRate30Bulk, isPremium = true),
                EpayRateTier("500+ USD (Wholesale)", 500.0, "৳${String.format(java.util.Locale.US, "%.1f", config.epayRate500Wholesale)} / USD", 500.0 * config.epayRate500Wholesale, isPremium = true)
            )
        }

        fun calculateEpayBdt(usd: Double, config: AdminConfig = AdminConfig()): Pair<Double, Double> {
            if (usd <= 0.0) return Pair(0.0, 0.0)
            if (usd in 9.69..9.71) {
                return Pair(config.epayRate970Fixed, config.epayRate970Fixed / 9.70)
            }
            if (usd in 9.99..10.01) {
                return Pair(config.epayRate10Fixed, config.epayRate10Fixed / 10.0)
            }
            val rate = when {
                usd < 30.0 -> config.epayBaseRate
                usd < 500.0 -> config.epayRate30Bulk
                else -> config.epayRate500Wholesale
            }
            return Pair(usd * rate, rate)
        }

        val EPAY_TIERS = listOf(
            EpayRateTier("9.70 USD Fix", 9.70, "Fixed ৳1,170", 1170.0),
            EpayRateTier("10.00 USD Fix", 10.00, "Fixed ৳1,200", 1200.0),
            EpayRateTier("19.40 USD", 19.40, "৳121 / USD", 19.40 * 121.0),
            EpayRateTier("20.00 USD", 20.00, "৳121 / USD", 20.00 * 121.0),
            EpayRateTier("30+ USD (Bulk)", 30.0, "৳122 / USD", 30.0 * 122.0, isPremium = true),
            EpayRateTier("500+ USD (Wholesale)", 500.0, "৳123 / USD", 500.0 * 123.0, isPremium = true)
        )

        fun calculateEpayBdt(usd: Double): Pair<Double, Double> {
            return calculateEpayBdt(usd, AdminConfig())
        }

        fun isStoreOpen(): Boolean {
            val tz = TimeZone.getTimeZone("Asia/Dhaka")
            val cal = Calendar.getInstance(tz)
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            return hour in WORKING_HOURS_START until WORKING_HOURS_END
        }

        fun getDhakaTimeFormatted(): String {
            val tz = TimeZone.getTimeZone("Asia/Dhaka")
            val cal = Calendar.getInstance(tz)
            val hour = cal.get(Calendar.HOUR)
            val hourDisplay = if (hour == 0) 12 else hour
            val minute = String.format("%02d", cal.get(Calendar.MINUTE))
            val amPm = if (cal.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
            return "$hourDisplay:$minute $amPm (BST)"
        }
    }
}
