package com.aact.sat_pda.screen.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.MainActivity
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentLoginBinding
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val loginModel: LoginModel by lazy { LoginModel() }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? MainActivity)?.startProc()

        val pref = getPref("userId")
        if(pref==null||pref == ""){
            binding.switchSaveId.isChecked = false
            binding.idtxt.editText.setText("")
            Common.userId.value = ""
        }else{
            binding.switchSaveId.isChecked = true
            binding.idtxt.editText.setText(pref)
            Common.userId.value = pref
        }

        Common.terminalName.observe(viewLifecycleOwner) { value ->
            val terminal = binding.terminal
            terminal.text = "CARGO " + value
        }

        binding.idtxt.editText.setHint("아이디")
        item.strEditText(binding.idtxt.editText)

        binding.switchSaveId.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                savePref("userId", Common.userId.value)
            }else{
                savePref("userId", "")
            }
        }
        action.doneAction(binding.idtxt.editText) {
            loginClick()
        }

        binding.idtxt.editText.doAfterTextChanged {
            val text = it.toString()
            Common.userId.value = text
        }

        binding.loginBtn.setOnClickListener {
            Common.sound.value = "CLICK"
            loginClick()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun loginClick() {
        val permission = getPermission()
        Common.permissionFlag.value = permission
        if (!permission) {
            Common.sendError("권한 에러")
            return
        }
        if (Common.terminalCode.value == "") {
            Common.sendError("터미널 코드 에러")
            return
        }
        if (Common.userId.value == "") {
            Common.sendError("아이디를 확인해주세요")
            return
        }
        if (Common.userId.value.all { it.isDigit() }) {
            Common.userId.value = "AT" + Common.userId.value
        }

        if(binding.switchSaveId.isChecked){
            savePref("userId", Common.userId.value)
        }else{
            savePref("userId", "")
        }
        loginModel.loginClick(debugControl())
    }

}