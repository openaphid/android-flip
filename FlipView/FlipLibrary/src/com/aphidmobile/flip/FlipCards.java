/*
Copyright 2012 Aphid Mobile

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
	 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */

package com.aphidmobile.flip;

import android.view.*;
import com.aphidmobile.utils.AphidLog;
import com.aphidmobile.utils.TextureUtils;

import javax.microedition.khronos.opengles.GL10;

public class FlipCards {
	private static final float ACCELERATION = 1f;	
	private static final float MOVEMENT_RATE = 1.5f;
	private static final int MAX_TIP_ANGLE = 60;
	private static final int MAX_TOUCH_MOVE_ANGLE = 15;
	private static final float MIN_MOVEMENT = 4f;

	private static final int STATE_INIT = 0;
	private static final int STATE_TOUCH = 1;
	private static final int STATE_AUTO_ROTATE = 2;

	private ViewDualCards frontCards;
	private ViewDualCards backCards;

	private float angle = 0f;
	private boolean forward = true;
	private int animatedFrame = 0;
	private int state = STATE_INIT;

	private boolean orientationVertical = true;
	private float lastPosition = -1;

	private FlipViewController controller;

	private int activeIndex = -1;
	private boolean waitForTexture = false;
	
	private boolean visible = false;

	public FlipCards(FlipViewController controller, boolean orientationVertical) {
		this.controller = controller;

		frontCards = new ViewDualCards(orientationVertical);
		backCards = new ViewDualCards(orientationVertical);
		this.orientationVertical = orientationVertical;
		resetAxises();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (!visible)
			waitForTexture = false;
	}
	
	void refreshPageView(View view) {
		if (frontCards.getView() == view)
			frontCards.markForceReload();
		if (backCards.getView() == view)
			backCards.markForceReload();
	}
	
	void refreshPage(int pageIndex) {
		if (frontCards.getIndex() == pageIndex)
			frontCards.markForceReload();
		if (backCards.getIndex() == pageIndex)
			backCards.markForceReload();
	}

	public void reloadTexture(int frontIndex, View frontView, int backIndex, View backView) {
		synchronized (this) {
			if (frontView != null) {
				if (backCards.getView() == frontView) {
					frontCards.setView(-1, null);
					swapCards();
				}
			}

			if (backView != null) {
				if (frontCards.getView() == backView) {
					backCards.setView(-1, null);
					swapCards();
				}
			}

			boolean frontChanged = frontCards.setView(frontIndex, frontView);
			boolean backChanged = backCards.setView(backIndex, backView);

			if (AphidLog.ENABLE_DEBUG)
				AphidLog.d("reloading texture: %s and %s; old views: %s, %s, front changed %s, back changed %s", frontView, backView, frontCards.getView(), backCards.getView(), frontChanged, backChanged);
			
			if (AphidLog.ENABLE_DEBUG)
				AphidLog.d("reloadTexture: activeIndex %d, front %d, back %d, angle %.1f", activeIndex, frontIndex, backIndex, angle);

			if (waitForTexture) {
				if (frontIndex == activeIndex) {
					if (angle >= 180)
						angle -= 180;
					else if (angle < 0)
						angle += 180;
				} else if (backIndex == activeIndex) {
					if (angle < 0)
						angle += 180;
				}
				waitForTexture = false;
			}

//			AphidLog.i("View changed: front (%d, %s), back (%d, %s), angle %s, activeIndex %d", frontIndex, frontView, backIndex, backView, angle, activeIndex);
		}
	}

	private void setState(int state) {
		if (this.state != state) {
			/*
			if (AphidLog.ENABLE_DEBUG)
				AphidLog.i("setState: from %d, to %d; angle %.1f", this.state, state, angle);
			*/
			this.state = state;
			animatedFrame = 0;
		}
	}

	public synchronized void draw(FlipRenderer renderer, GL10 gl) {		
		applyTexture(renderer, gl);

		if (!TextureUtils.isValidTexture(frontCards.getTexture()) && !TextureUtils.isValidTexture(backCards.getTexture()))
			return;
		
		if (!visible)
			return;

		switch (state) {
		case STATE_INIT:
			break;
		case STATE_TOUCH:
			break;
		case STATE_AUTO_ROTATE: {
			if (waitForTexture)
				controller.getSurfaceView().requestRender();
			else {
				animatedFrame++;
				float delta = (forward ? ACCELERATION : -ACCELERATION) * animatedFrame;

				angle += delta;

				if (backCards.getIndex() == -1) {
					if (angle >= MAX_TIP_ANGLE)
						angle = MAX_TIP_ANGLE;
				}

				if (angle >= 180 || angle <= 0) {
					setState(STATE_INIT);

					if (angle >= 180) { // flip to next page
						if (backCards.getIndex() != -1) {
							activeIndex = backCards.getIndex();
							waitForTexture = true;
							controller.postFlippedToView(activeIndex);
						}

						angle = 180;
					} else
						angle = 0;

					controller.postHideFlipAnimation();
				} else
					controller.getSurfaceView().requestRender();
			}
		}
			break;
		default:
			AphidLog.e("Invalid state: " + state);
			break;
		}

		if (angle < 90) { //render front view over back view
			frontCards.getTopCard().setAngle(0);
			frontCards.getTopCard().draw(gl);

			backCards.getBottomCard().setAngle(0);
			backCards.getBottomCard().draw(gl);

			frontCards.getBottomCard().setAngle(angle);
			frontCards.getBottomCard().draw(gl);
		} else { //render back view first
			frontCards.getTopCard().setAngle(0);
			frontCards.getTopCard().draw(gl);

			backCards.getTopCard().setAngle(180 - angle);
			backCards.getTopCard().draw(gl);

			backCards.getBottomCard().setAngle(0);
			backCards.getBottomCard().draw(gl);
		}
	}

	public void invalidateTexture() {
		frontCards.abandonTexture();
		backCards.abandonTexture();
	}

	public synchronized boolean handleTouchEvent(MotionEvent event, boolean isOnTouchEvent) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastPosition = orientationVertical ? event.getY() : event.getX();
				return isOnTouchEvent;
			case MotionEvent.ACTION_MOVE:
				if (waitForTexture)
					return isOnTouchEvent;
				float delta = orientationVertical ? (lastPosition - event.getY()) : (lastPosition - event.getX());
				
				if (Math.abs(delta) > controller.getTouchSlop()) {
					setState(STATE_TOUCH); //XXX: initialize views?
					forward = delta > 0;
				}
				if (state == STATE_TOUCH) {
					if (Math.abs(delta) > MIN_MOVEMENT)
						forward = delta > 0;					
					
					controller.showFlipAnimation();
					
					float angleDelta ;
					if (orientationVertical)
						angleDelta = 180 * delta / controller.getContentHeight() * MOVEMENT_RATE;
					else
						angleDelta = 180 * delta / controller.getContentWidth() * MOVEMENT_RATE;

					if (Math.abs(angleDelta) > MAX_TOUCH_MOVE_ANGLE) //prevent large delta when moving too fast
						angleDelta = Math.signum(angleDelta) * MAX_TOUCH_MOVE_ANGLE;
					
					angle += angleDelta;
					
					//Bounce the page for the first and the last page
					if (backCards.getIndex() == -1) { //the last page
						if (angle >= MAX_TIP_ANGLE)
							angle = MAX_TIP_ANGLE;
					} else if (backCards.getIndex() == 0) { //the first page
						if (angle <= 180 - MAX_TIP_ANGLE)
							angle = 180 - MAX_TIP_ANGLE;
					}
					
					if (angle < 0) {
						if (frontCards.getIndex() > 0) {
							activeIndex = frontCards.getIndex() - 1; //xxx
							waitForTexture = true;
							controller.flippedToView(activeIndex, false);
						} else {
							swapCards();
							frontCards.setView(-1, null);
							if (-angle >= MAX_TIP_ANGLE)
								angle = -MAX_TIP_ANGLE;
							angle += 180;
						}
					}
					
					lastPosition = orientationVertical ? event.getY() : event.getX();
					
					controller.getSurfaceView().requestRender();
					return true;
				}

				return isOnTouchEvent;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (state == STATE_TOUCH) {
					if (frontCards.getIndex() == -1) // If at the first or last card
						forward = true;
					else if (backCards.getIndex() == -1) 
						forward = false;
					
					setState(STATE_AUTO_ROTATE);
					controller.getSurfaceView().requestRender();
				}				
				return isOnTouchEvent;
		}

		return false;
	}

	private void resetAxises() {
		frontCards.getTopCard().setAxis(Card.AXIS_TOP);
		frontCards.getBottomCard().setAxis(Card.AXIS_TOP);
		backCards.getBottomCard().setAxis(Card.AXIS_TOP);
		backCards.getTopCard().setAxis(Card.AXIS_BOTTOM);
	}
	
	private void swapCards() {
		ViewDualCards tmp = frontCards;
		frontCards = backCards;
		backCards = tmp;
		resetAxises();
	}
	
	private void applyTexture(FlipRenderer renderer, GL10 gl) {
		frontCards.buildTexture(renderer, gl);
		backCards.buildTexture(renderer, gl);
	}
}