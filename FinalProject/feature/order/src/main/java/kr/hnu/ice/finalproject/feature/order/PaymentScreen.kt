package kr.hnu.ice.finalproject.feature.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hnu.ice.finalproject.core.common.util.PriceFormatter
import kr.hnu.ice.finalproject.core.designsystem.component.AppButton

/**
 * 결제 화면. "지금 결제하기"를 누르면 2초 로딩(시뮬) 후 성공 처리된다.
 *
 * @param onPaymentSuccess 결제 성공 시 완료 화면으로 이동
 */
@Composable
fun PaymentScreen(
    viewModel: OrderViewModel,
    onPaymentSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val processing = state.paymentState == PaymentState.Processing

    // 결제 성공 → 완료 화면으로
    androidx.compose.runtime.LaunchedEffect(state.paymentState) {
        if (state.paymentState == PaymentState.Success) onPaymentSuccess()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 처리 중에는 뒤로가기 막기
            OrderTopBar(title = "결제", onBack = { if (!processing) onBack() })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("결제 수단", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(state.paymentMethod.label, style = MaterialTheme.typography.bodyLarge)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("최종 결제 금액", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        PriceFormatter.format(state.total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            AppButton(
                text = "지금 결제하기",
                onClick = viewModel::pay,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = state.canPay && !processing,
            )
        }

        // 결제 처리 중 오버레이
        if (processing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Text("결제 처리 중...", color = Color.White)
                }
            }
        }
    }
}