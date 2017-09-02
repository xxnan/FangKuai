/*
b * Copyright 2013 Simon Willeke
 * contact: hamstercount@hotmail.com
 */

/*
    This file is part of Blockinger.

    Blockinger is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Blockinger is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Blockinger.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Blockinger.

    Blockinger ist Freie Software: Sie k�nnen es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
    ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

    Blockinger wird in der Hoffnung, dass es n�tzlich sein wird, aber
    OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License f�r weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package org.xxnan.blockinger.game.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Button;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import net.youmi.android.nm.bn.BannerManager;
import net.youmi.android.nm.bn.BannerViewListener;

import org.xxnan.blockinger.game.BlockBoardView;
import org.xxnan.blockinger.game.R;
import org.xxnan.blockinger.game.WorkThread;
import org.xxnan.blockinger.game.components.Controls;
import org.xxnan.blockinger.game.components.Display;
import org.xxnan.blockinger.game.components.GameState;
import org.xxnan.blockinger.game.components.Sound;


public class GameActivity extends FragmentActivity {

	public Sound sound;
	public Controls controls;
	public Display display;
	public GameState game;
	private WorkThread mainThread;
	private DefeatDialogFragment dialog;
	private boolean layoutSwap;

	public static final int NEW_GAME = 0;
	public static final int RESUME_GAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_layoutswap", false)) {
			setContentView(R.layout.activity_game_alt);
			layoutSwap = true;
		} else {
			setContentView(R.layout.activity_game);
			layoutSwap = false;
		}

		/* Read Starting Arguments */
		Bundle b = getIntent().getExtras();
		int value = NEW_GAME;
		
		/* Create Components */
		game = (GameState)getLastCustomNonConfigurationInstance();
		if(game == null) {
			/* Check for Resuming (or Resumption?) */
			if(b!=null)
				value = b.getInt("mode");
				
			if((value == NEW_GAME)) {
				game = GameState.getNewInstance(this);
				game.setLevel(b.getInt("level"));
			} else
				game = GameState.getInstance(this);
		}
		game.reconnect(this);
		dialog = new DefeatDialogFragment();
		controls = new Controls(this);
		display = new Display(this);
		sound = new Sound(this);
		
		/* Init Components */
		if(game.isResumable())
			sound.startMusic(Sound.GAME_MUSIC, game.getSongtime());
		sound.loadEffects();
		if(b!=null){
			value = b.getInt("mode");
			if(b.getString("playername") != null)
				game.setPlayerName(b.getString("playername"));
		} else 
			game.setPlayerName(getResources().getString(R.string.anonymous));
		dialog.setCancelable(false);
		if(!game.isResumable())
			gameOver(game.getScore(), game.getTimeString(), game.getAPM());
		
		/* Register Button callback Methods */
		((Button)findViewById(R.id.pausebutton_1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//GameActivity.this.finish();
				game.setRunning(false);
			}
		});
		((BlockBoardView)findViewById(R.id.boardView)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	controls.boardPressed(event.getX(), event.getY());
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	controls.boardReleased();
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.rightButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	controls.rightButtonPressed();
		        	((ImageButton)findViewById(R.id.rightButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	controls.rightButtonReleased();
		        	((ImageButton)findViewById(R.id.rightButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.leftButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	controls.leftButtonPressed();
		        	((ImageButton)findViewById(R.id.leftButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	controls.leftButtonReleased();
		        	((ImageButton)findViewById(R.id.leftButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.softDropButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	controls.downButtonPressed();
		        	((ImageButton)findViewById(R.id.softDropButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	controls.downButtonReleased();
		        	((ImageButton)findViewById(R.id.softDropButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.hardDropButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	controls.dropButtonPressed();
		        	((ImageButton)findViewById(R.id.hardDropButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	controls.dropButtonReleased();
		        	((ImageButton)findViewById(R.id.hardDropButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.rotateRightButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	controls.rotateRightPressed();
		        	((ImageButton)findViewById(R.id.rotateRightButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	controls.rotateRightReleased();
		        	((ImageButton)findViewById(R.id.rotateRightButton)).setPressed(false);
		        }
		        return true;
		    }
		});
		((ImageButton)findViewById(R.id.rotateLeftButton)).setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	controls.rotateLeftPressed();
		        	((ImageButton)findViewById(R.id.rotateLeftButton)).setPressed(true);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	controls.rotateLeftReleased();
		        	((ImageButton)findViewById(R.id.rotateLeftButton)).setPressed(false);
		        }
		        return true;
		    }
		});

		((BlockBoardView)findViewById(R.id.boardView)).init();
		((BlockBoardView)findViewById(R.id.boardView)).setHost(this);
		setupBannerAd();
	}
	
	/**
	 * Called by BlockBoardView upon completed creation
	 * @param caller
	 */
	public void startGame(BlockBoardView caller){
		mainThread = new WorkThread(this, caller.getHolder()); 
		mainThread.setFirstTime(false);
		game.setRunning(true);
		mainThread.setRunning(true);
		mainThread.start();
	}

	/**
	 * Called by BlockBoardView upon destruction
	 */
	public void destroyWorkThread() {
        boolean retry = true;
        mainThread.setRunning(false);
        while (retry) {
            try {
            	mainThread.join();
                retry = false;
            } catch (InterruptedException e) {
                
            }
        }
	}
	
	/**
	 * Called by GameState upon Defeat
	 * @param score
	 */
	public void putScore(long score) {
		String playerName = game.getPlayerName();
		if(playerName == null || playerName.equals(""))
			playerName = getResources().getString(R.string.anonymous);//"Anonymous";
		
		Intent data = new Intent();
		data.putExtra(MainActivity.PLAYERNAME_KEY, playerName);
		data.putExtra(MainActivity.SCORE_KEY, score);
		setResult(MainActivity.RESULT_OK, data);
		
		finish();
	}
	
	@Override
	protected void onPause() {
    	super.onPause();
    	sound.pause();
    	sound.setInactive(true);
    	game.setRunning(false);
	};
    
    @Override
    protected void onStop() {
    	super.onStop();
    	sound.pause();
    	sound.setInactive(true);
    };
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	game.setSongtime(sound.getSongtime());
    	sound.release();
    	sound = null;
    	game.disconnect();
    };
    
    @Override
    protected void onResume() {
    	super.onResume();
    	sound.resume();
    	sound.setInactive(false);
    	
    	/* Check for changed Layout */
    	boolean tempswap = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_layoutswap", false);
		if(layoutSwap != tempswap) {
			layoutSwap = tempswap;
			if(layoutSwap) {
				setContentView(R.layout.activity_game_alt);
			} else {
				setContentView(R.layout.activity_game);
			}
		}
    	game.setRunning(true);
    };
    
    @Override
    public Object onRetainCustomNonConfigurationInstance () {
        return game;
    }
	
	public void gameOver(long score, String gameTime, int apm) {
		dialog.setData(score, gameTime, apm);
		dialog.show(getSupportFragmentManager(), "hamster");
	}
	/**
	 * 设置广告条广告
	 */
	private void setupBannerAd() {
		//		/**
		//		 * 普通布局
		//		 */
		//		// 获取广告条
		//		View bannerView = BannerManager.getInstance(mContext)
		//		                                        .getBannerView(mContext, new BannerViewListener() {
		//			                                        @Override
		//			                                        public void onRequestSuccess() {
		//				                                        logInfo("请求广告条成功");
		//			                                        }
		//
		//			                                        @Override
		//			                                        public void onSwitchBanner() {
		//				                                        logDebug("广告条切换");
		//			                                        }
		//
		//			                                        @Override
		//			                                        public void onRequestFailed() {
		//				                                        logError("请求广告条失败");
		//			                                        }
		//		                                        });
		//		// 实例化广告条容器
		//		LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.ll_main_banner);
		//		// 添加广告条到容器中
		//		bannerLayout.addView(bannerView);

		/**
		 * 悬浮布局
		 */
		// 实例化LayoutParams
		FrameLayout.LayoutParams layoutParams =
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		// 设置广告条的悬浮位置，这里示例为右下角
		layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		// 获取广告条
		final View bannerView = BannerManager.getInstance(this)
				.getBannerView(this, new BannerViewListener() {

					@Override
					public void onRequestSuccess() {
						//logInfo("请求广告条成功");

					}

					@Override
					public void onSwitchBanner() {

						//logDebug("广告条切换");
					}

					@Override
					public void onRequestFailed() {
						//logError("请求广告条失败");
					}
				});
		// 添加广告条到窗口中
		LinearLayout banner= (LinearLayout) findViewById(R.id.bannerView);
		banner.addView(bannerView, layoutParams);
	}
}
