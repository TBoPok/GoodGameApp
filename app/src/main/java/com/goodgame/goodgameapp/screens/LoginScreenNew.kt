package com.goodgame.goodgameapp.screens

import android.graphics.Matrix
import android.graphics.RectF
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser
import androidx.navigation.NavHostController
import com.goodgame.goodgameapp.R
import com.goodgame.goodgameapp.models.ClubModel
import com.goodgame.goodgameapp.navigation.Screen
import com.goodgame.goodgameapp.retrofit.Status
import com.goodgame.goodgameapp.screens.views.*
import com.goodgame.goodgameapp.ui.theme.FocusBlue
import com.goodgame.goodgameapp.ui.theme.FocusGreen
import com.goodgame.goodgameapp.ui.theme.InActiveWhite
import com.goodgame.goodgameapp.ui.theme.SemiWhite
import com.goodgame.goodgameapp.viewmodel.LoginViewModel

@Composable
fun LoginScreenNew(navController: NavHostController, viewModel: LoginViewModel) {
    val phoneNumber = remember { mutableStateOf("") }
    val currentClub = remember { mutableStateOf<ClubModel?>(null) }

    val showClubListView = remember { mutableStateOf(false)}

    /*
    0 -> "Для продолжения необходимо войти в свой профиль",
                        "это займёт не более 30 секунд"
    1 -> "УПС...\nАккаунт в этом клубе не найден",
                        "давай попробуем еще раз"
    2 -> "Что-то не так с интернетом"
            "Проверьте подключение и попробуйте снова"

     */
    val topTextState = remember { mutableStateOf(0)}

    val enterInAccountButtonActive = remember { mutableStateOf(false) }
    enterInAccountButtonActive.value = phoneNumber.value.length == 10 && currentClub.value != null

    val keyboardController = LocalFocusManager.current

    val loadingViewActive = remember {mutableStateOf(false)}

    Column(modifier = Modifier
        .background(Color(0xff00131d))
    ) {

        Column() {
            Row() {
                TopImage {
                    Image(painterResource(R.drawable.white_logo),
                        contentDescription = "Top logo")
                }
            }
            Row (modifier = Modifier.padding(14.dp).weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Column()
                {
//                    Spacer(modifier = Modifier.padding(15.dp))
                    when(topTextState.value) {
                        0 -> LoginHeadText(
                            "Для продолжения необходимо войти в свой профиль",
                            "это займёт не более 30 секунд"
                        )
                        1 -> LoginHeadText (
                            "УПС...\nАккаунт в этом клубе не найден",
                            "давай попробуем еще раз")
                        2 -> LoginHeadText (
                            "Что-то не так с интернетом",
                            "Проверьте подключение и попробуйте снова")
                    }
                    Spacer(modifier = Modifier.padding(20.dp))
                    PhoneNumberNew_Input(phoneNumber)
                    Spacer(modifier = Modifier.padding(5.dp))
                    ChoseClubNew_Input(currentClub) {
                        keyboardController.clearFocus()
                        showClubListView.value = true
                    }
                    Spacer(modifier = Modifier.padding(15.dp))
                    MetallButton(isActive = enterInAccountButtonActive, activeText = "Войти в аккаунт") { // Enter in account button
                        loadingViewActive.value = true
                        viewModel.phoneNumber.value = phoneNumber.value
                        viewModel.club.value = currentClub.value
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    WeDontUseYourData()
                }

            }
            Row() {
                BottomImage {
                    IDontHaveAccount() {
                        navController.navigate(Screen.RegistrationScreen.route)
                    }
                }
            }

        }
    }
    if (showClubListView.value == true) {
        ClubListView(viewModel = viewModel) { chosenClub ->
            currentClub.value = chosenClub.value
            showClubListView.value = false
        }
    }

    if (loadingViewActive.value == true) {
        LoadingView()
        viewModel.sendLoginData().observe(LocalLifecycleOwner.current) {
            when (it.status) {
                Status.SUCCESS -> {
                    loadingViewActive.value = false
                    if (it.data?.status == true)
                        navController.navigate("LoginCodeConfirmScreen")
                    else {
                        topTextState.value = 1
                    }
                }
                Status.ERROR -> {
                    loadingViewActive.value = false
                    topTextState.value = 2
                }
                else -> {}
            }
        }
    }
}

@Composable
fun LoginHeadText(headText: String, hintText: String) {
    Text(
        text = headText,
        textAlign = TextAlign.Start,
        fontSize = 30.sp,
        textDecoration = TextDecoration.None,
        letterSpacing = 0.sp,
        lineHeight = 39.sp,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier

            .fillMaxWidth()
            .alpha(1f),
        color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
        style = MaterialTheme.typography.h1
    )

    Text(
        text = hintText,
        textAlign = TextAlign.Start,
        fontSize = 15.sp,
        textDecoration = TextDecoration.None,
        letterSpacing = 0.sp,
        lineHeight = 19.sp,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .padding(top = 15.dp)
            .alpha(1f),
        color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
        style = MaterialTheme.typography.subtitle2
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PhoneNumberNew_Input(phoneNumber : MutableState<String>) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusBorderColor = remember {mutableStateOf(InActiveWhite)}

    TextField(
        value = phoneNumber.value,
        onValueChange = { changedValue ->
            val filteredValue = changedValue.filter { char -> char.isDigit() }
            phoneNumber.value = if (filteredValue.length < 10) filteredValue else filteredValue.substring(0..9)
        },
        placeholder = { Text("Введите номер телефона", color = SemiWhite, style = MaterialTheme.typography.body1) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide();
                focusManager.clearFocus()
            }),
        visualTransformation = {mobileFilter(it)},
        singleLine = true,
        textStyle = MaterialTheme.typography.body1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(63.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .border(
                1.dp,
                focusBorderColor.value,
                RoundedCornerShape(15.dp)
            )
            .onFocusEvent { focusState ->
                if (focusState.isFocused) {
                    if (phoneNumber.value.length < 10)
                        focusBorderColor.value = FocusBlue
                    else
                        focusBorderColor.value = FocusGreen
                } else {
                    if (phoneNumber.value.length < 10)
                        focusBorderColor.value = InActiveWhite
                    else
                        focusBorderColor.value = FocusGreen
                }
            }


    )
}

fun mobileFilter(text: AnnotatedString) : TransformedText {
    val phoneNumberMask = "+7              "
//                         012345678901234567
    val trimmed : String = when (text.text.length) {
        in(0..9) -> text.text
        else -> text.text.substring(0..9)
    }

    val annotatedString = AnnotatedString.Builder().run {
        var tr_i = 0
        for (i in phoneNumberMask.indices) {
            when (i) {
                0  -> append('+')
                1  -> append('7')
                2  -> append(' ')
                6  -> append(' ')
                10 -> append(' ')
                13 -> append(' ')
                else -> {
                    if (tr_i != trimmed.length) {
                        append(trimmed[tr_i]);
                        tr_i++;
                    } else break
                }
            }
        }
        pushStyle(SpanStyle(color = Color(0xffD3D3D3)))
        append(phoneNumberMask.takeLast(phoneNumberMask.length - length))
        toAnnotatedString()
    }

    val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3)  return offset  + 3
            if (offset <= 6)  return offset  + 4
            if (offset <= 8)  return offset  + 5
            if (offset <= 10) return offset  + 6
            return phoneNumberMask.length
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 3)   return 0             // =7  3
            if (offset <= 7)   return offset - 3    // 939 4
            if (offset <= 11)  return offset - 4    // 395 4
            if (offset <= 14)  return offset - 5    // 50  3
            if (offset <= 16)  return offset - 6    // 62  2
            return 16
        }
    }

    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}

@Composable
fun ChoseClubNew_Input(currentClub: MutableState<ClubModel?>, onClick: () -> Unit) {
    val clubText = remember { mutableStateOf("Выберите свой клуб")}
    val colorText = remember { mutableStateOf (Color(0x80FFFFFF))}
    val borderColor = remember { mutableStateOf(Color(0xFFD3D3D3))}
    if (currentClub.value != null) {
        clubText.value = currentClub.value!!.text_name
        borderColor.value = FocusGreen
        colorText.value = Color.White
    }
    else {
        clubText.value = "Выберите свой клуб"
        borderColor.value = InActiveWhite
        colorText.value = Color(0x80FFFFFF)
    }
    Row(
        modifier = Modifier

            .fillMaxWidth()
            .height(63.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .border(
                1.dp,
                borderColor.value,
                RoundedCornerShape(15.dp)
            )
            .padding(start = 20.dp, top = 21.dp, end = 0.dp, bottom = 21.dp)

    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(21.dp)
                .background(Color.Transparent)
                .clickable {
                    onClick()
                }
        ) {

            Text(
                text = clubText.value,
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                textDecoration = TextDecoration.None,
                letterSpacing = 0.sp,
                lineHeight = 21.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .alpha(1f),
                color = colorText.value,
                style = MaterialTheme.typography.body1
            )

            Spacer(Modifier.weight(1f))
            IconButton(onClick = { onClick() }) {
                Icon(
                    painterResource(R.drawable.ic_baseline_expand_more_24),
                    contentDescription = "Список клубов",
                    tint = Color(red = 1f, green = 1f, blue = 1f, alpha = 0.5f))
            }

        }
    }
}

@Composable
fun WeDontUseYourData() {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        modifier = Modifier

            .width(299.09539794921875.dp)
            .height(32.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .background(Color.Transparent)

            .padding(start = 0.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)

            .alpha(1f)


    ) {

        Canvas(
            modifier = Modifier
                .width(16.dp)
                .height(17.dp)
            //.fillMaxWidth()
            //.aspectRatio(1f)

        ) {
            val fillPath = PathParser.createPathFromPathData("M 15.323075294494629 0 L 1.0215383768081665 0 C 0.45735836029052734 0 0 0.44771480560302734 0 0.9999995231628418 L 0 7.164854049682617 C 0 14.546374320983887 6.409289121627808 17.3515682220459 7.875865459442139 17.896833419799805 C 8.070185780525208 17.969079971313477 8.274428248405457 17.969079971313477 8.468749046325684 17.896833419799805 C 9.935325384140015 17.3515682220459 16.344614028930664 14.546374320983887 16.344614028930664 7.164854049682617 L 16.344614028930664 0.9999999403953552 C 16.344614028930664 0.44771522283554077 15.887255787849426 0 15.323075294494629 0 Z M 11.236907958984375 6 L 7.150753974914551 10 L 5.107676982879639 8 ")
            //fillPath.fillType = Path.FillType.EVEN_ODD
            val rectF = RectF()
            fillPath.computeBounds(rectF, true)
            val matrix = Matrix()
            val scale = minOf( size.width / rectF.width(), size.height / rectF.height() )
            matrix.setScale(scale, scale)
            fillPath.transform(matrix)
            val composePathFill = fillPath.asComposePath()

            drawPath(path = composePathFill, color = Color.Transparent, style = Fill)
            drawPath(path = composePathFill, color = Color(red = 0.3326374292373657f, green = 0.6499999761581421f, blue = 0.1381250023841858f, alpha = 1f), style = Stroke(width = 4f, miter = 4f, join = StrokeJoin.Round))
        }

        Text(
            text = "мы не передаем и не используем ваши данные для передачи 3м лицам",
            textAlign = TextAlign.Start,
            fontSize = 12.sp,
            textDecoration = TextDecoration.None,
            letterSpacing = 0.sp,
            lineHeight = 16.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(1f),
            color = Color(red = 0.3326374292373657f, green = 0.6499999761581421f, blue = 0.1381250023841858f, alpha = 1f),
            style = MaterialTheme.typography.subtitle1
        )
    }

}

@Composable
fun IDontHaveAccount(onClick: () -> Unit) {
    Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.clickable { onClick() }) {
            Text(
                text = "у меня еще нет аккаунта",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                textDecoration = TextDecoration.None,
                letterSpacing = 0.sp,
                lineHeight = 19.sp,
                overflow = TextOverflow.Ellipsis,
                color = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f),
                style = MaterialTheme.typography.button
            )
        }
    }
}



//@Preview
//@Composable
//fun NewLoginScreenPreview() {
//    val phoneNumber = remember { mutableStateOf("") }
//    val currentClub  = remember { MutableLiveData<ClubModel>(null)}
//
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .background(Color(0xff00131d))
//    ) {
//        TopImage {
//            Image(painterResource(R.drawable.white_logo),
//                contentDescription = "Top logo")
//        }
//        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
//            Spacer(modifier = Modifier.padding(15.dp))
//            LoginHeadText()
//            Spacer(modifier = Modifier.padding(20.dp))
//            PhoneNumberNew_Input(phoneNumber)
//            Spacer(modifier = Modifier.padding(5.dp))
//            ChoseClubNew_Input(currentClub) {
//
//            }
//        }
//
//    }
//    Box(modifier = Modifier
//        .fillMaxSize(),
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        Column() {
//            Column(modifier = Modifier.padding(horizontal = 14.dp)) {
//                EnterInAccountButton() {}
//                Spacer(modifier = Modifier.padding(5.dp))
//                WeDontUseYourData()
//                Spacer(modifier = Modifier.padding(15.dp))
//            }
//            BottomImage {
//                IDontHaveAccount()
//            }
//        }
//    }
//}