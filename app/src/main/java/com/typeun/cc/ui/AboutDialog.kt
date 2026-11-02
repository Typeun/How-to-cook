package com.typeun.cc.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val GITHUB_URL = "https://github.com/Anduin2017/HowToCook"

@Composable
fun AboutDialog(onDismiss: () -> Unit, onCopied: () -> Unit) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(4.dp),
        title = { Text("来源与版权说明", fontSize = 20.sp) },
        text = {
            SelectionContainer {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "本应用的内容均整理自以下开源项目，特此说明：",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFECECEC))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("程序员做饭指南 · HowToCook", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                GITHUB_URL,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colors.primaryVariant,
                                modifier = Modifier.clickable { openGitHub(context) }
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFCC80))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("非原创文案", color = Color(0xFFE65100), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "本应用所有菜谱文案、按份数配比的「计算公式」与操作步骤，均直接取自上述开源项目，著作权归原作者 Anduin2017 及其贡献者所有。本应用仅把原始 Markdown 解析为结构化数据以便离线浏览与检索，未做任何创作性改写，文案并非本应用原创。如原作者或相关权利人提出异议，本应用将立即移除对应内容。",
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                clipboard.setText(AnnotatedString(GITHUB_URL))
                onDismiss()
                onCopied()
            }) { Text("复制仓库地址", color = MaterialTheme.colors.primaryVariant) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("知道了", color = MaterialTheme.colors.primary) }
        }
    )
}

private fun openGitHub(context: Context) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL)))
    }
}
