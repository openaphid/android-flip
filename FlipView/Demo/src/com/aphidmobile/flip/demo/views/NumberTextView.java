package com.aphidmobile.flip.demo.views;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

/**
 * @author Bo Hu
 *         created at 8/21/12, 4:42 PM
 */
public class NumberTextView extends TextView{
	private int number;

	public NumberTextView(Context context, int number) {
		super(context);
		setNumber(number);
		setTextSize(480);
		setTextColor(Color.BLACK);
		setBackgroundColor(Color.WHITE);
		setGravity(Gravity.CENTER);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
		setText(String.valueOf(number));
	}

	@Override
	public String toString() {
		return "NumberTextView: " + number;
	}
}
