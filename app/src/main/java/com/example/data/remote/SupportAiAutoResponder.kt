package com.example.data.remote

import com.example.BuildConfig
import com.example.data.local.AdminConfig
import com.example.data.model.OrderRecord
import com.example.data.model.ServiceCategory
import com.example.data.model.SupportChatMessage
import com.example.data.model.TopupPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AiSupportReplyResult(
    val replyText: String,
    val showTalkToAdminButton: Boolean = false,
    val detectedIntent: String = "GENERAL"
)

class SupportAiAutoResponder {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .build()

    suspend fun generateAutoReply(
        customerMessage: String,
        customerName: String,
        customerEmail: String,
        userWalletBalance: Double,
        adminConfig: AdminConfig,
        allPackages: List<TopupPackage>,
        allOrders: List<OrderRecord>,
        recentConversation: List<SupportChatMessage> = emptyList()
    ): AiSupportReplyResult = withContext(Dispatchers.IO) {
        val cleanEmail = customerEmail.trim().lowercase()
        val cleanMsg = customerMessage.trim()
        val lowerMsg = cleanMsg.lowercase(Locale.ROOT)

        // Security: Strictly filter orders belonging ONLY to this authenticated customer email
        val customerOrders = if (cleanEmail.isNotBlank()) {
            allOrders.filter { it.customerEmail.trim().lowercase() == cleanEmail }
                .sortedByDescending { it.timestamp }
        } else {
            emptyList()
        }

        val wantsAdminDirectly = isAskingForAdmin(lowerMsg)
        val isFinancialActionRequest = isRequestingRestrictedFinancialAction(lowerMsg)
        val isOrderStatusQuery = isAskingOrderStatus(lowerMsg)

        // Build deterministic knowledge response first (used as fallback or primary when Gemini key is placeholder)
        val localResult = buildSmartContextualReply(
            cleanMsg = cleanMsg,
            lowerMsg = lowerMsg,
            customerName = customerName.ifBlank { "গ্রাহক" },
            customerEmail = cleanEmail,
            userWalletBalance = userWalletBalance,
            adminConfig = adminConfig,
            allPackages = allPackages,
            customerOrders = customerOrders,
            wantsAdminDirectly = wantsAdminDirectly,
            isFinancialActionRequest = isFinancialActionRequest,
            isOrderStatusQuery = isOrderStatusQuery
        )

        // If user explicitly asked for Admin handoff, return immediately with the Talk to Admin button
        if (wantsAdminDirectly) {
            return@withContext localResult
        }

        // Check if a valid Gemini API key is configured in BuildConfig
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY.trim()
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && !apiKey.startsWith("YOUR_")) {
            val geminiText = callGeminiFlashWithContext(
                apiKey = apiKey,
                customerMessage = cleanMsg,
                customerName = customerName.ifBlank { "গ্রাহক" },
                customerEmail = cleanEmail,
                userWalletBalance = userWalletBalance,
                adminConfig = adminConfig,
                allPackages = allPackages,
                customerOrders = customerOrders,
                recentConversation = recentConversation
            )
            if (!geminiText.isNullOrBlank()) {
                val shouldShowAdminBtn = localResult.showTalkToAdminButton ||
                    geminiText.contains("Talk to Admin", ignoreCase = true) ||
                    geminiText.contains("এডমিন", ignoreCase = true)
                return@withContext AiSupportReplyResult(
                    replyText = geminiText.trim(),
                    showTalkToAdminButton = shouldShowAdminBtn,
                    detectedIntent = localResult.detectedIntent
                )
            }
        }

        return@withContext localResult
    }

    suspend fun generateAdminBossReply(
        adminMessage: String,
        executedActionSummary: String,
        adminConfig: AdminConfig,
        totalOrdersCount: Int,
        pendingOrdersCount: Int
    ): AiSupportReplyResult = withContext(Dispatchers.IO) {
        val cleanMsg = adminMessage.trim()
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY.trim()
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && !apiKey.startsWith("YOUR_")) {
            try {
                val systemPrompt = """
                    You are the loyal, obedient AI Assistant of CHAMET TOP UP BD.
                    You are speaking DIRECTLY to your Boss and Owner/Admin (${adminConfig.adminEmail}).
                    
                    CRITICAL RULES WHEN SPEAKING TO ADMIN:
                    1. Always recognize that the user is the Official Admin / Owner of this app. Address them respectfully in Bengali (e.g., "👑 জ্বী এডমিন স্যার / বস").
                    2. Whatever instruction, rule, rate, notice, or command the Admin gives you, confirm that you have memorized it and will 100% obey and apply it for all customers.
                    3. System Action already executed for this message: $executedActionSummary
                    4. Current saved Admin Custom Instructions in your memory:
                    ${adminConfig.adminCustomAiInstructions.ifBlank { "None yet" }}
                    5. Current Live Stats: Total Orders = $totalOrdersCount, Pending Orders = $pendingOrdersCount, bKash = ${adminConfig.bkashNumber}, Nagad = ${adminConfig.nagadNumber}, TikTok Rate = ৳${adminConfig.tiktokRatePerCoin}/coin, EPAY Rate = ৳${adminConfig.epayBaseRate}/USD, INR Rate = ৳${adminConfig.inrBaseRate}.
                    6. Reply concisely and clearly in Bengali.
                """.trimIndent()

                val rootJson = JSONObject().apply {
                    put("system_instruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                    })
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().put(JSONObject().put("text", cleanMsg)))
                        })
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent"
                val request = Request.Builder()
                    .url(url)
                    .addHeader("x-goog-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(rootJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyStr = response.body?.string().orEmpty()
                        val json = JSONObject(bodyStr)
                        val text = json.optJSONArray("candidates")
                            ?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text", "")
                            ?.trim()
                        if (!text.isNullOrBlank()) {
                            return@withContext AiSupportReplyResult(
                                replyText = text,
                                showTalkToAdminButton = false,
                                detectedIntent = "ADMIN_BOSS_COMMAND"
                            )
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }

        val fallbackReply = buildString {
            appendLine("👑 **আসসালামু আলাইকুম এডমিন স্যার (${adminConfig.adminEmail})!**")
            appendLine("আমি আপনাকে চিনতে পেরেছি—আপনি আমাদের অ্যাপের **Official Admin**।")
            appendLine()
            if (executedActionSummary.isNotBlank()) {
                appendLine("✅ **আপনার নির্দেশনা কার্যকর করা হয়েছে:**")
                appendLine(executedActionSummary)
                appendLine()
            }
            if (adminConfig.adminCustomAiInstructions.isNotBlank()) {
                appendLine("🧠 **আমার মেমোরিতে থাকা আপনার নির্দেশনাসমূহ:**")
                appendLine(adminConfig.adminCustomAiInstructions)
                appendLine()
            }
            appendLine("🫡 আপনি আমাকে যা বলবেন এবং যে নিয়ম বা তথ্য দেবেন, আমি ঠিক সেভাবেই সব কাস্টমারের সাথে কথা বলবো এবং পালন করবো!")
        }.trim()

        return@withContext AiSupportReplyResult(
            replyText = fallbackReply,
            showTalkToAdminButton = false,
            detectedIntent = "ADMIN_BOSS_COMMAND"
        )
    }

    private fun isAskingForAdmin(lowerMsg: String): Boolean {
        val keywords = listOf(
            "admin এর সাথে কথা",
            "এডমিন এর সাথে কথা",
            "অ্যাডমিন এর সাথে কথা",
            "এডমিনের সাথে কথা",
            "অ্যাডমিনের সাথে কথা",
            "talk to admin",
            "contact admin",
            "human admin",
            "human support",
            "live agent",
            "real person",
            "admin lagbe",
            "admin chai",
            "এডমিন চাই",
            "এডমিন লাগবে",
            "মানুষের সাথে কথা",
            "সাপোর্ট এডমিন",
            "admin kothay",
            "এডমিন কোথায়",
            "এডমিন কোথায়"
        )
        return keywords.any { lowerMsg.contains(it) }
    }

    private fun isRequestingRestrictedFinancialAction(lowerMsg: String): Boolean {
        val keywords = listOf(
            "verify my payment",
            "payment verify",
            "পেমেন্ট ভেরিফাই",
            "অর্ডার কমপ্লিট করে দিন",
            "complete my order",
            "order complete koren",
            "cancel my order",
            "order cancel",
            "অর্ডার বাতিল",
            "অর্ডার ক্যান্সেল",
            "refund",
            "রিফান্ড",
            "টাকা ফেরত",
            "taka ferot",
            "ব্যালেন্স এড করে দিন",
            "টাকা যোগ হয়নি",
            "balance add hoyni",
            "trxid",
            "ট্রানজেকশন আইডি"
        )
        return keywords.any { lowerMsg.contains(it) }
    }

    private fun isAskingOrderStatus(lowerMsg: String): Boolean {
        val keywords = listOf(
            "আমার অর্ডার কোথায়",
            "আমার অর্ডার কোথায়",
            "অর্ডার কোথায়",
            "অর্ডার কোথায়",
            "অর্ডার স্ট্যাটাস",
            "আমার অর্ডার",
            "order kothay",
            "amar order",
            "my order",
            "order status",
            "order update",
            "ডেলিভারি কই",
            "ডেলিভারি কখন",
            "কখন পাবো",
            "kokhon pabo",
            "পেন্ডিং",
            " প্রসেসিং",
            "processing",
            "jbd-",
            "auto-",
            "wal-",
            "epay-",
            "exc-",
            "sb-"
        )
        return keywords.any { lowerMsg.contains(it) }
    }

    private fun buildSmartContextualReply(
        cleanMsg: String,
        lowerMsg: String,
        customerName: String,
        customerEmail: String,
        userWalletBalance: Double,
        adminConfig: AdminConfig,
        allPackages: List<TopupPackage>,
        customerOrders: List<OrderRecord>,
        wantsAdminDirectly: Boolean,
        isFinancialActionRequest: Boolean,
        isOrderStatusQuery: Boolean
    ): AiSupportReplyResult {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        // 1. Direct request to talk to Admin
        if (wantsAdminDirectly) {
            val reply = buildString {
                appendLine("👋 প্রিয় $customerName,")
                appendLine("আপনি সরাসরি আমাদের **Official Admin**-এর সাথে কথা বলতে চেয়েছেন।")
                appendLine()
                appendLine("নিচের **\"💬 Talk to Admin\"** বাটনে ট্যাপ করলে **AI Auto-Reply বন্ধ হয়ে যাবে** এবং আমাদের একজন Human Admin সরাসরি আপনার সাথে এই চ্যাটে যুক্ত হবেন।")
                appendLine()
                appendLine("📞 জরুরি প্রয়োজনে WhatsApp: **${adminConfig.whatsappDisplay}**")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = true,
                detectedIntent = "ADMIN_HANDOFF"
            )
        }

        // 2. Order Status Check ("আমার অর্ডার কোথায়?" or Order ID lookup)
        if (isOrderStatusQuery) {
            val mentionedOrder = customerOrders.find { ord ->
                lowerMsg.contains(ord.orderNumber.lowercase(Locale.ROOT))
            }
            val reply = buildString {
                appendLine("📦 **আপনার অর্ডার স্ট্যাটাস রিপোর্ট (CHAMET TOP UP BD):**")
                appendLine()
                if (customerOrders.isEmpty()) {
                    appendLine("আপনার একাউন্টে (**$customerEmail**) বর্তমানে কোনো অর্ডার পাওয়া যায়নি।")
                    appendLine("• নতুন অর্ডার করতে হোম স্ক্রিন বা প্রোফাইল ওয়ালেট থেকে আপনার পছন্দের সার্ভিস সিলেক্ট করুন।")
                    appendLine("• আপনি যদি ইতিমধ্যে পেমেন্ট করে থাকেন এবং অর্ডার না দেখেন, তবে নিচের **\"💬 Talk to Admin\"** বাটনে ট্যাপ করে এডমিনকে জানান।")
                } else if (mentionedOrder != null) {
                    val statusBadge = when (mentionedOrder.status) {
                        "Completed", "Approved" -> "✅ Completed (সম্পন্ন হয়েছে)"
                        "Cancelled", "Rejected" -> "❌ Cancelled (বাতিল)"
                        else -> "⏳ Processing (প্রসেসিং চলছে)"
                    }
                    appendLine("🔖 **অর্ডার নং:** `#${mentionedOrder.orderNumber}`")
                    appendLine("🛍️ **সার্ভিস:** ${mentionedOrder.itemName} (${mentionedOrder.amountOrQuantity})")
                    appendLine("👤 **UID / Info:** ${mentionedOrder.userAccountId}")
                    appendLine("💰 **মূল্য:** ৳${String.format(Locale.US, "%,.0f", mentionedOrder.priceBdt)} BDT")
                    appendLine("📊 **বর্তমান অবস্থা:** $statusBadge")
                    appendLine("🕒 **সময়:** ${sdf.format(Date(mentionedOrder.timestamp))}")
                    if (mentionedOrder.status == "Processing") {
                        appendLine()
                        appendLine("ℹ️ আপনার অর্ডারটি বর্তমানে প্রসেসিংয়ে আছে। সাধারণত **৫–১৫ মিনিটের** মধ্যে ডেলিভারি সম্পন্ন হয়। সরাসরি এডমিনের সাহায্য চাইলে নিচের **\"💬 Talk to Admin\"** বাটনে ট্যাপ করুন।")
                    }
                } else {
                    val recent = customerOrders.take(3)
                    val processingCount = customerOrders.count { it.status == "Processing" }
                    val completedCount = customerOrders.count { it.status == "Completed" || it.status == "Approved" }
                    appendLine("মোট অর্ডার: **${customerOrders.size} টি** (চলমান: **$processingCount টি**, সফল: **$completedCount টি**)")
                    appendLine()
                    recent.forEachIndexed { idx, ord ->
                        val statusIcon = when (ord.status) {
                            "Completed", "Approved" -> "✅ Completed"
                            "Cancelled", "Rejected" -> "❌ Cancelled"
                            else -> "⏳ Processing"
                        }
                        appendLine("${idx + 1}. **#${ord.orderNumber}** — ${ord.itemName}")
                        appendLine("   • মূল্য: ৳${String.format(Locale.US, "%,.0f", ord.priceBdt)} | স্ট্যাটাস: **$statusIcon**")
                        appendLine("   • সময়: ${sdf.format(Date(ord.timestamp))}")
                    }
                    if (processingCount > 0) {
                        appendLine()
                        appendLine("⏱️ **ডেলিভারি তথ্য:** চলমান অর্ডারগুলো সাধারণত ৫–১৫ মিনিটের মধ্যে এডমিন ভেরিফাই করে ডেলিভারি সম্পন্ন করেন।")
                    }
                    appendLine()
                    appendLine("সরাসরি এডমিনের সাথে কথা বলতে নিচের **\"💬 Talk to Admin\"** বাটনে ট্যাপ করুন।")
                }
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = true,
                detectedIntent = "ORDER_STATUS"
            )
        }

        // 3. Strict Financial / Verification / Cancellation / Refund Guardrail
        if (isFinancialActionRequest) {
            val latestProcessing = customerOrders.firstOrNull { it.status == "Processing" }
            val reply = buildString {
                appendLine("🛡️ **ফিন্যান্সিয়াল সিকিউরিটি ও ভেরিফিকেশন বার্তা:**")
                appendLine()
                appendLine("সম্মানিত $customerName, নিরাপত্তার স্বার্থে AI কখনো নিজে থেকে:")
                appendLine("• পেমেন্ট বা TrxID ভেরিফাই করে না")
                appendLine("• অর্ডার Complete বা Cancel করে না")
                appendLine("• ওয়ালেট ব্যালেন্স পরিবর্তন বা Refund অনুমোদন করে না")
                if (latestProcessing != null) {
                    appendLine()
                    appendLine("📋 আপনার সর্বশেষ চলমান অর্ডার: **#${latestProcessing.orderNumber}** (${latestProcessing.itemName} - ৳${String.format(Locale.US, "%,.0f", latestProcessing.priceBdt)}) বর্তমানে **Processing** অবস্থায় আছে।")
                }
                appendLine()
                appendLine("👨‍💻 আমাদের **Official Admin** আপনার পেমেন্ট/রিকোয়েস্ট চেক করে দ্রুত সমাধান দেবেন। সরাসরি এডমিনের কাছে চ্যাট ট্রান্সফার করতে নিচের **\"💬 Talk to Admin\"** বাটনে ট্যাপ করুন।")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = true,
                detectedIntent = "FINANCIAL_GUARDRAIL"
            )
        }

        // 4. TikTok Coins
        if (lowerMsg.contains("tiktok") || lowerMsg.contains("টিকটক") || lowerMsg.contains("coin") || lowerMsg.contains("কয়েন") || lowerMsg.contains("কয়েন")) {
            val tiktokPkgs = allPackages.filter { it.category == ServiceCategory.TIKTOK }.take(6)
            val reply = buildString {
                appendLine("🪙 **TikTok Coins Official Top-Up (CHAMET TOP UP BD):**")
                appendLine()
                if (tiktokPkgs.isNotEmpty()) {
                    appendLine("📋 **জনপ্রিয় প্যাকেজ ও বর্তমান প্রাইস:**")
                    tiktokPkgs.forEach { pkg ->
                        appendLine("• **${pkg.name}** (${pkg.amount}) — **৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)} BDT**")
                    }
                    appendLine()
                }
                appendLine("💱 **কাস্টম কয়েন রেট:** প্রতি কয়েন **৳${String.format(Locale.US, "%.2f", adminConfig.tiktokRatePerCoin)} BDT**")
                appendLine("⚡ **ডেলিভারি সময়:** ৫ – ১৫ মিনিট (UID / ইউজারনেম দিয়ে কিংবা ওয়ালেট ব্যালেন্স দিয়ে ইনস্ট্যান্ট অর্ডার করতে পারবেন)।")
                appendLine("💳 **পেমেন্ট:** bKash (${adminConfig.bkashNumber}), Nagad (${adminConfig.nagadNumber}) অথবা Profile Wallet।")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "TIKTOK_COINS"
            )
        }

        // 5. Chamet Diamonds
        if (lowerMsg.contains("chamet") || lowerMsg.contains("চ্যামেট") || lowerMsg.contains("চেমেট") || lowerMsg.contains("diamond") || lowerMsg.contains("ডায়মন্ড") || lowerMsg.contains("ডায়মন্ড")) {
            val chametPkgs = allPackages.filter { it.category == ServiceCategory.CHAMET }.take(6)
            val reply = buildString {
                appendLine("💎 **Chamet Diamonds VIP Top-Up (CHAMET TOP UP BD):**")
                appendLine()
                if (chametPkgs.isNotEmpty()) {
                    appendLine("📋 **বর্তমান ডায়মন্ড প্যাকেজ ও প্রাইস:**")
                    chametPkgs.forEach { pkg ->
                        appendLine("• **${pkg.name}** (${pkg.amount}) — **৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)} BDT**")
                    }
                    appendLine()
                }
                appendLine("⚡ **ডেলিভারি:** শুধুমাত্র আপনার **Chamet User ID** দিলেই ২–১০ মিনিটের মধ্যে ডায়মন্ড পৌঁছে যাবে (কোনো পাসওয়ার্ড লাগে না)।")
                appendLine("👛 আপনার প্রোফাইল ওয়ালেট ব্যালেন্স (**৳${String.format(Locale.US, "%,.2f", userWalletBalance)} BDT**) দিয়েও সরাসরি কিনতে পারবেন!")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "CHAMET_DIAMONDS"
            )
        }

        // 6. EPAY Dollar Sell
        if (lowerMsg.contains("epay") || lowerMsg.contains("ইপে") || lowerMsg.contains("dollar") || lowerMsg.contains("ডলার") || lowerMsg.contains("usd")) {
            val reply = buildString {
                appendLine("💵 **EPAY Dollar Sell / Exchange রেট ও নিয়মাবলী:**")
                appendLine()
                appendLine("📊 **আজকের লাইভ EPAY রেট:**")
                appendLine("• **$10 Fixed:** ৳${String.format(Locale.US, "%,.0f", adminConfig.epayRate10Fixed)} BDT (৳${String.format(Locale.US, "%.1f", adminConfig.epayRate10Fixed / 10.0)}/USD)")
                appendLine("• **$30+ Bulk Rate:** ৳${String.format(Locale.US, "%.1f", adminConfig.epayRate30Bulk)} BDT / USD")
                appendLine("• **$500+ Wholesale Rate:** ৳${String.format(Locale.US, "%.1f", adminConfig.epayRate500Wholesale)} BDT / USD")
                appendLine("• **Base Rate:** ৳${String.format(Locale.US, "%.1f", adminConfig.epayBaseRate)} BDT / USD")
                appendLine()
                appendLine("📧 **Official Receiver EPAY Email:**")
                appendLine("`${adminConfig.epayEmail}`")
                appendLine()
                appendLine("✅ **কিভাবে বিক্রি করবেন:** অ্যাপের **EPAY** ট্যাবে গিয়ে আপনার ডলার পরিমাণ লিখুন, আমাদের EPAY ইমেইলে ডলার পাঠিয়ে Order ID ও আপনার bKash/Nagad নাম্বার সাবমিট করুন। ৫–১৫ মিনিটে পেমেন্ট পেয়ে যাবেন!")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "EPAY_DOLLAR"
            )
        }

        // 7. BD Exchange (BDT to INR)
        if (lowerMsg.contains("exchange") || lowerMsg.contains("এক্সচেঞ্জ") || lowerMsg.contains("inr") || lowerMsg.contains("rupee") || lowerMsg.contains("রুপি") || lowerMsg.contains("india") || lowerMsg.contains("ইন্ডিয়া") || lowerMsg.contains("ইন্ডিয়া") || lowerMsg.contains("phonepe") || lowerMsg.contains("upi") || lowerMsg.contains("paytm")) {
            val sample1000Inr = if (adminConfig.inrBaseRate > 0) 1000.0 / adminConfig.inrBaseRate else 690.0
            val reply = buildString {
                appendLine("🔄 **BD 🇧🇩 ➔ India 🇮🇳 Currency Exchange (BDT to INR):**")
                appendLine()
                appendLine("💱 **বর্তমান এক্সচেঞ্জ রেট:** **৳${String.format(Locale.US, "%.2f", adminConfig.inrBaseRate)} BDT = ₹1 INR**")
                appendLine("• উদাহরণ: **৳১,০০০ BDT** পাঠালে পাবেন আনুমানিক **₹${String.format(Locale.US, "%,.0f", sample1000Inr)} INR**")
                appendLine()
                appendLine("📲 **সাপোর্টেড মেথড:**")
                appendLine("• **Send (BD):** bKash, Nagad, Rocket")
                appendLine("• **Receive (India):** PhonePe, Google Pay, Paytm, UPI ID")
                appendLine()
                appendLine("📌 **নিয়ম:** ${adminConfig.noticeRule1}")
                appendLine("অ্যাপের **Exchange** ট্যাব থেকে সহজেই অর্ডার সাবমিট করতে পারবেন।")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "BD_EXCHANGE"
            )
        }

        // 8. Social Boost
        if (lowerMsg.contains("boost") || lowerMsg.contains("বুস্ট") || lowerMsg.contains("follower") || lowerMsg.contains("ফলোয়ার") || lowerMsg.contains("ফলোয়ার") || lowerMsg.contains("like") || lowerMsg.contains("লাইক") || lowerMsg.contains("view") || lowerMsg.contains("ভিউ") || lowerMsg.contains("subscriber") || lowerMsg.contains("সাবস্ক্রাইবার") || lowerMsg.contains("facebook") || lowerMsg.contains("youtube") || lowerMsg.contains("instagram")) {
            val activePlatforms = adminConfig.socialBoostPlatforms.filter { it.isEnabled }
            val activeServices = adminConfig.socialBoostServices.filter { it.isEnabled }.take(5)
            val reply = buildString {
                appendLine("🚀 **Social Media Boost Services (CHAMET TOP UP BD):**")
                appendLine()
                if (activePlatforms.isNotEmpty()) {
                    appendLine("🌐 **উপলব্ধ প্ল্যাটফর্ম:** ${activePlatforms.joinToString(", ") { it.name }}")
                    appendLine()
                }
                if (activeServices.isNotEmpty()) {
                    appendLine("🔥 **জনপ্রিয় বুস্ট সার্ভিসসমূহ (প্রতি ১০০০ রেট):**")
                    activeServices.forEach { srv ->
                        appendLine("• **${srv.name}** — **৳${String.format(Locale.US, "%,.0f", srv.pricePerThousandBdt)} / 1K** (Min: ${srv.minQuantity}, সময়: ${srv.averageTime})")
                    }
                    appendLine()
                }
                appendLine("✅ **কিভাবে অর্ডার করবেন:** হোম পেজ থেকে **Social Boost** অপশনে গিয়ে প্ল্যাটফর্ম ও সার্ভিস সিলেক্ট করে আপনার পাবলিক লিংক ও পরিমাণ দিন।")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "SOCIAL_BOOST"
            )
        }

        // 9. Payment Methods
        if (lowerMsg.contains("payment") || lowerMsg.contains("পেমেন্ট") || lowerMsg.contains("bkash") || lowerMsg.contains("বিকাশ") || lowerMsg.contains("nagad") || lowerMsg.contains("নগদ") || lowerMsg.contains("rocket") || lowerMsg.contains("রকেট") || lowerMsg.contains("নাম্বার") || lowerMsg.contains("number") || lowerMsg.contains("টাকা পাঠাব")) {
            val reply = buildString {
                appendLine("💳 **অফিসিয়াল পেমেন্ট নাম্বার ও নিয়মাবলী:**")
                appendLine()
                appendLine("🟣 **bKash (${adminConfig.bkashType}):**")
                appendLine("• নাম্বার: **`${adminConfig.bkashNumber}`**")
                appendLine("• নির্দেশনা: ${adminConfig.bkashInstruction}")
                appendLine()
                appendLine("🟠 **Nagad (${adminConfig.nagadType}):**")
                appendLine("• নাম্বার: **`${adminConfig.nagadNumber}`**")
                appendLine("• নির্দেশনা: ${adminConfig.nagadInstruction}")
                if (adminConfig.isRocketEnabled && adminConfig.rocketNumber.isNotBlank()) {
                    appendLine()
                    appendLine("🟣 **Rocket:** `${adminConfig.rocketNumber}`")
                }
                appendLine()
                appendLine("👛 **Profile Wallet:** আপনার ওয়ালেটে টাকা অ্যাড করে ১-ট্যাপে যেকোনো সার্ভিস ইনস্ট্যান্ট কিনতে পারবেন!")
                appendLine("⚠️ **সতর্কতা:** ${adminConfig.noticeAlert}")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "PAYMENT_METHODS"
            )
        }

        // 10. Delivery Information
        if (lowerMsg.contains("delivery") || lowerMsg.contains("ডেলিভারি") || lowerMsg.contains("কতক্ষণ") || lowerMsg.contains("সময় লাগে") || lowerMsg.contains("সময় লাগে") || lowerMsg.contains("কত সময়") || lowerMsg.contains("fast") || lowerMsg.contains("auto")) {
            val reply = buildString {
                appendLine("⚡ **ডেলিভারি সময় ও সার্ভিস তথ্য:**")
                appendLine()
                appendLine("• **Profile Wallet Instant Order:** ২ – ৫ মিনিটে অটো প্রসেসিং")
                appendLine("• **Chamet Diamonds & TikTok Coins:** ৫ – ১৫ মিনিট")
                appendLine("• **Free Fire UID Top-Up:** ২ – ১০ মিনিট")
                appendLine("• **EPAY Dollar & BD Exchange:** ৫ – ২০ মিনিট (ভেরিফিকেশনের পর)")
                appendLine("• **Social Media Boost:** ১৫ – ৪৫ মিনিট (সার্ভিস ভেদে)")
                appendLine()
                appendLine("🔔 অর্ডার কমপ্লিট হওয়ার সাথে সাথে অ্যাপে নোটিফিকেশন ও রিসিট পেয়ে যাবেন।")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "DELIVERY_INFO"
            )
        }

        // 11. Wallet, Referral & Profile Questions
        if (lowerMsg.contains("wallet") || lowerMsg.contains("ওয়ালেট") || lowerMsg.contains("ওয়ালেট") || lowerMsg.contains("ব্যালেন্স") || lowerMsg.contains("balance") || lowerMsg.contains("add money") || lowerMsg.contains("অ্যাড মানি") || lowerMsg.contains("refer") || lowerMsg.contains("রেফার") || lowerMsg.contains("কমিশন") || lowerMsg.contains("profile") || lowerMsg.contains("প্রোফাইল")) {
            val refRuleSummary = if (!adminConfig.isReferralEnabled) {
                "বর্তমানে রেফারেল প্রোগ্রাম সাময়িকভাবে বন্ধ আছে।"
            } else if (adminConfig.referralCommissionMode == "PERCENTAGE") {
                "প্রতি সফল অর্ডারে **${adminConfig.referralCommissionPercent}%** কমিশন (সর্বোচ্চ ৳${adminConfig.referralMaxCommissionBdt.toInt()})"
            } else {
                "প্রতি ৳${adminConfig.referralBaseOrderAmountBdt.toInt()} অর্ডারে **৳${adminConfig.referralCommissionBdt.toInt()} BDT** রেফারেল বোনাস"
            }
            val reply = buildString {
                appendLine("👛 **প্রোফাইল ওয়ালেট ও রেফারেল তথ্য:**")
                appendLine()
                appendLine("💰 **আপনার বর্তমান ওয়ালেট ব্যালেন্স:** **৳${String.format(Locale.US, "%,.2f", userWalletBalance)} BDT**")
                appendLine("➕ **টাকা Add করার নিয়ম:**")
                appendLine("১. নিচের মেনু থেকে **Profile (প্রোফাইল)** ট্যাবে যান।")
                appendLine("২. **\"➕ টাকা Add করুন\"** বাটনে ট্যাপ করে সর্বনিম্ন **৳${adminConfig.minWalletAddMoneyBdt.toInt()} BDT** bKash/Nagad-এ পেমেন্ট করে TrxID দিন।")
                appendLine("৩. ওয়ালেট ব্যালেন্স দিয়ে সরাসরি যেকোনো সার্ভিস কিনতে পারবেন!")
                appendLine()
                appendLine("🎁 **রেফারেল ইনকাম:** $refRuleSummary। আপনার রেফারেল কোড প্রোফাইলের **Referral** ট্যাবে পাবেন।")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "WALLET_PROFILE"
            )
        }

        // 12. Free Fire
        if (lowerMsg.contains("free fire") || lowerMsg.contains("freefire") || lowerMsg.contains("ফ্রি ফায়ার") || lowerMsg.contains("ফ্রি ফায়ার") || lowerMsg.contains("ff")) {
            val ffPkgs = allPackages.filter { it.category == ServiceCategory.FREEFIRE }.take(5)
            val reply = buildString {
                appendLine("🔥 **Free Fire Diamond Top-Up (UID):**")
                appendLine()
                if (ffPkgs.isNotEmpty()) {
                    ffPkgs.forEach { pkg ->
                        appendLine("• **${pkg.name}** (${pkg.amount}) — **৳${String.format(Locale.US, "%,.0f", pkg.priceBdt)} BDT**")
                    }
                    appendLine()
                }
                appendLine("⚡ শুধু আপনার **Player UID** দিয়ে অর্ডার করুন, ২–১০ মিনিটে ডায়মন্ড ডেলিভারি পাবেন!")
            }.trim()
            return AiSupportReplyResult(
                replyText = reply,
                showTalkToAdminButton = false,
                detectedIntent = "FREE_FIRE"
            )
        }

        // 13. General Greeting / Overview Help
        val processingOrders = customerOrders.count { it.status == "Processing" }
        val reply = buildString {
            appendLine("🤖 **আসসালামু আলাইকুম $customerName! CHAMET TOP UP BD AI সাপোর্টে স্বাগতম।**")
            appendLine()
            appendLine("আমি আপনাকে নিচের বিষয়গুলোতে তাৎক্ষণিক সাহায্য করতে পারি:")
            appendLine("• 🪙 **TikTok Coins** ও 💎 **Chamet Diamonds** রেট")
            appendLine("• 💵 **EPAY Dollar Sell** (রেট: ৳${adminConfig.epayRate30Bulk.toInt()}–৳${adminConfig.epayRate500Wholesale.toInt()}/USD)")
            appendLine("• 🔄 **BD ➔ India Exchange** (রেট: ৳${adminConfig.inrBaseRate} = ₹1 INR)")
            appendLine("• 🚀 **Social Boost** (Facebook, TikTok, YouTube, Instagram)")
            appendLine("• 💳 **পেমেন্ট নাম্বার:** bKash `${adminConfig.bkashNumber}` | Nagad `${adminConfig.nagadNumber}`")
            appendLine("• 📦 **অর্ডার স্ট্যাটাস** ও 👛 **ওয়ালেট ব্যালেন্স** (৳${String.format(Locale.US, "%,.2f", userWalletBalance)})")
            if (processingOrders > 0) {
                appendLine()
                appendLine("⏳ আপনার বর্তমানে **$processingOrders টি অর্ডার Processing** অবস্থায় আছে। স্ট্যাটাস জানতে লিখুন: **\"আমার অর্ডার কোথায়?\"**")
            }
            if (adminConfig.adminCustomAiInstructions.isNotBlank()) {
                appendLine()
                appendLine("📌 **এডমিনের বিশেষ আপডেট ও নির্দেশনা:**")
                appendLine(adminConfig.adminCustomAiInstructions)
            }
            appendLine()
            appendLine("আপনার প্রশ্নটি নিচে লিখুন অথবা সরাসরি এডমিনের সাথে কথা বলতে **\"💬 Talk to Admin\"** বাটনে ট্যাপ করুন।")
        }.trim()

        return AiSupportReplyResult(
            replyText = reply,
            showTalkToAdminButton = true,
            detectedIntent = "GENERAL_HELP"
        )
    }

    private fun callGeminiFlashWithContext(
        apiKey: String,
        customerMessage: String,
        customerName: String,
        customerEmail: String,
        userWalletBalance: Double,
        adminConfig: AdminConfig,
        allPackages: List<TopupPackage>,
        customerOrders: List<OrderRecord>,
        recentConversation: List<SupportChatMessage>
    ): String? {
        return try {
            val tiktokSummary = allPackages.filter { it.category == ServiceCategory.TIKTOK }
                .take(6)
                .joinToString("; ") { "${it.name} (${it.amount}): ৳${it.priceBdt.toInt()}" }
            val chametSummary = allPackages.filter { it.category == ServiceCategory.CHAMET }
                .take(6)
                .joinToString("; ") { "${it.name} (${it.amount}): ৳${it.priceBdt.toInt()}" }
            val ffSummary = allPackages.filter { it.category == ServiceCategory.FREEFIRE }
                .take(5)
                .joinToString("; ") { "${it.name} (${it.amount}): ৳${it.priceBdt.toInt()}" }
            val sbSummary = adminConfig.socialBoostServices.filter { it.isEnabled }
                .take(6)
                .joinToString("; ") { "${it.name}: ৳${it.pricePerThousandBdt.toInt()}/1K (Min ${it.minQuantity}, ${it.averageTime})" }
            val ordersSummary = if (customerOrders.isEmpty()) {
                "No orders found for $customerEmail."
            } else {
                customerOrders.take(5).joinToString("\n") {
                    "- Order #${it.orderNumber}: ${it.itemName} (${it.amountOrQuantity}), Price: ৳${it.priceBdt.toInt()}, Status: ${it.status}, UID: ${it.userAccountId}"
                }
            }

            val systemPrompt = """
                You are the official AI Support Assistant for "CHAMET TOP UP BD" (Admin Email: ${adminConfig.adminEmail}).
                Reply naturally, politely, and concisely in Bengali (with clear English terms for numbers/rates).
                
                LIVE BUSINESS DATA FROM FIREBASE:
                - Customer Name: $customerName | Email: $customerEmail | Wallet Balance: ৳${String.format(Locale.US, "%.2f", userWalletBalance)} BDT
                - bKash (${adminConfig.bkashType}): ${adminConfig.bkashNumber} (${adminConfig.bkashInstruction})
                - Nagad (${adminConfig.nagadType}): ${adminConfig.nagadNumber} (${adminConfig.nagadInstruction})
                - Rocket: ${if (adminConfig.isRocketEnabled) adminConfig.rocketNumber else "Disabled"}
                - WhatsApp Support: ${adminConfig.whatsappDisplay} | Telegram: @${adminConfig.telegramUsername}
                - TikTok Coins Packages: $tiktokSummary | Custom Coin Rate: ৳${adminConfig.tiktokRatePerCoin}/coin
                - Chamet Diamonds Packages: $chametSummary | Custom Diamond Rate: ৳${adminConfig.chametRatePerDiamond}/diamond
                - Free Fire Packages: $ffSummary
                - EPAY Dollar Sell Rates: $10 Fixed = ৳${adminConfig.epayRate10Fixed.toInt()}, $30+ Bulk = ৳${adminConfig.epayRate30Bulk}/USD, $500+ Wholesale = ৳${adminConfig.epayRate500Wholesale}/USD, Base Rate = ৳${adminConfig.epayBaseRate}/USD. Receiver EPAY Email: ${adminConfig.epayEmail}
                - BD Exchange (BDT to INR): ৳${adminConfig.inrBaseRate} BDT = ₹1 INR (${adminConfig.noticeRule1})
                - Social Boost Services: $sbSummary
                - Minimum Wallet Add Money: ৳${adminConfig.minWalletAddMoneyBdt.toInt()} BDT
                - Referral Rules: ${adminConfig.referralRulesText}
                - Official Notice: ${adminConfig.noticeWarning} | ${adminConfig.noticeAlert}
                
                ADMIN'S CUSTOM DIRECTIVES & TRAINING (YOU MUST OBEY THESE 100%):
                ${adminConfig.adminCustomAiInstructions.ifBlank { "Follow standard business rules above." }}
                
                CUSTOMER'S OWN ORDERS (Strictly confidential to $customerEmail):
                $ordersSummary
                
                STRICT SECURITY & FINANCIAL RULES:
                1. NEVER claim that you have verified a payment, completed an order, cancelled an order, changed wallet balance, or approved a refund.
                2. If the customer asks for payment verification, order completion/cancellation, wallet adjustment, or human admin support, tell them only the Human Admin can perform financial actions and instruct them to tap the "💬 Talk to Admin" button below.
                3. Keep answers well-structured with bullet points and emojis.
            """.trimIndent()

            val historySnippet = recentConversation.takeLast(4).joinToString("\n") { msg ->
                val roleLabel = when (msg.senderRole.uppercase()) {
                    "ADMIN" -> "Admin"
                    "AI" -> "AI"
                    else -> "Customer"
                }
                "$roleLabel: ${msg.message}"
            }

            val rootJson = JSONObject().apply {
                put("system_instruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put(
                            "text",
                            if (historySnippet.isNotBlank()) "Recent chat:\n$historySnippet\n\nCustomer new message: $customerMessage" else customerMessage
                        )))
                    })
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent"
            val request = Request.Builder()
                .url(url)
                .addHeader("x-goog-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(rootJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val bodyStr = response.body?.string() ?: return null
                val json = JSONObject(bodyStr)
                val candidates = json.optJSONArray("candidates") ?: return null
                if (candidates.length() == 0) return null
                val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
                val parts = content.optJSONArray("parts") ?: return null
                if (parts.length() == 0) return null
                parts.getJSONObject(0).optString("text", "").takeIf { it.isNotBlank() }
            }
        } catch (_: Exception) {
            null
        }
    }
}
