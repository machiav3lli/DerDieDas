package com.machiav3lli.derdiedas.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.material.button.MaterialButton
import com.machiav3lli.derdiedas.R
import com.machiav3lli.derdiedas.ui.WordActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AnimationUtil {
    fun animateButtonDrawable(button: MaterialButton) {
        val context = button.context
        CoroutineScope(Dispatchers.IO).launch {
            repeat(4) {
                button.backgroundTintList = context.getColorStateList(R.color.md_theme_primary)
                button.setTextColor(context.getColorStateList(R.color.md_theme_onPrimary))
                delay(300)
                button.backgroundTintList =
                    context.getColorStateList(R.color.md_theme_secondaryContainer)
                button.setTextColor(context.getColorStateList(R.color.md_theme_onSecondaryContainer))
                delay(300)
            }
        }
    }

    fun View.animateJumpAndSlide(context: Context, isCorrectAnswer: Boolean) {
        val jumpAnim = AnimationUtils.loadAnimation(context, R.anim.jump_animation)
        val waitAnimation = AnimationUtils.loadAnimation(context, R.anim.wait_animation)
        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                (context as WordActivity).replaceFragment()
            }
        }
        waitAnimation.setAnimationListener(animationListener)
        jumpAnim.setAnimationListener(animationListener)

        // if correct answer, we do jump and after that slide the new fragment,
        // if wrong, we do flash and after that slide the fragment
        if (isCorrectAnswer) {
            startAnimation(jumpAnim)
        } else {
            // we are using this dummy animation which does nothing, to wait for the flashing to end
            // and then we replace the fragment (because AnimationDrawable doesn't have a simple
            // onFinished listener) - this means the duration in this animation has to be the sum of
            // durations in button_animation_correct
            startAnimation(waitAnimation)
        }
    }
}