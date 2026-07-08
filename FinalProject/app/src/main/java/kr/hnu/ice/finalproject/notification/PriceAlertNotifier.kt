package kr.hnu.ice.finalproject.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.hnu.ice.finalproject.R
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter
import javax.inject.Inject
import javax.inject.Singleton

/** 가격 인하 알림에 담을 상품 정보. */
data class PriceDrop(
    val brand: String,
    val name: String,
    val oldPrice: Int,
    val newPrice: Int,
)

/**
 * 찜 상품 가격 인하 로컬 알림 발송기.
 * 알림 채널 생성과 발송을 담당한다. (권한 요청은 호출부에서 처리)
 */
@Singleton
class PriceAlertNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "가격 인하 알림",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "찜한 상품의 가격이 내려가면 알려드려요."
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    /**
     * 가격이 내려간 찜 상품들을 알림으로 발송한다.
     * 알림 권한이 없으면(거부 상태) 조용히 무시한다.
     */
    fun notifyPriceDrops(drops: List<PriceDrop>) {
        if (drops.isEmpty()) return

        val title = if (drops.size == 1) {
            "찜한 상품 가격이 내려갔어요!"
        } else {
            "찜한 상품 ${drops.size}개의 가격이 내려갔어요!"
        }
        val lines = drops.joinToString("\n") { drop ->
            "${drop.brand} ${drop.name}  ${PriceFormatter.format(drop.oldPrice)} → ${PriceFormatter.format(drop.newPrice)}"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(drops.first().let { "${it.brand} ${it.name} 외 특가" })
            .setStyle(NotificationCompat.BigTextStyle().bigText(lines))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = NotificationManagerCompat.from(context)
        // POST_NOTIFICATIONS 권한이 없으면 발송이 무시된다(호출부에서 권한을 먼저 요청함).
        if (manager.areNotificationsEnabled()) {
            manager.notify(NOTIFICATION_ID, notification)
        }
    }

    private companion object {
        const val CHANNEL_ID = "price_alert"
        const val NOTIFICATION_ID = 1001
    }
}