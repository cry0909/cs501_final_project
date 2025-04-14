import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    onPermissionResult: (Boolean) -> Unit
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // 当权限状态发生变化时，检查是否已被授予
    LaunchedEffect(locationPermissionState.status) {
        onPermissionResult(locationPermissionState.status is PermissionStatus.Granted)
    }

    // 在组合首次进入时触发权限请求
    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }
}
