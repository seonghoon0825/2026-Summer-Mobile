package kr.hnu.ice.finalproject.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.hnu.ice.finalproject.MainActivity
import kr.hnu.ice.finalproject.R
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter
import javax.inject.Inject
import javax.inject.Singleton

/** 가격 인하 알림에 담을 상품 정보. productId는 알림 탭 시 상세로 이동하는 데 쓰인다. */
data class PriceDrop(
    val productId: String,
    val brand: String,
    val name: String,
    val oldPrice: Int,
    val newPrice: Int,
)

/**
 * 찜 상품 가격 인하 로컬 알림 발송기.
 * 알림 채널 생성과 발송을 담당한다. (POST_NOTIFICATIONS 권한 요청은 호출부에서 처리)
 * 상품별로 개별 알림을 발송하며, 탭하면 그 상품 상세로 이동한다.
 */
@Singleton
class PriceAlertNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    init {
        // 앱 시작 시(= 이 Singleton이 처음 생성될 때) 알림 채널을 등록한다.
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
     * 가격이 내려간 찜 상품들을 각각 개별 알림으로 발송한다.
     * 알림 권한이 없으면(거부 상태) 조용히 무시한다.
     */
    fun notifyPriceDrops(drops: List<PriceDrop>) {
        drops.forEach { drop ->
            showPriceDropNotification(
                productId = drop.productId,
                brand = drop.brand,
                name = drop.name,
                oldPrice = drop.oldPrice,
                newPrice = drop.newPrice,
            )
        }
    }

    /**
     * 상품 하나의 가격 인하 알림을 발송한다.
     * 탭하면 해당 상품 상세로 이동하는 PendingIntent가 연결된다.
     * 알림 권한이 없으면 조용히 무시하므로(크래시 없음), 호출부에서 권한을 먼저 요청한다.
     */
    fun showPriceDropNotification(
        productId: String,
        brand: String,
        name: String,
        oldPrice: Int,
        newPrice: Int,
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("찜한 상품 가격이 내려갔어요!")
            .setContentText(
                "$brand $name  ${PriceFormatter.format(oldPrice)} → ${PriceFormatter.format(newPrice)}",
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(detailPendingIntent(productId))
            .setAutoCancel(true)
            .build()

        val manager = NotificationManagerCompat.from(context)
        // POST_NOTIFICATIONS 권한이 없으면 발송이 무시된다(권한 없는 기기에서도 크래시 없음).
        if (manager.areNotificationsEnabled()) {
            // productId별 고유 id로 발송해 상품마다 개별 알림이 뜨게 한다.
            manager.notify(productId.hashCode(), notification)
        }
    }

    /** 알림 탭 → MainActivity를 productId와 함께 열어 상품 상세로 이동시킨다. */
    private fun detailPendingIntent(productId: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_PRODUCT_ID, productId)
            // 이미 실행 중이면 새 인스턴스를 만들지 않고 onNewIntent로 전달받는다.
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            productId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val CHANNEL_ID = "price_alert"
    }
}