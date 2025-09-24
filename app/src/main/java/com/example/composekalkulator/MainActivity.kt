package com.example.composekalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var operand by remember { mutableStateOf<Double?>(null) }
    var operator by remember { mutableStateOf<Char?>(null) }
    var isNewEntry by remember { mutableStateOf(true) }

    fun clearAll() {
        display = "0"
        operand = null
        operator = null
        isNewEntry = true
    }

    fun backspace() {
        if (isNewEntry) return
        display = if (display.length > 1) display.dropLast(1) else "0"
    }

    fun inputDot() {
        if (isNewEntry) {
            display = "0."
            isNewEntry = false
        } else if (!display.contains(".")) {
            display += "."
        }
    }

    fun inputNumber(n: String) {
        display = if (isNewEntry || display == "0") n else display + n
        isNewEntry = false
    }

    // --- Pindahkan calculate() ke atas agar dikenal saat dipanggil di setOperator() ---
    fun calculate() {
        val second = display.toDoubleOrNull() ?: 0.0
        val first = operand ?: second
        val result = when (operator) {
            '+' -> first + second
            '-' -> first - second
            '×' -> first * second
            '÷' -> if (second == 0.0) Double.NaN else first / second
            else -> second
        }
        display = if (result.isNaN() || result.isInfinite()) {
            "Error"
        } else {
            val asLong = result.toLong()
            if (result == asLong.toDouble()) asLong.toString() else result.toString()
        }
        operand = null
        operator = null
        isNewEntry = true
    }

    fun setOperator(op: Char) {
        if (operator != null && !isNewEntry) {
            calculate()
        } else {
            operand = display.toDoubleOrNull() ?: 0.0
        }
        operator = op
        isNewEntry = true
    }

    fun percent() {
        val value = display.toDoubleOrNull() ?: return
        val p = if (operand != null && operator in listOf('+','-','×','÷')) {
            (operand!! * value) / 100.0
        } else value / 100.0
        display = p.toString().trimEnd('0').trimEnd('.')
        isNewEntry = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = display,
                fontSize = 48.sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 52.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        val btnModifier = Modifier
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))

        // >>> Tambahkan anotasi @Composable di sini <<<
        @Composable
        fun CalcButton(
            label: String,
            onClick: () -> Unit,
            filled: Boolean = false,
            emphasis: Boolean = false,
            modifier: Modifier = Modifier
        ) {
            if (filled) {
                Button(
                    onClick = onClick,
                    modifier = modifier.then(btnModifier)
                ) {
                    Text(label, fontSize = 20.sp)
                }
            } else {
                OutlinedButton(
                    onClick = onClick,
                    modifier = modifier.then(btnModifier)
                ) {
                    Text(label, fontSize = 20.sp)
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                CalcButton("AC", onClick = { clearAll() }, emphasis = true, modifier = Modifier.weight(1f))
                CalcButton("⌫", onClick = { backspace() }, emphasis = true, modifier = Modifier.weight(1f))
                CalcButton("%", onClick = { percent() }, emphasis = true, modifier = Modifier.weight(1f))
                CalcButton("÷", onClick = { setOperator('÷') }, filled = true, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                CalcButton("7", onClick = { inputNumber("7") }, modifier = Modifier.weight(1f))
                CalcButton("8", onClick = { inputNumber("8") }, modifier = Modifier.weight(1f))
                CalcButton("9", onClick = { inputNumber("9") }, modifier = Modifier.weight(1f))
                CalcButton("×", onClick = { setOperator('×') }, filled = true, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                CalcButton("4", onClick = { inputNumber("4") }, modifier = Modifier.weight(1f))
                CalcButton("5", onClick = { inputNumber("5") }, modifier = Modifier.weight(1f))
                CalcButton("6", onClick = { inputNumber("6") }, modifier = Modifier.weight(1f))
                CalcButton("-", onClick = { setOperator('-') }, filled = true, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                CalcButton("1", onClick = { inputNumber("1") }, modifier = Modifier.weight(1f))
                CalcButton("2", onClick = { inputNumber("2") }, modifier = Modifier.weight(1f))
                CalcButton("3", onClick = { inputNumber("3") }, modifier = Modifier.weight(1f))
                CalcButton("+", onClick = { setOperator('+') }, filled = true, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                CalcButton("0", onClick = { inputNumber("0") }, modifier = Modifier.weight(2f))
                CalcButton(".", onClick = { inputDot() }, modifier = Modifier.weight(1f))
                CalcButton("=", onClick = { calculate() }, filled = true, modifier = Modifier.weight(1f))
            }
        }
    }
}
