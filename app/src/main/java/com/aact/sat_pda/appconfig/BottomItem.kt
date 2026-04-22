package com.aact.sat_pda.appconfig

import com.aact.sat_pda.R

sealed class BottomItem(
    var name: String,
    val icon: Int,
    var onClick: () -> Unit = {},
    val defaultName: String = name
) {

    object Login : BottomItem("로그인", R.drawable.login_24)
    object Info : BottomItem("사용자정보", R.drawable.info)
    object Exit : BottomItem("종료", R.drawable.exit)
    object KeyBoard : BottomItem("키보드", R.drawable.keyboard)
    object Save : BottomItem("저장", R.drawable.save)
    object Open : BottomItem("열기", R.drawable.open_in_browser)
    object Search : BottomItem("조회", R.drawable.search)
    object Add : BottomItem("추가", R.drawable.add)
    object Print : BottomItem("인쇄", R.drawable.print)
    object Camera : BottomItem("카메라", R.drawable.camera)
    object Scale : BottomItem("계량", R.drawable.scale)
    object Location : BottomItem("이동", R.drawable.location)
    object List : BottomItem("목록", R.drawable.menu)
    object Delete : BottomItem("삭제", R.drawable.delete)
    object Complete: BottomItem("완료",R.drawable.check_circle)
    object Export: BottomItem("출고",R.drawable.outbound)
    object Offload: BottomItem("오프로드",R.drawable.swap_vert)
    object Cancel: BottomItem("취소",R.drawable.close)
    object Tmp01: BottomItem("",0)
    object Tmp02: BottomItem("",0)

    companion object {
        fun setDefault() {
            Login.name = Login.defaultName
            Info.name = Info.defaultName
            Exit.name = Exit.defaultName
            KeyBoard.name = KeyBoard.defaultName
            Save.name = Save.defaultName
            Open.name = Open.defaultName
            Search.name = Search.defaultName
            Add.name = Add.defaultName
            Print.name = Print.defaultName
            Camera.name = Camera.defaultName
            Scale.name = Scale.defaultName
            Location.name = Location.defaultName
            List.name = List.defaultName
            Delete.name = Delete.defaultName
            Complete.name = Complete.defaultName
            Cancel.name = Cancel.defaultName
            Tmp01.name = Tmp01.defaultName
            Tmp02.name = Tmp02.defaultName
        }

    }
}