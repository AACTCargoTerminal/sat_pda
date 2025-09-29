package com.aact.sat_pda.appconfig

import androidx.fragment.app.Fragment
import com.aact.sat_pda.appconfig.BottomItem.Add
import com.aact.sat_pda.appconfig.BottomItem.Complete
import com.aact.sat_pda.appconfig.BottomItem.Delete
import com.aact.sat_pda.appconfig.BottomItem.Exit
import com.aact.sat_pda.appconfig.BottomItem.KeyBoard
import com.aact.sat_pda.appconfig.BottomItem.Open
import com.aact.sat_pda.appconfig.BottomItem.Print
import com.aact.sat_pda.appconfig.BottomItem.Save
import com.aact.sat_pda.appconfig.BottomItem.Search
import com.aact.sat_pda.screen.accept.CARGO_ACCEPT_Fragment
import com.aact.sat_pda.screen.camera.CAMERA_Fragment
import com.aact.sat_pda.screen.cargo_stock_list_date.CARGO_STOCK_LIST_DATE_Fragment
import com.aact.sat_pda.screen.control.CARGO_CONTROL_Fragment
import com.aact.sat_pda.screen.control_list.CARGO_CONTROL_LIST_Fragment
import com.aact.sat_pda.screen.damage_export.CARGO_DAMAGE_EXPORT_Fragment
import com.aact.sat_pda.screen.doubt_list.CARGO_DOUBT_LIST_Fragment
import com.aact.sat_pda.screen.import_bd_with_no.IMPORT_BD_WITH_NO_Fragment
import com.aact.sat_pda.screen.import_info.IMPORT_INFO_Fragment
import com.aact.sat_pda.screen.import_info_list.IMPORT_INFO_LIST_Fragment
import com.aact.sat_pda.screen.import_irr.IMPORT_IRR_Fragment
import com.aact.sat_pda.screen.import_irr_list.IMPORT_IRR_LIST_Fragment
import com.aact.sat_pda.screen.info.InfoFragment
import com.aact.sat_pda.screen.location.CARGO_LOCATION_Fragment
import com.aact.sat_pda.screen.login.LoginFragment
import com.aact.sat_pda.screen.menu.MenuFragment
import com.aact.sat_pda.screen.scale.CARGO_SCALE_Fragment
import com.aact.sat_pda.screen.scale_list.CARGO_SCALE_LIST_Fragment
import com.aact.sat_pda.screen.stock.CARGO_STOCK_Fragment
import com.aact.sat_pda.screen.stock_list_all.STOCK_LIST_ALL_Fragment
import com.aact.sat_pda.screen.uld.OPERATION_ULD_Fragment
import com.aact.sat_pda.screen.uld_buildup.OPERATION_ULD_BUILDUP_Fragment
import com.aact.sat_pda.screen.uld_material.OPERATION_ULD_MATERIAL_Fragment
import com.aact.sat_pda.screen.uld_mawb.OPERATION_ULD_MAWBLIST_Fragment
import com.aact.sat_pda.screen.uld_scale.OPERATION_ULD_SCALE_Fragment
import com.aact.sat_pda.screen.uldlist.OPERATION_ULD_LIST_Fragment
import com.aact.sat_pda.screen.volume.CARGO_VOLUME_Fragment

sealed class Route(
    val route: String,
    var name: String,
    var params: List<Pair<String, String>> = emptyList(),
    var group: String = "",
    var execute: String = "N",
    var create: String = "N",
    var update: String = "N",
    var delete: String = "N",
    var read: String = "N",
    var print: String = "N",
    var excel: String = "N"
) {
    abstract fun fragment(): Fragment
    open fun bottomItems(): List<BottomItem> = emptyList()

    private fun resetPermissions() {
        execute = "N"
        create = "N"
        update = "N"
        delete = "N"
        read = "N"
        print = "N"
        excel = "N"
    }

    companion object {
        fun setDefault() {
            Common.menuList = listOf<Route>(
                Accept, ControlList, Scale, Volume, DamageExport,
                OperationUldList,StockListDt, DoubtList,StockListAl,ImportInfo,ImportIrrList,ImportInfoList,ImportBDWithNo
            )
            Common.menuList.forEach { it.resetPermissions() }
        }
    }

    object Login : Route("login", "로그인") {
        override fun fragment(): Fragment = LoginFragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val list = listOf(
                BottomItem.Login,
                BottomItem.Info,
                BottomItem.Exit
            )
            return list
        }

    }

    object Info : Route("info", "사용자정보") {
        override fun fragment(): Fragment = InfoFragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val list = listOf(
                BottomItem.Login,
                BottomItem.Info,
                BottomItem.Exit
            )
            return list
        }
    }

    object Menu : Route("menu", "메뉴") {
        override fun fragment(): Fragment = MenuFragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val list = listOf(
                BottomItem.Login,
                BottomItem.Info,
                BottomItem.Exit
            )
            return list
        }
    }

    object Accept : Route("accept", "화물접수") {
        override fun fragment(): Fragment = CARGO_ACCEPT_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val list = listOf(
                BottomItem.Save to update,
                BottomItem.KeyBoard to execute
            )
            return list.filter { (_, permission) -> permission == "Y" }
                .map { (item, _) -> item }
        }
    }

    object ControlList : Route("eList", "수출화물목록") {
        override fun fragment(): Fragment = CARGO_CONTROL_LIST_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()
            val accept = BottomItem.Add
            accept.name = "화물접수"
            val list = listOf(
                accept to create,
                BottomItem.Open to read,
                BottomItem.Search to read,
                BottomItem.KeyBoard to execute
            )
            return list.filter { (_, permission) -> permission == "Y" }
                .map { (item, _) -> item }
        }
    }

    object Control : Route("control", "수출화물") {
        override fun fragment(): Fragment = CARGO_CONTROL_Fragment()
        override fun bottomItems(): List<BottomItem> {
            BottomItem.setDefault()
            val update = BottomItem.Save
            update.name = "수정"
            val cargoIn = BottomItem.Add
            cargoIn.name = "입고"
            val cargoCancel = BottomItem.Exit
            cargoCancel.name = "입고취소"
            val list = listOf(
                BottomItem.Scale,
                update,
                BottomItem.Location,
                cargoIn,
                cargoCancel,
                BottomItem.Print,
                BottomItem.Camera,
                BottomItem.KeyBoard
            )
            return list
        }
    }

    object Camera : Route("camera", "사진등록") {
        override fun fragment(): Fragment = CAMERA_Fragment()
        override fun bottomItems(): List<BottomItem> {
            BottomItem.setDefault()
            val list = listOf(
                BottomItem.Save,
            )
            return list
        }
    }

    object Location : Route("location", "수출화물이동") {
        override fun fragment(): Fragment = CARGO_LOCATION_Fragment()
        override fun bottomItems(): List<BottomItem> {
            BottomItem.setDefault()
            val list = listOf(
                BottomItem.Save,
                BottomItem.KeyBoard
            )
            return list
        }
    }

    object Scale : Route("scale", "화물계량") {
        override fun fragment(): Fragment = CARGO_SCALE_Fragment()
        override fun bottomItems(): List<BottomItem> {
            BottomItem.setDefault()
            val volume = BottomItem.Location
            volume.name = "부피"
            val cargoIn = BottomItem.Add
            cargoIn.name = "입고"

            val list = listOf(
                BottomItem.KeyBoard to execute,
                BottomItem.Save to update,
                BottomItem.Scale to read,
                volume to create,
                cargoIn to create,
                BottomItem.List to create,
                BottomItem.Print to print,
            )
            return list.filter { (_, permission) -> permission == "Y" }
                .map { (item, _) -> item }
        }
    }

    object ScaleList : Route("scaleList", "화물계량목록") {
        override fun fragment(): Fragment = CARGO_SCALE_LIST_Fragment()
        override fun bottomItems(): List<BottomItem> {
            BottomItem.setDefault()
            val update = BottomItem.Save
            update.name = "수정"

            val list = listOf(
                update,
                BottomItem.Delete,
                BottomItem.Print,
                BottomItem.KeyBoard
            )
            return list
        }
    }

    object Volume : Route("volume", "화물부피") {
        override fun fragment(): Fragment = CARGO_VOLUME_Fragment()
        override fun bottomItems(): List<BottomItem> {
            BottomItem.setDefault()
            val list = listOf(
                BottomItem.Save to update,
                BottomItem.Delete to delete,
                BottomItem.Add to create,
                BottomItem.Print to print,
                BottomItem.KeyBoard to execute
            )
            return list.filter { (_, permission) -> permission == "Y" }
                .map { (item, _) -> item }
        }
    }

    object Stock : Route("stock", "수출화물입고") {
        override fun fragment(): Fragment = CARGO_STOCK_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val cargoIn = BottomItem.Add
            cargoIn.name = "입고"

            val list = listOf(
                cargoIn,
                BottomItem.KeyBoard
            )
            return list
        }
    }

    object DamageExport : Route("outdamage", "반입전 파손화물", listOf(Pair("ACCEPT_TYPE", "N"))) {
        override fun fragment(): Fragment = CARGO_DAMAGE_EXPORT_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val list = listOf(
                BottomItem.Save to update,
                BottomItem.Camera to update,
                BottomItem.Print to print,
                BottomItem.KeyBoard to execute
            )
            return list.filter { (_, permission) -> permission == "Y" }
                .map { (item, _) -> item }
        }
    }

    object OperationUldList : Route("uldList", "ULD 작업 목록") {
        override fun fragment(): Fragment = OPERATION_ULD_LIST_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val add = BottomItem.Add
            add.name = "신규"

            val list = listOf(
                BottomItem.Search,
                add,
                BottomItem.Open,
                BottomItem.KeyBoard
            )
            return list
        }
    }

    object OperationUld : Route("uld", "ULD 작업 관리") {
        override fun fragment(): Fragment = OPERATION_ULD_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val add = BottomItem.Add
            add.name = "추가"
            val cancel = BottomItem.Exit
            cancel.name = "취소"

            val buildUp = BottomItem.Tmp01
            buildUp.name = "BuildUp"

            val mawbList = BottomItem.Tmp02
            mawbList.name = "MAWB목록"


            val list = listOf(
                BottomItem.KeyBoard,
                add,
                BottomItem.Save,
                buildUp,
                BottomItem.Complete,
                BottomItem.Export,
                cancel,
                BottomItem.Offload,
                mawbList,
                BottomItem.Delete,
                BottomItem.Print,


                )
            return list
        }
    }

    object UldMaterial : Route("uldMaterial", "ULD 소모품 관리") {
        override fun fragment(): Fragment = OPERATION_ULD_MATERIAL_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val list = listOf(
                BottomItem.KeyBoard,
                BottomItem.Save,
            )
            return list
        }
    }

    object UldScale : Route("uldScale", "ULD 계량") {
        override fun fragment(): Fragment = OPERATION_ULD_SCALE_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val saveAndExport = BottomItem.Tmp01
            saveAndExport.name = "저장/출고"

            val printAndExport = BottomItem.Tmp02
            printAndExport.name = "인쇄/출고"

            val list = listOf(
                BottomItem.KeyBoard,
                BottomItem.Save,
                saveAndExport,
                printAndExport
            )
            return list
        }
    }

    object UldBuildUp : Route("buildUp", "ULD 화물포장") {
        override fun fragment(): Fragment = OPERATION_ULD_BUILDUP_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val tmp1 = BottomItem.Tmp01
            tmp1.name = "선택저장"

            val list = listOf(
                BottomItem.KeyBoard,
                BottomItem.Search,
                BottomItem.Save,
                tmp1
            )
            return list
        }
    }

    object UldMawbList : Route("mawbList", "수출화물포장 목록") {
        override fun fragment(): Fragment = OPERATION_ULD_MAWBLIST_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val list = listOf(
                BottomItem.KeyBoard,
                BottomItem.Search,
                BottomItem.Delete,
            )
            return list
        }
    }

    object StockListDt : Route("stockListDt", "일일입고현황") {
        override fun fragment(): Fragment = CARGO_STOCK_LIST_DATE_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val list = listOf(
                BottomItem.KeyBoard,
                BottomItem.Search,
            )
            return list
        }
    }

    object DoubtList : Route("doubtList", "의심화물리스트") {
        override fun fragment(): Fragment = CARGO_DOUBT_LIST_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val tmp1 = BottomItem.Tmp01
            tmp1.name = "통보"

            val tmp2 = BottomItem.Tmp02
            tmp2.name = "해제"

            val list = listOf(
                BottomItem.KeyBoard,
                BottomItem.Search,
                tmp1,
                tmp2
            )
            return list
        }
    }

    object StockListAl : Route("stockListAl", "수출화물재고현황") {
        override fun fragment(): Fragment = STOCK_LIST_ALL_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()
            val list = listOf(
                BottomItem.Search,
            )
            return list
        }
    }

    object ImportInfo : Route("importInfo", "수입화물조회") {
        override fun fragment(): Fragment = IMPORT_INFO_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val irr = BottomItem.Tmp01
            irr.name = "이례등록"
            val dmg = BottomItem.Tmp02
            dmg.name = "파손등록"
            val bd = BottomItem.Save
            bd.name = "B/D작업"
            val cargoLoc = BottomItem.Location
            cargoLoc.name = "화물이동"
            val inCargo = BottomItem.Add
            inCargo.name = "입고"
            val list = listOf(
                BottomItem.KeyBoard,
                BottomItem.Camera,
                irr,
                bd,
                cargoLoc,
                dmg,
                inCargo
            )
            return list
        }
    }

    object ImportIrrList : Route("irrList", "수입화물보류목록") {
        override fun fragment(): Fragment = IMPORT_IRR_LIST_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val irrRelease = BottomItem.Tmp01
            irrRelease.name = "보류해제"

            val list = listOf(
                BottomItem.KeyBoard,
                BottomItem.Add,
                BottomItem.Save,
                irrRelease,
                BottomItem.Camera

            )
            return list
        }
    }

    object ImportIrr : Route("importIrr", "수입화물이례등록") {
        override fun fragment(): Fragment = IMPORT_IRR_Fragment()
        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val save = BottomItem.Save
            save.name = "등록"

            val list = listOf(
                BottomItem.KeyBoard,
                save,
            )
            return list
        }
    }

    object ImportInfoList : Route("infolist","수입화물목록") {
        override fun fragment(): Fragment = IMPORT_INFO_LIST_Fragment()

        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val bd = BottomItem.Save
            bd.name = "B/D작업"
            val inCargo = BottomItem.Add
            inCargo.name = "입고"

            val list = listOf(
                BottomItem.KeyBoard,
                bd,
                inCargo
            )

            return list
        }
    }


    object ImportBDWithNo : Route("importBDWithNo","수입화물B/D") {
        override fun fragment(): Fragment = IMPORT_BD_WITH_NO_Fragment()

        override fun bottomItems(): List<BottomItem> {

            BottomItem.setDefault()

            val dmg = BottomItem.Tmp01
            dmg.name = "파손"
            val irr = BottomItem.Tmp02
            irr.name = "이례"
            val inCargo = BottomItem.Add
            inCargo.name = "입고"
            val save = BottomItem.Save
            save.name = "저장"
            val uldComplete = BottomItem.Complete
            uldComplete.name = "ULD완료"
            val camera = BottomItem.Camera
            camera.name = "사진"


            val list = listOf(
                BottomItem.KeyBoard,
                dmg,
                irr,
                inCargo,
                save,
                uldComplete,
                camera
            )

            return list
        }
    }


}