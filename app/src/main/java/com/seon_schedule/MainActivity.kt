package com.seon_schedule

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seon_schedule.ui.theme.SEONscheduleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SEONscheduleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var l by remember { mutableStateOf(JSONObject()) }
                    var get by remember { mutableStateOf(false) }
                    val scdList = remember { mutableStateListOf<String>() }
                    var grade by remember { mutableStateOf("1") }
                    var class_ by remember { mutableStateOf("1") }
                    val context = LocalContext.current

                    CoroutineScope(Dispatchers.IO).launch {
                        if (!get) {
                            val currentDate = LocalDate.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                            val formatter2 = DateTimeFormatter.ofPattern("yyyy")
                            val formattedDate = currentDate.format(formatter)
                            val formattedDate2 = currentDate.format(formatter2)
                            grade = loadInt(context, "grade").toString()
                            class_ = loadInt(context, "class").toString()
                            l = getSCD(formattedDate, formattedDate2, "1", grade, class_, BuildConfig.NICE_API)
                            get = true
                        }
                    }

                    LaunchedEffect(l) {
                        scdList.clear()
                        for (i in l.keys().iterator()){
                            if (i.equals("type")) continue
                            Log.i("czxxzc", i)
                            scdList.add(l.getJSONArray(i).join(", "))
                        }
                        Log.i("NASJ",scdList.toString())
                    }

                    Column (
                        Modifier.padding(innerPadding)
                    ) {

                        var opened by remember { mutableStateOf(false) }
                        Text(
                            "오늘의 시간표",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        val tim = listOf("08:40\n09:30", "09:40\n10:30", "10:40\n11:30", "11:40\n12:30","13:20\n14:10","14:20\n15:10","15:20\n16:10")
                        LazyColumn {
                            itemsIndexed(scdList) { index, i ->
                                var modi = Modifier.height(50.dp).fillMaxWidth()
                                if (index%2==0) modi = modi.background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                val now = LocalDateTime.now()
                                val formatter = DateTimeFormatter.ofPattern("HHmm")
                                val time = now.format(formatter).toString()
                                val f_ = tim.get(index).split("\n")
                                val t = f_[1].replace(":", "").toInt()
                                val f = f_[0].replace(":", "").toInt()
                                var now_perio = false
                                Log.i("SSS", time.toString())
                                Log.i("SSS", f.toString())
                                Log.i("SSS", t.toString())
                                if (time.toInt() in f..t){
                                    Log.i("kkk", "f is pass")
                                    modi = modi.background(MaterialTheme.colorScheme.tertiaryContainer)
                                }

                                Row(
                                    modifier = modi,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val perio = index + 1
                                    Text(perio.toString(), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                                    VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 1.dp)
                                    Text(tim[index])
                                    VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 1.dp)
                                    Text(i.replace("\"", ""), modifier = Modifier
                                        .padding(10.dp,0.dp,0.dp,0.dp))
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .animateContentSize()
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .clickable {
                                    opened = !opened
                                    Log.i("!", opened.toString())
                                }
                        ) {
                            Column {
                                var text = "클릭하여 환경설정 열기"
                                if (opened) text = "클릭하여 환경설정 닫기"
                                Text(text, modifier = Modifier.fillMaxWidth().padding(10.dp), textAlign = TextAlign.Center)
                                var expanded by remember { mutableStateOf(false) }
                                AnimatedVisibility(
                                    visible = opened,
                                    enter = expandVertically(animationSpec = tween(150)),
                                    exit = shrinkVertically(animationSpec = tween(200))
                                ) {
                                    Column {
                                        Row {
                                            val Goptions = listOf(1,2,3)
                                            val Coptions = listOf(1,2,3,4,5,6,7,8)
                                            var expanded by remember { mutableStateOf(false) }
                                            var expanded2 by remember { mutableStateOf(false) }
                                            ExposedDropdownMenuBox(
                                                modifier = Modifier
                                                    .padding(innerPadding)
                                                    .weight(1f),
                                                expanded = expanded,
                                                onExpandedChange = {
                                                    expanded = !expanded
                                                }
                                            ) {

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    TextField(
                                                        modifier = Modifier.menuAnchor(),
                                                        value = grade,
                                                        onValueChange = { },
                                                        readOnly = true
                                                    )
                                                }
                                                ExposedDropdownMenu(expanded = expanded,
                                                    onDismissRequest = {
                                                        expanded = false
                                                    }
                                                ) {
                                                    Goptions.forEachIndexed { index, item ->
                                                        DropdownMenuItem(
                                                            text = {
                                                                Text(item.toString())
                                                            },
                                                            onClick = {
                                                                grade = Goptions[index].toString()
                                                                expanded = false
                                                            },
                                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                                        )
                                                    }
                                                }

                                            }


                                            ExposedDropdownMenuBox(
                                                modifier = Modifier
                                                    .padding(innerPadding)
                                                    .weight(1f),
                                                expanded = expanded2,
                                                onExpandedChange = {
                                                    expanded2= !expanded2
                                                }
                                            ) {

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    TextField(
                                                        modifier = Modifier.menuAnchor(),
                                                        value = class_,
                                                        onValueChange = { },
                                                        readOnly = true
                                                    )
                                                }
                                                ExposedDropdownMenu(expanded = expanded2,
                                                    onDismissRequest = {
                                                        expanded2 = false
                                                    }
                                                ) {
                                                    Coptions.forEachIndexed { index, item ->
                                                        DropdownMenuItem(
                                                            text = {
                                                                Text(item.toString())
                                                            },
                                                            onClick = {
                                                                class_ = Coptions[index].toString()
                                                                expanded2 = false
                                                            },
                                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                                        )
                                                    }
                                                }

                                            }
                                        }
                                        val context = LocalContext.current
                                        Button(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RectangleShape,
                                            onClick = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val currentDate = LocalDate.now()
                                                    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                                                    val formatter2 = DateTimeFormatter.ofPattern("yyyy")
                                                    val formattedDate = currentDate.format(formatter)
                                                    val formattedDate2 = currentDate.format(formatter2)
                                                    saveInt(context, "grade", grade.toInt())
                                                    saveInt(context, "class", class_.toInt())
                                                    l = getSCD(formattedDate, formattedDate2, "1", grade, class_, BuildConfig.NICE_API)
                                                }
                                            }
                                        ) {
                                            Text("시간표 받아오기")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}