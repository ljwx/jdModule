package com.ljwx.basefragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ljwx.baseapp.page.IPageBroadcast

open class BaseFragment(@LayoutRes private val layoutResID: Int) : Fragment(), IPageBroadcast {

    open val TAG = this.javaClass.simpleName

    /**
     * 结束当前页的广播
     */
    private var mFinishReceiver: BroadcastReceiver? = null

    /**
     * 刷新广播
     */
    private var mRefreshReceiver: BroadcastReceiver? = null

    /**
     * 注册广播的Intent
     */
    private var mBroadcastIntentFilter: IntentFilter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(layoutResID, container, false)
    }

    override fun registerFinishBroadcast(vararg actions: String?) {
        mBroadcastIntentFilter = mBroadcastIntentFilter ?: IntentFilter()
        actions.forEach {
            mBroadcastIntentFilter?.addAction(it)
        }
        mFinishReceiver = mFinishReceiver ?: (object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (mBroadcastIntentFilter?.matchAction(intent.action) == true) {
                    onPageFinish()
                }
            }
        })
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(mFinishReceiver!!, mBroadcastIntentFilter!!)
    }

    override fun registerRefreshBroadcast(vararg actions: String?) {
        mBroadcastIntentFilter = mBroadcastIntentFilter ?: IntentFilter()
        actions.forEach {
            mBroadcastIntentFilter?.addAction(it)
        }
        mRefreshReceiver = mRefreshReceiver ?: (object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (mBroadcastIntentFilter?.matchAction(intent.action) == true) {
                    onPageRefresh(intent.type)
                }
            }
        })
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(mRefreshReceiver!!, mBroadcastIntentFilter!!)
    }


    override fun unregisterBroadcast(action: String?) {
        if (action != null) {
            val iterator = mBroadcastIntentFilter?.actionsIterator()
            while (iterator?.hasNext() == true) {
                if (iterator.next() == action) {
                    iterator.remove()
                }
            }
        } else {
            mFinishReceiver?.let {
                LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
            }
            mFinishReceiver = null
        }
    }

    override fun sendFinishBroadcast(action: String?) {
        if (action.isNullOrBlank()) {
            return
        }
        val intent = Intent(action)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    override fun sendRefreshBroadcast(action: String?, type: String?) {
        if (action.isNullOrBlank()) {
            return
        }
        val intent = Intent(action)
        type?.let {
            intent.type = type
        }
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    override fun onPageFinish() {
        activity?.finish()
    }

    override fun onPageRefresh(type: String?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mFinishReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }
        mFinishReceiver = null
        mRefreshReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }
        mRefreshReceiver = null
        mBroadcastIntentFilter = null
    }

}