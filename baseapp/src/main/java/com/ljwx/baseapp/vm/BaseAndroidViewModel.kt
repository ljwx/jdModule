package com.ljwx.baseapp.vm

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ljwx.baseapp.constant.ConstTag
import com.ljwx.baseapp.event.ISendEvent
import com.ljwx.baseapp.response.DataResult
import com.ljwx.baseapp.util.BaseAppUtils
import com.ljwx.baseapp.util.Log2
import com.ljwx.baseapp.vm.model.BaseDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseAndroidViewModel<M : BaseDataRepository<*>>(application: Application) :
    IBaseViewModel<M>, AndroidViewModel(application), DefaultLifecycleObserver, IRxAutoCleared,
    ISendEvent {

    open val TAG = this.javaClass.simpleName + ConstTag.MVVM

    protected var mRepository: M

    private var mCompositeDisposable2: io.reactivex.disposables.CompositeDisposable? = null
    private var mCompositeDisposable3: io.reactivex.rxjava3.disposables.CompositeDisposable? = null

    private val mShowPopLoading = MutableLiveData<Triple<Boolean, Int, String>>()
    private val mDismissPopLoading = MutableLiveData<Triple<Boolean, Int, String>>()
    val popLoadingShow: MutableLiveData<Triple<Boolean, Int, String>>
        get() = mShowPopLoading
    val popLoadingDismiss: MutableLiveData<Triple<Boolean, Int, String>>
        get() = mDismissPopLoading

    init {
        Log2.d(TAG, "创建repository")
        mRepository = createRepository()
    }


    override fun autoClear(disposable: io.reactivex.disposables.Disposable) {
        Log2.d(TAG, "添加Rx自动取消")
        if (mCompositeDisposable2 == null) {
            mCompositeDisposable2 = io.reactivex.disposables.CompositeDisposable()
        }
        mCompositeDisposable2?.add(disposable)
    }

    override fun autoClear(disposable: io.reactivex.rxjava3.disposables.Disposable) {
        Log2.d(TAG, "添加Rx自动取消")
        if (mCompositeDisposable3 == null) {
            mCompositeDisposable3 = io.reactivex.rxjava3.disposables.CompositeDisposable()
        }
        mCompositeDisposable3?.add(disposable)
    }

    override fun onRxCleared() {
        Log2.d(TAG, "执行Rx自动取消")
        mCompositeDisposable2?.clear()
        mCompositeDisposable3?.clear()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log2.d(TAG, "LifecycleOwner的onStop执行")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        Log2.d(TAG, "LifecycleOwner的onDestroy执行")
    }

    override fun onCleared() {
        super.onCleared()
        mRepository.onRxCleared()
        onRxCleared()
        Log2.d(TAG, "ViewModel的onCleared执行")
    }

    open fun showPopLoading(show: Boolean = true, code: Int? = 0, message: String? = "") {
        Log2.d(TAG, "显示Loading弹窗")
        mShowPopLoading.postValue(Triple(show, code ?: 0, message ?: ""))
    }

    open fun dismissPopLoading(dismiss: Boolean = true, code: Int? = 0, message: String? = "") {
        Log2.d(TAG, "取消Loading弹窗")
        mDismissPopLoading.postValue(Triple(dismiss, code ?: 0, message ?: ""))
    }

    override fun commonResponseNotSuccess(result: DataResult<*>) {

    }

    override fun sendLocalEvent(action: String?) {
        if (action == null) {
            return
        }
        Log2.d(TAG, "发送事件广播:$action")
        LocalBroadcastManager.getInstance(BaseAppUtils.getApplication())
            .sendBroadcast(Intent(action))
    }

}