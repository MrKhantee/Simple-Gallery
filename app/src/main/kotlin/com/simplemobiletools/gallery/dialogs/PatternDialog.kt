package com.simplemobiletools.gallery.dialogs

import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.gallery.R
import com.simplemobiletools.gallery.activities.SimpleActivity
import com.simplemobiletools.gallery.extensions.config
import kotlinx.android.synthetic.main.dialog_pattern.view.*

class PatternDialog(val activity: SimpleActivity, val requiredHash: String, val callback: (hash: String) -> Unit) {
    var dialog: AlertDialog? = null
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_pattern, null)

    var hash = requiredHash

    init {
        view.apply {
            pattern_lock_view.correctStateColor = activity.config.primaryColor
            pattern_lock_view.normalStateColor = activity.config.textColor
            pattern_lock_view.addPatternLockListener(object : PatternLockViewListener {
                override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                    receivedHash(PatternLockUtils.patternToSha1(pattern_lock_view, pattern))
                }

                override fun onCleared() {
                }

                override fun onStarted() {
                }

                override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
                }
            })
        }

        dialog = AlertDialog.Builder(activity)
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
            activity.setupDialogStuff(view, this)
        }
    }

    private fun receivedHash(newHash: String) {
        view.apply {
            if (hash.isEmpty()) {
                hash = newHash
                pattern_lock_view.clearPattern()
                pattern_dialog_title.setText(R.string.repeat_pattern)
            } else {
                if (hash == newHash) {
                    pattern_lock_view.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                    Handler().postDelayed({
                        callback(hash)
                        dialog!!.dismiss()
                    }, 300)
                } else {
                    pattern_lock_view.setViewMode(PatternLockView.PatternViewMode.WRONG)
                    activity.toast(R.string.wrong_pattern)
                    Handler().postDelayed({
                        pattern_lock_view.clearPattern()
                        if (requiredHash.isEmpty()) {
                            hash = ""
                            pattern_dialog_title.setText(R.string.insert_pattern)
                        }
                    }, 1000)
                }
            }
        }
    }
}
