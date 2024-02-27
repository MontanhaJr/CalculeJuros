package com.montanhajr.calculejuros

import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.montanhajr.calculejuros.network.Api
import com.montanhajr.calculejuros.network.RetrofitBuilder
import com.montanhajr.calculejuros.ui.theme.CalculeJurosTheme
import com.montanhajr.calculejuros.util.CurrencyAmountInputVisualTransformation
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculeJurosTheme {
                Surface {
                    Greeting()
                }
            }
        }

        val service = RetrofitBuilder.createNetworkService()

        lifecycleScope.launch {
            val cdi = service.getCDI()
            Log.i("CDI", cdi.value[cdi.value.size - 2].valValor.toString())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    var originalValue by remember {
        mutableStateOf("")
    }
    var installmentsValue by remember {
        mutableStateOf("")
    }
    var installmentsAmount by remember {
        mutableStateOf("")
    }
    var result by remember {
        mutableStateOf(0.0)
    }
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp),
            text = "Insira os valores para descobrir se vale a pena parcelar:"
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            OutlinedTextField(
                value = originalValue, onValueChange = {
                    originalValue = if (it.startsWith("0")) "" else it
                },
                visualTransformation = CurrencyAmountInputVisualTransformation(),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .weight(2f),
                label = {
                    Text(text = "Valor à vista")
                },
                shape = RoundedCornerShape(30),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                )
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            OutlinedTextField(
                value = installmentsValue, onValueChange = {
                    installmentsValue = if (it.startsWith("0")) "" else it
                },
                visualTransformation = CurrencyAmountInputVisualTransformation(),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .weight(4f),
                label = {
                    Text(text = "Valor das parcelas")
                },
                shape = RoundedCornerShape(30),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = installmentsAmount, onValueChange = {
                    installmentsAmount = it
                },
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .weight(3f),
                label = {
                    Text(text = "N° de parcelas")
                },
                shape = RoundedCornerShape(30),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )
            )
        }

        ElevatedButton(
            onClick = {
                result = calculateResult(
                    originalValue.toDouble(),
                    installmentsValue.toDouble()
                )
                Log.i(
                    "BUTTON CLICKED",
                    "$originalValue $installmentsValue $installmentsAmount $result"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(30)
        ) {
            Text(text = "Vale parcelar?")
        }

        if (!result.equals(0.0)) {
            Text(
                text = "Valor total parcelado: $result",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                fontSize = 32.sp,
                color = Color.Blue,
                fontWeight = FontWeight.ExtraBold
            )
            val totalFees = result.minus(originalValue.toDouble())
            Text(
                text = "Juros total: $totalFees (${totalFees.percent(originalValue.toDouble())}%)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                fontSize = 32.sp,
                color = Color.Red,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

fun Double.percent(originalValue: Double): Double {
    return this.times(100).div(originalValue)
}

fun calculateResult(
    installmentsValue: Double,
    installmentsAmount: Double
): Double {
    return installmentsValue.times(installmentsAmount)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculeJurosTheme {
        Greeting()
    }
}