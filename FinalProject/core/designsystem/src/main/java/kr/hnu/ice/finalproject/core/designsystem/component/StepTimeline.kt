package kr.hnu.ice.finalproject.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.hnu.ice.finalproject.core.designsystem.theme.AppTheme

/**
 * 단계 진행 타임라인(가로형). 배송 상태 등 순차 단계 시각화에 쓰는 도메인 비의존 컴포넌트.
 *
 * @param steps 단계 라벨들 (예: 주문완료/결제완료/배송중/배송완료)
 * @param currentStep 현재 단계 인덱스(0-based). 이 단계까지 도달한 것으로 표시된다.
 */
@Composable
fun StepTimeline(
    steps: List<String>,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    if (steps.isEmpty()) return
    val lastIndex = steps.lastIndex

    Column(modifier = modifier.fillMaxWidth()) {
        // 노드 + 연결선
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            steps.forEachIndexed { index, _ ->
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 왼쪽 연결선(첫 노드는 숨김) — 이전 단계까지 도달했으면 활성
                    Connector(
                        active = index in 1..currentStep,
                        visible = index != 0,
                        modifier = Modifier.weight(1f),
                    )
                    StepNode(state = stepStateOf(index, currentStep))
                    // 오른쪽 연결선(마지막 노드는 숨김) — 다음 단계까지 도달했으면 활성
                    Connector(
                        active = index < currentStep,
                        visible = index != lastIndex,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // 라벨
        Row(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index <= currentStep) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (index == currentStep) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}

private enum class StepState { COMPLETED, CURRENT, PENDING }

private fun stepStateOf(index: Int, currentStep: Int): StepState = when {
    index < currentStep -> StepState.COMPLETED
    index == currentStep -> StepState.CURRENT
    else -> StepState.PENDING
}

@Composable
private fun StepNode(state: StepState) {
    val filledColor = MaterialTheme.colorScheme.primary
    val pendingColor = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (state == StepState.PENDING) pendingColor else filledColor),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            StepState.COMPLETED -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp),
            )
            // 현재 단계: 안쪽 점으로 강조
            StepState.CURRENT -> Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary),
            )
            StepState.PENDING -> Unit
        }
    }
}

@Composable
private fun Connector(active: Boolean, visible: Boolean, modifier: Modifier) {
    val color = when {
        !visible -> Color.Transparent
        active -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    Box(
        modifier = modifier
            .height(2.dp)
            .background(color),
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun StepTimelinePreview() {
    AppTheme {
        Column {
            StepTimeline(
                steps = listOf("주문완료", "결제완료", "배송중", "배송완료"),
                currentStep = 2,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}