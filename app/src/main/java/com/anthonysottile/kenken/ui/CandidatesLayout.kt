package com.anthonysottile.kenken.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

class CandidatesLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val candidateAddedListeners = ArrayList<(Int) -> Unit>()

    private val candidateRemovedListeners = ArrayList<(Int) -> Unit>()

    private var candidates: Array<CustomButton>? = null

    fun addCandidateAddedListener(listener: (Int) -> Unit) {
        this.candidateAddedListeners.add(listener)
    }

    private fun triggerCandidateAdded(candidate: Int) {
        for (listener in this.candidateAddedListeners) {
            listener(candidate)
        }
    }

    fun addCandidateRemovedListener(listener: (Int) -> Unit) {
        this.candidateRemovedListeners.add(listener)
    }

    private fun triggerCandidateRemoved(candidate: Int) {
        for (listener in this.candidateRemovedListeners) {
            listener(candidate)
        }
    }

    private fun checkChangedListener(button: CustomButton) {
        if (button.checked) {
            this@CandidatesLayout.triggerCandidateAdded(button.value)
        } else {
            this@CandidatesLayout.triggerCandidateRemoved(button.value)
        }
    }

    /**
     * Note: this does not trigger any events as this should only be
     * called from ui setting of a square.
     */
    fun setValues(values: Set<Int>) {
        this.candidates!!.forEach { it.setCheckedNoTrigger(false) }

        for (x in values) {
            this.candidates!![x - 1].setCheckedNoTrigger(true)
        }
    }

    fun setDisabled() {
        this.candidates!!.forEach { it.uiEnabled = false }
    }

    fun setDisabled(disabled: Set<Int>) {
        this.candidates!!.forEach { it.uiEnabled = true }

        for (x in disabled) {
            this.candidates!![x - 1].uiEnabled = false
        }
    }

    fun newGame(gameSize: Int) {
        this.clear()

        // Candidates layout... Add the + and - buttons
        val plusButton = CustomButton(
                this.context,
                true,
                true,
                0,
                "+",
                true,
                true,
                false
        )
        plusButton.addClickListener { _ ->
            for (candidate in this.candidates!!) {
                if (candidate.uiEnabled) {
                    candidate.checked = true
                }
            }
        }

        val minusButton = CustomButton(
                this.context,
                true,
                true,
                0,
                "-",
                true,
                true,
                false
        )
        minusButton.addClickListener { _ ->
            for (candidate in this.candidates!!) {
                if (candidate.uiEnabled) {
                    candidate.checked = false
                }
            }
        }

        this.addView(plusButton, CandidatesLayout.allNoneLayoutParams)
        this.addView(TextView(this.context), 5, ViewGroup.LayoutParams.MATCH_PARENT)
        this.addView(minusButton, CandidatesLayout.allNoneLayoutParams)
        this.addView(TextView(this.context), 5, ViewGroup.LayoutParams.MATCH_PARENT)

        this.candidates = Array(gameSize) { i ->
            val button = CustomButton(
                    this.context,
                    true,
                    false,
                    i + 1,
                    Integer.toString(i + 1, 10),
                    i == 0,
                    i == gameSize - 1,
                    true
            )
            button.addCheckChangedListener(this::checkChangedListener)

            this.addView(button, CandidatesLayout.buttonsLayoutParams)
            return@Array button
        }
    }

    fun clear() {
        if (this.candidates != null) {
            this.removeAllViews()
            this.candidates = null
        }
    }

    init {
        this.setPadding(5, 15, 5, 15)
    }

    companion object {
        private val allNoneLayoutParams = LinearLayout.LayoutParams(
                30,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0.3f
        )

        private val buttonsLayoutParams = LinearLayout.LayoutParams(
                30,
                ViewGroup.LayoutParams.MATCH_PARENT,
                0.5f
        )
    }
}
